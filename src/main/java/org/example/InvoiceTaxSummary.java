package org.example;

public class InvoiceTaxSummary {
    private Integer id;
    private Double ht;
    private Double tva;
    private Double ttc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getHt() {
        return ht;
    }

    public void setHt(Double ht) {
        this.ht = ht;
    }

    public Double getTva() {
        return tva;
    }

    public void setTva(Double tva) {
        this.tva = tva;
    }

    public Double getTtc() {
        return ttc;
    }

    public void setTtc(Double ttc) {
        this.ttc = ttc;
    }

    @Override
    public String toString() {
        return "InvoiceTaxSummary{" +
                "id=" + id +
                ", ht=" + ht +
                ", tva=" + tva +
                ", ttc=" + ttc +
                '}';
    }
}
