package com.example.etl.repository;

import com.example.etl.model.ProcessedOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrder, String> {

    Page<ProcessedOrder> findByCustomerId(String customerId, Pageable pageable);

    Page<ProcessedOrder> findByCustomerTier(String customerTier, Pageable pageable);
}
