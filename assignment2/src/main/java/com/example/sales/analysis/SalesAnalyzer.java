package com.example.sales.analysis;

import com.example.sales.model.SaleRecord;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public final class SalesAnalyzer {

    public Map<String, BigDecimal> totalRevenueByRegion(List<SaleRecord> records) {
        return records.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.status()))
                .collect(Collectors.groupingBy(
                        SaleRecord::region,
                        Collectors.mapping(this::netRevenue,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    public List<ProductRevenue> topProductsByRevenue(List<SaleRecord> records, int limit) {
        return records.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.status()))
                .collect(Collectors.groupingBy(
                        SaleRecord::productName,
                        Collectors.mapping(this::netRevenue,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .map(e -> new ProductRevenue(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(ProductRevenue::revenue).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Map<String, Long> salesCountByCategory(List<SaleRecord> records) {
        return records.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.status()))
                .collect(Collectors.groupingBy(
                        SaleRecord::productCategory,
                        Collectors.counting()
                ));
    }

    public Map<String, BigDecimal> averageOrderValueByRegion(List<SaleRecord> records) {
        return records.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.status()))
                .collect(Collectors.groupingBy(
                        SaleRecord::region,
                        Collectors.collectingAndThen(
                                Collectors.averagingDouble(r -> netRevenue(r).doubleValue()),
                                avg -> BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP)
                        )
                ));
    }

    public Map<YearMonth, BigDecimal> monthlyRevenueTrend(List<SaleRecord> records) {
        return records.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.status()))
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.date()),
                        TreeMap::new,
                        Collectors.mapping(this::netRevenue,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    public List<CustomerSpending> topCustomersBySpending(List<SaleRecord> records, int limit) {
        return records.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.status()))
                .collect(Collectors.groupingBy(
                        SaleRecord::customerId,
                        Collectors.mapping(this::netRevenue,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .map(e -> new CustomerSpending(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(CustomerSpending::totalSpent).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Map<String, Long> paymentMethodDistribution(List<SaleRecord> records) {
        return records.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.status()))
                .collect(Collectors.groupingBy(
                        SaleRecord::paymentMethod,
                        Collectors.counting()
                ));
    }

    public Map<String, DiscountStats> discountImpactByCategory(List<SaleRecord> records) {
        return records.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.status()))
                .collect(Collectors.groupingBy(
                        SaleRecord::productCategory,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                this::calculateDiscountStats
                        )
                ));
    }

    public Map<String, BigDecimal> revenueByCategory(List<SaleRecord> records) {
        return records.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.status()))
                .collect(Collectors.groupingBy(
                        SaleRecord::productCategory,
                        Collectors.mapping(this::netRevenue,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    public List<SaleRecord> completedSalesOnly(List<SaleRecord> records) {
        return records.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.status()))
                .collect(Collectors.toList());
    }

    public Map<String, Integer> totalUnitsSoldByRegion(List<SaleRecord> records) {
        return records.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.status()))
                .collect(Collectors.groupingBy(
                        SaleRecord::region,
                        Collectors.summingInt(SaleRecord::unitsSold)
                ));
    }

    private BigDecimal netRevenue(SaleRecord record) {
        BigDecimal gross = record.unitPrice().multiply(BigDecimal.valueOf(record.unitsSold()));
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(record.discount());
        return gross.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private DiscountStats calculateDiscountStats(List<SaleRecord> records) {
        if (records.isEmpty()) {
            return new DiscountStats(0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        long count = records.size();
        BigDecimal totalRevenue = records.stream()
                .map(this::netRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal avgDiscount = records.stream()
                .map(SaleRecord::discount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);

        BigDecimal totalDiscountAmount = records.stream()
                .map(r -> {
                    BigDecimal gross = r.unitPrice().multiply(BigDecimal.valueOf(r.unitsSold()));
                    return gross.multiply(r.discount());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DiscountStats(count, avgDiscount, totalDiscountAmount, totalRevenue);
    }

    public record ProductRevenue(String productName, BigDecimal revenue) {
        @Override
        public String toString() {
            return String.format("%s: $%,.2f", productName, revenue);
        }
    }

    public record CustomerSpending(String customerId, BigDecimal totalSpent) {
        @Override
        public String toString() {
            return String.format("%s: $%,.2f", customerId, totalSpent);
        }
    }

    public record DiscountStats(
            long salesCount,
            BigDecimal avgDiscount,
            BigDecimal totalDiscountAmount,
            BigDecimal totalRevenue
    ) {
        @Override
        public String toString() {
            return String.format(
                    "Sales: %d, Avg Discount: %.2f%%, Total Discount Amount: $%,.2f, Revenue: $%,.2f",
                    salesCount,
                    avgDiscount.multiply(BigDecimal.valueOf(100)),
                    totalDiscountAmount,
                    totalRevenue
            );
        }
    }
}
