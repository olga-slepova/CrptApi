package ru.slepova;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CrptApi {
    private final long timeUnitMillis;
    private final int requestLimit;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private long startTimeMillis = System.currentTimeMillis();
    private final ObjectMapper objectMapper;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnitMillis = timeUnit.toMillis(1);
        this.requestLimit = requestLimit;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public synchronized HttpResponse<String> createDocument(Document document, String sign) throws IOException, InterruptedException {
        long millisDiff = System.currentTimeMillis() - startTimeMillis;
        if (millisDiff > timeUnitMillis) {
            requestCount.set(0);
        }
        if (requestCount.get() == 0) {
            startTimeMillis = System.currentTimeMillis();
            requestCount.incrementAndGet();
        } else if (requestCount.get() > 0 && requestCount.get() < requestLimit) {
            requestCount.incrementAndGet();
        } else if (requestCount.get() >= requestLimit) {
            Thread.sleep(timeUnitMillis - millisDiff);
            startTimeMillis = System.currentTimeMillis();
            requestCount.set(1);
        }

        String url = "https://ismp.crpt.ru/api/v3/lk/documents/create";
        String jsonBody = objectMapper.writeValueAsString(document);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .setHeader("signature", sign)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Data
    @AllArgsConstructor
    public static class Document {
        @JsonProperty("description")
        private Description description;
        @JsonProperty("doc_id")
        private String docId;
        @JsonProperty("doc_status")
        private String docStatus;
        @JsonProperty("doc_type")
        private DocType docType;
        @JsonProperty("import_request")
        private boolean importRequest;
        @JsonProperty("owner_inn")
        private String ownerInn;
        @JsonProperty("participant_inn")
        private String participantInn;
        @JsonProperty("producer_inn")
        private String producerInn;
        @JsonProperty("production_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate productionDate;
        @JsonProperty("production_type")
        private String productionType;
        @JsonProperty("products")
        private List<Product> products;
        @JsonProperty("reg_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate regDate;
        @JsonProperty("reg_number")
        private String regNumber;
    }

    @Data
    @AllArgsConstructor
    public static class Product {
        @JsonProperty("certificate_document")
        private String certificateDocument;
        @JsonProperty("certificate_document_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate certificateDocumentDate;
        @JsonProperty("certificate_document_number")
        private String certificateDocumentNumber;
        @JsonProperty("owner_inn")
        private String ownerInn;
        @JsonProperty("producer_inn")
        private String producerInn;
        @JsonProperty("production_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate productionDate;
        @JsonProperty("tnved_code")
        private String tnvedCode;
        @JsonProperty("uit_code")
        private String uitCode;
        @JsonProperty("uitu_code")
        private String uituCode;
    }

    @Data
    @AllArgsConstructor
    public static class Description {
        @JsonProperty("participant_inn")
        private String participantInn;
    }

    public enum DocType {
        LP_INTRODUCE_GOODS;
    }
}