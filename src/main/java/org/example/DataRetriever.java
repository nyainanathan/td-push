package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    List<InvoiceTotal> findInvoiceTotals() throws SQLException {

        List<InvoiceTotal> invoiceTotals = new ArrayList<>();

        String query = """
                SELECT i.id, i.customer_name, SUM(il.quantity * il.unit_price) AS amount FROM invoice i
                    JOIN invoice_line il ON i.id = il.invoice_id
                GROUP BY i.id, i.customer_name
                ORDER BY i.id;
                """;
        try(Connection conn = new DBConnection().getConnection()){
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                InvoiceTotal invoiceTotal = new InvoiceTotal();
                invoiceTotal.setInvoiceId(rs.getInt("id"));
                invoiceTotal.setCustomerName(rs.getString("customer_name"));
                invoiceTotal.setAmount(rs.getDouble("amount"));
                invoiceTotals.add(invoiceTotal);
            }
        } catch (Exception e){
            e.printStackTrace();
            throw  new SQLException(e);
        }

        return invoiceTotals;
    }

    List<InvoiceTotal> findConfirmedAndPaidInvoiceTotals() throws SQLException {
        List<InvoiceTotal> invoiceTotals = new ArrayList<>();

        String query = """
        SELECT i.id , i.customer_name, i.status, SUM(il.quantity * il.unit_price) AS amount FROM invoice i
        JOIN invoice_line il ON i.id = il.invoice_id
        WHERE i.status = 'CONFIRMED' OR i.status = 'PAID'
        GROUP BY i.status, i.customer_name, i.id;
                """;
        try(Connection conn = new DBConnection().getConnection()){
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                InvoiceTotal invoiceTotal = new InvoiceTotal();
                invoiceTotal.setInvoiceId(rs.getInt("id"));
                invoiceTotal.setCustomerName(rs.getString("customer_name"));
                invoiceTotal.setAmount(rs.getDouble("amount"));
                invoiceTotals.add(invoiceTotal);
            }
        } catch (Exception e){
            e.printStackTrace();
            throw  new SQLException(e);
        }

        return invoiceTotals;

    }
    InvoiceStatusTotals computeStatusTotals(){
        InvoiceStatusTotals total  = new InvoiceStatusTotals();

        String query = """
                SELECT  i.status, SUM(il.quantity * il.unit_price) AS AMOUNT FROM invoice i
                JOIN invoice_line il ON i.id = il.invoice_id
                GROUP BY i.status;
                """;
        try(Connection conn = new DBConnection().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                if(rs.getString("status").equals("CONFIRMED")){

                    total.setTotalConfirmed(rs.getDouble("amount"));

                } else if (rs.getString("status").equals("PAID")){

                    total.setTotalPaid(rs.getDouble("amount"));

                } else if (rs.getString("status").equals("DRAFT")){

                    total.setTotalDraft(rs.getDouble("amount"));

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return total;
    };

    Double computeWeightedTurnover() throws SQLException {
        Double total = 0d;

        String query = """
                SELECT SUM(
                    CASE
                        WHEN i.status = 'PAID' THEN  il.quantity * il.unit_price
                       WHEN i.status = 'CONFIRMED' THEN (il.quantity * il.unit_price) / 2
                       ELSE 0
                    END
                ) AS chiffre_d_affaire FROM invoice i
                JOIN invoice_line il ON i.id = il.invoice_id;
                """;

        try(Connection conn = new DBConnection().getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                total = rs.getDouble("chiffre_d_affaire");
            }
           }

        return total;
    }
}
