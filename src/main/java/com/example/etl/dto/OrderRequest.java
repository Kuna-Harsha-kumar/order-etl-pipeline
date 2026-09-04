package com.example.etl.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class OrderRequest {

    @NotBlank
    private String customerId;

    @NotBlank
    @Size(min = 2, max = 2)
    private String customerCountry;

    @NotBlank
    private String productId;

    @NotBlank
    private String productName;

    @Min(1)
    private int quantity;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal unitPriceUsd;

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
}
