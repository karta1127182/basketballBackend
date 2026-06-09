package com.hoopers.basketball.order;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Type type;

	@Column(nullable = false)
	private Long targetId;

	@Column(nullable = false)
	private int amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Status status;

	@Column(length = 40)
	private String paymentProvider;

	private Instant paidAt;

	@Column(nullable = false)
	private Instant createdAt;

	protected Order() {
	}

	public Order(Long userId, Type type, Long targetId, int amount) {
		this.userId = userId;
		this.type = type;
		this.targetId = targetId;
		this.amount = amount;
		this.status = amount == 0 ? Status.PAID : Status.PENDING;
		this.paymentProvider = amount == 0 ? "FREE" : null;
		this.paidAt = amount == 0 ? Instant.now() : null;
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public Type getType() {
		return type;
	}

	public Long getTargetId() {
		return targetId;
	}

	public int getAmount() {
		return amount;
	}

	public Status getStatus() {
		return status;
	}

	public Instant getPaidAt() {
		return paidAt;
	}

	public String getPaymentProvider() {
		return paymentProvider;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void markPaid(String paymentProvider) {
		this.status = Status.PAID;
		this.paymentProvider = paymentProvider;
		this.paidAt = Instant.now();
	}

	public void markRefunded() {
		this.status = Status.REFUNDED;
	}

	public enum Type {
		COURSE
	}

	public enum Status {
		PENDING,
		PAID,
		CANCELLED,
		REFUNDED
	}
}
