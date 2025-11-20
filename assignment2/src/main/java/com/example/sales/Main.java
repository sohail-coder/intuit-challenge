package com.example.sales;

import com.example.sales.analysis.SalesAnalyzer;
import com.example.sales.csv.SaleRecordLoader;
import com.example.sales.model.SaleRecord;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("     SALES DATA ANALYSIS - Functional Programming Demo");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        Path csvPath = Path.of("data", "sales_data.csv");
        SaleRecordLoader loader = new SaleRecordLoader(csvPath);
        List<SaleRecord> records = loader.load();

        System.out.printf("✓ Loaded %d sales records from CSV%n%n", records.size());

        SalesAnalyzer analyzer = new SalesAnalyzer();

        printSectionHeader("1. Total Revenue by Region");
        Map<String, BigDecimal> revenueByRegion = analyzer.totalRevenueByRegion(records);
        revenueByRegion.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .forEach(e -> System.out.printf("   %-20s $%,12.2f%n", e.getKey(), e.getValue()));

        printSectionHeader("2. Top 5 Products by Revenue");
        analyzer.topProductsByRevenue(records, 5)
                .forEach(p -> System.out.printf("   %-30s $%,12.2f%n", p.productName(), p.revenue()));

        printSectionHeader("3. Sales Count by Product Category");
        Map<String, Long> salesByCategory = analyzer.salesCountByCategory(records);
        salesByCategory.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.printf("   %-20s %5d sales%n", e.getKey(), e.getValue()));

        printSectionHeader("4. Average Order Value (AOV) by Region");
        Map<String, BigDecimal> aovByRegion = analyzer.averageOrderValueByRegion(records);
        aovByRegion.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .forEach(e -> System.out.printf("   %-20s $%,12.2f%n", e.getKey(), e.getValue()));

        printSectionHeader("5. Monthly Revenue Trend");
        Map<YearMonth, BigDecimal> monthlyTrend = analyzer.monthlyRevenueTrend(records);
        monthlyTrend.forEach((month, revenue) ->
                System.out.printf("   %s: $%,12.2f%n", month, revenue));

        printSectionHeader("6. Top 5 Customers by Total Spending");
        analyzer.topCustomersBySpending(records, 5)
                .forEach(c -> System.out.printf("   %-15s $%,12.2f%n", c.customerId(), c.totalSpent()));

        printSectionHeader("7. Payment Method Distribution");
        Map<String, Long> paymentMethods = analyzer.paymentMethodDistribution(records);
        long totalTransactions = paymentMethods.values().stream().mapToLong(Long::longValue).sum();
        paymentMethods.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> {
                    double percentage = (e.getValue() * 100.0) / totalTransactions;
                    System.out.printf("   %-20s %5d transactions (%.1f%%)%n",
                            e.getKey(), e.getValue(), percentage);
                });

        printSectionHeader("8. Discount Impact Analysis by Category");
        Map<String, SalesAnalyzer.DiscountStats> discountStats = analyzer.discountImpactByCategory(records);
        discountStats.forEach((category, stats) ->
                System.out.printf("   %-15s: %s%n", category, stats));

        printSectionHeader("9. Total Revenue by Product Category");
        Map<String, BigDecimal> revenueByCategory = analyzer.revenueByCategory(records);
        revenueByCategory.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .forEach(e -> System.out.printf("   %-20s $%,12.2f%n", e.getKey(), e.getValue()));

        printSectionHeader("10. Total Units Sold by Region");
        Map<String, Integer> unitsByRegion = analyzer.totalUnitsSoldByRegion(records);
        unitsByRegion.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> System.out.printf("   %-20s %,8d units%n", e.getKey(), e.getValue()));

        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("     Analysis Complete - All Objectives Demonstrated");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("\nTesting Objectives Covered:");
        System.out.println("  ✓ Functional Programming - Pure functions, immutable data");
        System.out.println("  ✓ Stream Operations - filter, map, reduce, collect");
        System.out.println("  ✓ Data Aggregation - groupingBy, reducing, counting");
        System.out.println("  ✓ Lambda Expressions - Used throughout all analyses");
        System.out.println();
    }

    private static void printSectionHeader(String title) {
        System.out.println("\n───────────────────────────────────────────────────────────");
        System.out.println(title);
        System.out.println("───────────────────────────────────────────────────────────");
    }
}
