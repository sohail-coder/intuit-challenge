package com.example.sales.analysis;

import com.example.sales.model.SaleRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Sales Analyzer Tests")
public class SalesAnalyzerTest {

    private SalesAnalyzer analyzer;
    private List<SaleRecord> sampleRecords;

    @BeforeEach
    void setUp() {
        analyzer = new SalesAnalyzer();
        sampleRecords = createSampleRecords();
    }

    @Test
    @DisplayName("Total revenue by region aggregates correctly with discounts")
    void totalRevenueByRegionAggregatesValues() {
        Map<String, BigDecimal> result = analyzer.totalRevenueByRegion(sampleRecords);

        assertThat(result.get("North")).isEqualByComparingTo("200.00");
        assertThat(result.get("South")).isEqualByComparingTo("180.00");
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Total revenue by region filters out non-completed sales")
    void totalRevenueByRegionFiltersNonCompleted() {
        List<SaleRecord> recordsWithPending = List.of(
                createRecord("1", "North", "Electronics", "TV", 1, "100", "0.0", "Pending"),
                createRecord("2", "North", "Electronics", "TV", 2, "100", "0.0", "Completed")
        );

        Map<String, BigDecimal> result = analyzer.totalRevenueByRegion(recordsWithPending);

        assertThat(result.get("North")).isEqualByComparingTo("200.00");
    }

    @Test
    @DisplayName("Top products by revenue returns correct order and limit")
    void topProductsByRevenueReturnsCorrectOrder() {
        List<SalesAnalyzer.ProductRevenue> top2 = analyzer.topProductsByRevenue(sampleRecords, 2);

        assertThat(top2).hasSize(2);
        assertThat(top2.get(0).productName()).isEqualTo("Widget");
        assertThat(top2.get(0).revenue()).isEqualByComparingTo("200.00");
        assertThat(top2.get(1).productName()).isEqualTo("Gadget");
        assertThat(top2.get(1).revenue()).isEqualByComparingTo("180.00");
    }

    @Test
    @DisplayName("Top products handles limit larger than available products")
    void topProductsHandlesLargeLimitGracefully() {
        List<SalesAnalyzer.ProductRevenue> top10 = analyzer.topProductsByRevenue(sampleRecords, 10);

        assertThat(top10).hasSize(2);
    }

    @Test
    @DisplayName("Sales count by category groups correctly")
    void salesCountByCategoryGroupsCorrectly() {
        Map<String, Long> result = analyzer.salesCountByCategory(sampleRecords);

        assertThat(result.get("Electronics")).isEqualTo(1L);
        assertThat(result.get("Home")).isEqualTo(1L);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Average order value by region calculates correctly")
    void averageOrderValueByRegionCalculatesCorrectly() {
        List<SaleRecord> records = List.of(
                createRecord("1", "North", "Electronics", "TV", 2, "100", "0.0", "Completed"),
                createRecord("2", "North", "Electronics", "Phone", 1, "400", "0.0", "Completed")
        );

        Map<String, BigDecimal> result = analyzer.averageOrderValueByRegion(records);

        assertThat(result.get("North")).isEqualByComparingTo("300.00");
    }

    @Test
    @DisplayName("Monthly revenue trend groups by year-month")
    void monthlyRevenueTrendGroupsByYearMonth() {
        List<SaleRecord> records = List.of(
                createRecordWithDate("1", "North", "2024-01-15", "TV", 2, "100", "0.0"),
                createRecordWithDate("2", "North", "2024-01-20", "Phone", 1, "200", "0.0"),
                createRecordWithDate("3", "South", "2024-02-10", "Tablet", 3, "100", "0.0")
        );

        Map<YearMonth, BigDecimal> result = analyzer.monthlyRevenueTrend(records);

        assertThat(result.get(YearMonth.of(2024, 1))).isEqualByComparingTo("400.00");
        assertThat(result.get(YearMonth.of(2024, 2))).isEqualByComparingTo("300.00");
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Top customers by spending returns correct order")
    void topCustomersBySpendingReturnsCorrectOrder() {
        List<SaleRecord> records = List.of(
                createRecordWithCustomer("1", "C001", "North", "TV", 5, "100", "0.0"),
                createRecordWithCustomer("2", "C002", "South", "Phone", 2, "200", "0.0"),
                createRecordWithCustomer("3", "C001", "North", "Tablet", 1, "100", "0.0")
        );

        List<SalesAnalyzer.CustomerSpending> top2 = analyzer.topCustomersBySpending(records, 2);

        assertThat(top2).hasSize(2);
        assertThat(top2.get(0).customerId()).isEqualTo("C001");
        assertThat(top2.get(0).totalSpent()).isEqualByComparingTo("600.00");
        assertThat(top2.get(1).customerId()).isEqualTo("C002");
        assertThat(top2.get(1).totalSpent()).isEqualByComparingTo("400.00");
    }

    @Test
    @DisplayName("Payment method distribution counts correctly")
    void paymentMethodDistributionCountsCorrectly() {
        List<SaleRecord> records = List.of(
                createRecordWithPayment("1", "North", "TV", "Cash"),
                createRecordWithPayment("2", "South", "Phone", "Card"),
                createRecordWithPayment("3", "East", "Tablet", "Cash")
        );

        Map<String, Long> result = analyzer.paymentMethodDistribution(records);

        assertThat(result.get("Cash")).isEqualTo(2L);
        assertThat(result.get("Card")).isEqualTo(1L);
    }

    @Test
    @DisplayName("Discount impact by category calculates statistics correctly")
    void discountImpactByCategoryCalculatesStats() {
        List<SaleRecord> records = List.of(
                createRecord("1", "North", "Electronics", "TV", 2, "100", "0.10", "Completed"),
                createRecord("2", "North", "Electronics", "Phone", 1, "200", "0.20", "Completed")
        );

        Map<String, SalesAnalyzer.DiscountStats> result = analyzer.discountImpactByCategory(records);

        SalesAnalyzer.DiscountStats stats = result.get("Electronics");
        assertThat(stats.salesCount()).isEqualTo(2L);
        assertThat(stats.avgDiscount()).isEqualByComparingTo("0.1500");
        assertThat(stats.totalRevenue()).isEqualByComparingTo("340.00");
    }

    @Test
    @DisplayName("Revenue by category aggregates correctly")
    void revenueByCategoryAggregatesCorrectly() {
        Map<String, BigDecimal> result = analyzer.revenueByCategory(sampleRecords);

        assertThat(result.get("Electronics")).isEqualByComparingTo("200.00");
        assertThat(result.get("Home")).isEqualByComparingTo("180.00");
    }

    @Test
    @DisplayName("Completed sales only filters correctly")
    void completedSalesOnlyFiltersCorrectly() {
        List<SaleRecord> records = List.of(
                createRecord("1", "North", "Electronics", "TV", 1, "100", "0.0", "Completed"),
                createRecord("2", "South", "Home", "Chair", 1, "200", "0.0", "Pending"),
                createRecord("3", "East", "Electronics", "Phone", 1, "300", "0.0", "Returned")
        );

        List<SaleRecord> completed = analyzer.completedSalesOnly(records);

        assertThat(completed).hasSize(1);
        assertThat(completed.get(0).orderId()).isEqualTo("1");
    }

    @Test
    @DisplayName("Total units sold by region sums correctly")
    void totalUnitsSoldByRegionSumsCorrectly() {
        List<SaleRecord> records = List.of(
                createRecord("1", "North", "Electronics", "TV", 5, "100", "0.0", "Completed"),
                createRecord("2", "North", "Electronics", "Phone", 3, "200", "0.0", "Completed"),
                createRecord("3", "South", "Home", "Chair", 10, "150", "0.0", "Completed")
        );

        Map<String, Integer> result = analyzer.totalUnitsSoldByRegion(records);

        assertThat(result.get("North")).isEqualTo(8);
        assertThat(result.get("South")).isEqualTo(10);
    }

    @Test
    @DisplayName("Empty list returns empty results")
    void emptyListReturnsEmptyResults() {
        List<SaleRecord> emptyList = List.of();

        assertThat(analyzer.totalRevenueByRegion(emptyList)).isEmpty();
        assertThat(analyzer.salesCountByCategory(emptyList)).isEmpty();
        assertThat(analyzer.topProductsByRevenue(emptyList, 5)).isEmpty();
        assertThat(analyzer.paymentMethodDistribution(emptyList)).isEmpty();
    }

    @Test
    @DisplayName("Single record produces correct results")
    void singleRecordProducesCorrectResults() {
        List<SaleRecord> singleRecord = List.of(
                createRecord("1", "North", "Electronics", "TV", 2, "100", "0.10", "Completed")
        );

        Map<String, BigDecimal> revenue = analyzer.totalRevenueByRegion(singleRecord);
        assertThat(revenue.get("North")).isEqualByComparingTo("180.00");

        List<SalesAnalyzer.ProductRevenue> topProducts = analyzer.topProductsByRevenue(singleRecord, 5);
        assertThat(topProducts).hasSize(1);
        assertThat(topProducts.get(0).productName()).isEqualTo("TV");
    }

    @Test
    @DisplayName("Zero limit returns empty list")
    void zeroLimitReturnsEmptyList() {
        List<SalesAnalyzer.ProductRevenue> topProducts = analyzer.topProductsByRevenue(sampleRecords, 0);
        assertThat(topProducts).isEmpty();

        List<SalesAnalyzer.CustomerSpending> topCustomers = analyzer.topCustomersBySpending(sampleRecords, 0);
        assertThat(topCustomers).isEmpty();
    }

    @Test
    @DisplayName("Handles 100% discount correctly")
    void handlesFullDiscountCorrectly() {
        List<SaleRecord> records = List.of(
                createRecord("1", "North", "Electronics", "TV", 1, "100", "1.0", "Completed")
        );

        Map<String, BigDecimal> revenue = analyzer.totalRevenueByRegion(records);
        assertThat(revenue.get("North")).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Handles zero price correctly")
    void handlesZeroPriceCorrectly() {
        List<SaleRecord> records = List.of(
                createRecord("1", "North", "Electronics", "Free Item", 1, "0", "0.0", "Completed")
        );

        Map<String, BigDecimal> revenue = analyzer.totalRevenueByRegion(records);
        assertThat(revenue.get("North")).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Handles large quantities correctly")
    void handlesLargeQuantitiesCorrectly() {
        List<SaleRecord> records = List.of(
                createRecord("1", "North", "Electronics", "Item", 10000, "10", "0.0", "Completed")
        );

        Map<String, Integer> units = analyzer.totalUnitsSoldByRegion(records);
        assertThat(units.get("North")).isEqualTo(10000);
    }

    private List<SaleRecord> createSampleRecords() {
        return List.of(
                createRecord("1", "North", "Electronics", "Widget", 2, "100", "0.0", "Completed"),
                createRecord("2", "South", "Home", "Gadget", 1, "200", "0.10", "Completed")
        );
    }

    private SaleRecord createRecord(String orderId, String region, String category,
                                     String productName, int units, String price,
                                     String discount, String status) {
        return SaleRecord.builder()
                .orderId(orderId)
                .date(LocalDate.now())
                .customerId("C" + orderId)
                .region(region)
                .productCategory(category)
                .productName(productName)
                .unitsSold(units)
                .unitPrice(new BigDecimal(price))
                .discount(new BigDecimal(discount))
                .status(status)
                .paymentMethod("Cash")
                .build();
    }

    private SaleRecord createRecordWithDate(String orderId, String region, String date,
                                             String productName, int units, String price, String discount) {
        return SaleRecord.builder()
                .orderId(orderId)
                .date(LocalDate.parse(date))
                .customerId("C" + orderId)
                .region(region)
                .productCategory("Electronics")
                .productName(productName)
                .unitsSold(units)
                .unitPrice(new BigDecimal(price))
                .discount(new BigDecimal(discount))
                .status("Completed")
                .paymentMethod("Cash")
                .build();
    }

    private SaleRecord createRecordWithCustomer(String orderId, String customerId, String region,
                                                  String productName, int units, String price, String discount) {
        return SaleRecord.builder()
                .orderId(orderId)
                .date(LocalDate.now())
                .customerId(customerId)
                .region(region)
                .productCategory("Electronics")
                .productName(productName)
                .unitsSold(units)
                .unitPrice(new BigDecimal(price))
                .discount(new BigDecimal(discount))
                .status("Completed")
                .paymentMethod("Cash")
                .build();
    }

    private SaleRecord createRecordWithPayment(String orderId, String region,
                                                String productName, String paymentMethod) {
        return SaleRecord.builder()
                .orderId(orderId)
                .date(LocalDate.now())
                .customerId("C" + orderId)
                .region(region)
                .productCategory("Electronics")
                .productName(productName)
                .unitsSold(1)
                .unitPrice(new BigDecimal("100"))
                .discount(BigDecimal.ZERO)
                .status("Completed")
                .paymentMethod(paymentMethod)
                .build();
    }
}
