package com.shoppoc.order.infrastructure.persistence;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class JpaOrderEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String status;
    private String paymentId;
    private String paymentReference;
    private String paymentStatus;
    private String paymentRejectionReason;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String totalCurrency;

    @Column(nullable = false)
    private Instant createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<JpaOrderLineEntity> lines = new ArrayList<JpaOrderLineEntity>();

    protected JpaOrderEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentRejectionReason() {
        return paymentRejectionReason;
    }

    public void setPaymentRejectionReason(String paymentRejectionReason) {
        this.paymentRejectionReason = paymentRejectionReason;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTotalCurrency() {
        return totalCurrency;
    }

    public void setTotalCurrency(String totalCurrency) {
        this.totalCurrency = totalCurrency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<JpaOrderLineEntity> getLines() {
        return lines;
    }

    public void setLines(List<JpaOrderLineEntity> lines) {
        this.lines = lines;
    }
}
