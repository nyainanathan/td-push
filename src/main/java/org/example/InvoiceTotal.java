package org.example;

public class InvoiceTotal {
    private Integer invoiceId;
    private String customerName;
    private Double amount;

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "InvoiceTotal{" +
                "invoiceId=" + invoiceId +
                ", customerName='" + customerName + '\'' +
                ", amount=" + amount +
                '}';
    }
}
