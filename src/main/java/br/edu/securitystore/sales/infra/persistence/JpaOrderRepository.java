package br.edu.securitystore.sales.infra.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaOrderRepository extends JpaRepository<OrderEntity,Long>{List<OrderEntity> findByCustomerId(Long customerId);}
