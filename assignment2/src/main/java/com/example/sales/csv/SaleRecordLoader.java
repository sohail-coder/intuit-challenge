package com.example.sales.csv;

import com.example.sales.model.SaleRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.logging.Logger;

public final class SaleRecordLoader {

    private static final Logger LOGGER = Logger.getLogger(SaleRecordLoader.class.getName());

    private final Path csvPath;

    public SaleRecordLoader(Path csvPath) {
        this.csvPath = csvPath;
    }

    public List<SaleRecord> load() throws IOException {
        try (Reader reader = Files.newBufferedReader(csvPath);
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {
            return parser.getRecords().stream()
                    .map(this::toSaleRecordSafely)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());
        }
    }

    private Optional<SaleRecord> toSaleRecordSafely(CSVRecord record) {
        try {
            return Optional.of(toSaleRecord(record));
        } catch (RuntimeException ex) {
            LOGGER.warning(() -> "Skipping invalid row with order_id=" + record.get("order_id") + ": " + ex.getMessage());
            return Optional.empty();
        }
    }

    private SaleRecord toSaleRecord(CSVRecord record) {
        return SaleRecord.builder()
                .orderId(record.get("order_id"))
                .date(LocalDate.parse(record.get("date")))
                .customerId(record.get("customer_id"))
                .region(record.get("region"))
                .productCategory(record.get("product_category"))
                .productName(record.get("product_name"))
                .unitsSold(parseInteger(record.get("units_sold")))
                .unitPrice(parseDecimal(record.get("unit_price")))
                .discount(parseDecimal(record.get("discount")))
                .status(record.get("status"))
                .paymentMethod(record.get("payment_method"))
                .build();
    }

    private int parseInteger(String value) {
        return Integer.parseInt(value.trim());
    }

    private BigDecimal parseDecimal(String value) {
        return new BigDecimal(value.trim());
    }
}
