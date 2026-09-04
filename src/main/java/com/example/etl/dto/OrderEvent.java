package com.example.etl.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Raw order event as it is extracted from the source system (or simulated).
 * This is the payload published to the "orders-raw" Kafka topic.
 */
public class OrderEvent {

    private String orderId;
    private String customerId;
    private String customerCountry;
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPriceUsd;
    private Instant createdAt;

    public OrderEvent() {
    }

    public OrderEvent(String orderId, String customerId, String customerCountry, String productId,
                       String productName, int quantity, BigDecimal unitPriceUsd, Instant createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerCountry = customerCountry;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPriceUsd = unitPriceUsd;
        this.createdAt = createdAt;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public BigDecimal getUnitPriceUsd() {
        return unitPriceUsd;
    }

    public void setUnitPriceUsd(BigDecimal unitPriceUsd) {
        this.unitPriceUsd = unitPriceUsd;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "OrderEvent{" +
                "orderId='" + orderId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerCountry='" + customerCountry + '\'' +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPriceUsd=" + unitPriceUsd +
                ", createdAt=" + createdAt +
                '}';
    }
}
