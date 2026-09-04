package com.example.etl.controller;

import com.example.etl.dto.OrderEvent;
import com.example.etl.dto.OrderRequest;
import com.example.etl.model.ProcessedOrder;
import com.example.etl.producer.OrderProducer;
import com.example.etl.repository.ProcessedOrderRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProducer orderProducer;
    private final ProcessedOrderRepository processedOrderRepository;

    public OrderController(OrderProducer orderProducer, ProcessedOrderRepository processedOrderRepository) {
        this.orderProducer = orderProducer;
        this.processedOrderRepository = processedOrderRepository;
    }

    /** Extract stage: accept a new order and publish it to Kafka. */
    @PostMapping
    public ResponseEntity<Map<String, String>> submitOrder(@Valid @RequestBody OrderRequest request) {
        String orderId = UUID.randomUUID().toString();

        OrderEvent event = new OrderEvent(
                orderId,
                request.getCustomerId(),
                request.getCustomerCountry().toUpperCase(),
                request.getProductId(),
                request.getProductName(),
                request.getQuantity(),
                request.getUnitPriceUsd(),
                Instant.now()
        );

        orderProducer.publish(event);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of("orderId", orderId, "status", "PUBLISHED"));
    }

    /** Verify the load stage: list processed (transformed + persisted) orders. */
    @GetMapping("/processed")
    public Page<ProcessedOrder> listProcessedOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return processedOrderRepository.findAll(pageable);
    }

    @GetMapping("/processed/{orderId}")
    public ResponseEntity<ProcessedOrder> getProcessedOrder(@PathVariable String orderId) {
        return processedOrderRepository.findById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/processed/by-customer/{customerId}")
    public Page<ProcessedOrder> getOrdersByCustomer(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return processedOrderRepository.findByCustomerId(customerId, PageRequest.of(page, size));
    }
}
