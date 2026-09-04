package com.example.etl.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "processed_orders", indexes = {
        @Index(name = "idx_processed_orders_customer", columnList = "customerId"),
        @Index(name = "idx_processed_orders_created", columnList = "orderCreatedAt")
})
public class ProcessedOrder {

    @Id
    @Column(nullable = false, updatable = false, length = 64)
    private String orderId;

    @Column(nullable = false, length = 64)
    private String customerId;

    @Column(nullable = false, length = 8)
    private String customerCountry;

    @Column(nullable = false, length = 16)
    private String customerTier;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPriceUsd;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPriceLocal;

    @Column(nullable = false, length = 8)
    private String localCurrency;

    @Column(nullable = false)
    private Instant orderCreatedAt;

    @Column(nullable = false)
    private Instant processedAt;

    public ProcessedOrder() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCountry() {
        return customerCountry;
    }

    public void setCustomerCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }

    public String getCustomerTier() {
        return customerTier;
    }

    public void setCustomerTier(String customerTier) {
        this.customerTier = customerTier;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPriceUsd() {
        return totalPriceUsd;
    }

    public void setTotalPriceUsd(BigDecimal totalPriceUsd) {
        this.totalPriceUsd = totalPriceUsd;
    }

    public BigDecimal getTotalPriceLocal() {
        return totalPriceLocal;
    }

    public void setTotalPriceLocal(BigDecimal totalPriceLocal) {
        this.totalPriceLocal = totalPriceLocal;
    }

    public String getLocalCurrency() {
        return localCurrency;
    }

    public void setLocalCurrency(String localCurrency) {
        this.localCurrency = localCurrency;
    }

    public Instant getOrderCreatedAt() {
        return orderCreatedAt;
    }

    public void setOrderCreatedAt(Instant orderCreatedAt) {
        this.orderCreatedAt = orderCreatedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}
