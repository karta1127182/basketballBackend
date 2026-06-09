package com.hoopers.basketball.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<Order> findByTypeAndTargetId(Order.Type type, Long targetId);
	List<Order> findAllByTypeAndTargetIdIn(Order.Type type, List<Long> targetIds);
	List<Order> findAllByUserIdOrderByCreatedAtDesc(Long userId);
	void deleteAllByTypeAndTargetIdIn(Order.Type type, List<Long> targetIds);
}
