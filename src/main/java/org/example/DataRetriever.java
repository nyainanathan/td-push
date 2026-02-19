package org.example;

import java.math.BigDecimal;
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
        try(
                Connection conn = new DBConnection().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()
        ){
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
        try(
            Connection conn = new DBConnection().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
        ){
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
                SELECT SUM((case when i.status = 'PAID' THEN il.quantity * il.unit_price else 0 end)) as paid_amount,
                       SUM((case when i.status = 'CONFIRMED' THEN il.quantity * il.unit_price else 0 end)) as confirmed_amount,
                       SUM((case when i.status = 'DRAFT' THEN il.quantity * il.unit_price else 0 end)) as draft_amount
                FROM invoice i
                JOIN invoice_line il ON i.id = il.invoice_id;
                """;
        try(
                Connection conn = new DBConnection().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
        ) {


            while(rs.next()){

                    total.setTotalConfirmed(rs.getDouble("confirmed_amount"));


                    total.setTotalPaid(rs.getDouble("paid_amount"));


                    total.setTotalDraft(rs.getDouble("draft_amount"));

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

        try(
                Connection conn = new DBConnection().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
        ){


            if(rs.next()){
                total = rs.getDouble("chiffre_d_affaire");
            }
           }

        return total;
    }

    List<InvoiceTaxSummary> findInvoiceTaxSummaries() throws SQLException {

        List<InvoiceTaxSummary> invoiceTaxSummaries = new ArrayList<>();

        String query = """
                SELECT i.id,
                       SUM(il.quantity * il.unit_price) AS HT,
                       t.rate * SUM(il.quantity * il.unit_price)  / 100 AS TVA,
                       SUM(il.quantity * il.unit_price) + (( t.rate * SUM(il.quantity * il.unit_price) ) / 100) AS TTC
                FROM invoice i
                JOIN invoice_line il ON i.id = il.invoice_id
                JOIN tax_config t ON 1 = 1
                GROUP BY i.id, t.rate
                ORDER BY i.id;
                """;

        try(
                Connection conn = new DBConnection().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                InvoiceTaxSummary invoiceTaxSummary = new InvoiceTaxSummary();
                invoiceTaxSummary.setId(rs.getInt("id"));
                invoiceTaxSummary.setHt(rs.getDouble("ht"));
                invoiceTaxSummary.setTva(rs.getDouble("tva"));
                invoiceTaxSummary.setTtc(rs.getDouble("ttc"));;
                invoiceTaxSummaries.add(invoiceTaxSummary);
            }
        }

        return invoiceTaxSummaries;
    };

    BigDecimal computeWeightedTurnoverTtc() throws SQLException {

        BigDecimal total = BigDecimal.ZERO;

        String query = """
                SELECT SUM (
                    CASE
                            WHEN i.status = 'PAID' THEN (il.quantity * il.unit_price) + (( t.rate * (il.quantity * il.unit_price) ) / 100)
                            WHEN i.status = 'CONFIRMED' THEN  ((il.quantity * il.unit_price) + (( t.rate * (il.quantity * il.unit_price) ) / 100) ) / 2
                            ELSE 0
                       END
                       ) AS chiffre_d_affaire_TTC
                FROM invoice i
                         JOIN invoice_line il ON i.id = il.invoice_id
                         JOIN tax_config t ON 1 = 1
                WHERE i.status != 'DRAFT'
                GROUP BY  t.rate
                """;

        try(
                Connection conn = new DBConnection().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
        ){
            if(rs.next()){
                total = rs.getBigDecimal("chiffre_d_affaire_TTC");
            }
        }

        return total;
    }
}
