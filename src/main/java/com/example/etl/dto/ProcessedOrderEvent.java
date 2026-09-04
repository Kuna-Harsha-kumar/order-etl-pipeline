package com.example.etl.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Enriched/transformed order, published to "orders-processed" for downstream
 * consumers (e.g. a reporting service, another pipeline stage, etc.).
 */
public class ProcessedOrderEvent {

    private String orderId;
    private String customerId;
    private String customerCountry;
    private String customerTier;
    private String productName;
    private int quantity;
    private BigDecimal totalPriceUsd;
    private BigDecimal totalPriceLocal;
    private String localCurrency;
    private Instant orderCreatedAt;
    private Instant processedAt;

    public ProcessedOrderEvent() {
    }

    public ProcessedOrderEvent(String orderId, String customerId, String customerCountry, String customerTier,
                                String productName, int quantity, BigDecimal totalPriceUsd,
                                BigDecimal totalPriceLocal, String localCurrency,
                                Instant orderCreatedAt, Instant processedAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerCountry = customerCountry;
        this.customerTier = customerTier;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPriceUsd = totalPriceUsd;
        this.totalPriceLocal = totalPriceLocal;
        this.localCurrency = localCurrency;
        this.orderCreatedAt = orderCreatedAt;
        this.processedAt = processedAt;
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
