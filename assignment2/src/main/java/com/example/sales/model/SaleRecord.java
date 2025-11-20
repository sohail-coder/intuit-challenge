package com.example.sales.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class SaleRecord {

    private final String orderId;
    private final LocalDate date;
    private final String customerId;
    private final String region;
    private final String productCategory;
    private final String productName;
    private final int unitsSold;
    private final BigDecimal unitPrice;
    private final BigDecimal discount;
    private final String status;
    private final String paymentMethod;

    private SaleRecord(Builder builder) {
        this.orderId = builder.orderId;
        this.date = builder.date;
        this.customerId = builder.customerId;
        this.region = builder.region;
        this.productCategory = builder.productCategory;
        this.productName = builder.productName;
        this.unitsSold = builder.unitsSold;
        this.unitPrice = builder.unitPrice;
        this.discount = builder.discount;
        this.status = builder.status;
        this.paymentMethod = builder.paymentMethod;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String orderId() {
        return orderId;
    }

    public LocalDate date() {
        return date;
    }

    public String customerId() {
        return customerId;
    }

    public String region() {
        return region;
    }

    public String productCategory() {
        return productCategory;
    }

    public String productName() {
        return productName;
    }

    public int unitsSold() {
        return unitsSold;
    }

    public BigDecimal unitPrice() {
        return unitPrice;
    }

    public BigDecimal discount() {
        return discount;
    }

    public String status() {
        return status;
    }

    public String paymentMethod() {
        return paymentMethod;
    }

    public static final class Builder {
        private String orderId;
        private LocalDate date;
        private String customerId;
        private String region;
        private String productCategory;
        private String productName;
        private int unitsSold;
        private BigDecimal unitPrice = BigDecimal.ZERO;
        private BigDecimal discount = BigDecimal.ZERO;
        private String status;
        private String paymentMethod;

        private Builder() {
        }

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder productCategory(String productCategory) {
            this.productCategory = productCategory;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder unitsSold(int unitsSold) {
            this.unitsSold = unitsSold;
            return this;
        }

        public Builder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder discount(BigDecimal discount) {
            this.discount = discount;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public SaleRecord build() {
            Objects.requireNonNull(orderId, "orderId");
            Objects.requireNonNull(date, "date");
            Objects.requireNonNull(customerId, "customerId");
            Objects.requireNonNull(region, "region");
            Objects.requireNonNull(productCategory, "productCategory");
            Objects.requireNonNull(productName, "productName");
            Objects.requireNonNull(unitPrice, "unitPrice");
            Objects.requireNonNull(discount, "discount");
            Objects.requireNonNull(status, "status");
            Objects.requireNonNull(paymentMethod, "paymentMethod");
            return new SaleRecord(this);
        }
    }
}
