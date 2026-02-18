package org.example;

import java.sql.SQLException;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        DataRetriever  dataRetriever = new DataRetriever();
//        List<InvoiceTotal> tota = dataRetriever.findConfirmedAndPaidInvoiceTotals();
//        for (InvoiceTotal invoiceTotal : tota) {
//            System.out.println(invoiceTotal);
//        }

//        System.out.println(dataRetriever.computeStatusTotals());

//        System.out.println(dataRetriever.computeWeightedTurnover());

//        System.out.println(dataRetriever.findInvoiceTaxSummaries());

        System.out.println(dataRetriever.computeWeightedTurnoverTtc());
    }


}