package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float amount;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;

    private LocalDateTime timestamp;

    private float incentive;

    public TransactionRecord() {}

    public TransactionRecord(float amount, UserRecord sender, UserRecord recipient, LocalDateTime timestamp) {
        this.amount = amount;
        this.sender = sender;
        this.recipient = recipient;
        this.timestamp = timestamp;
        this.incentive = 0.0f;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }

    public UserRecord getSender() { return sender; }
    public void setSender(UserRecord sender) { this.sender = sender; }

    public UserRecord getRecipient() { return recipient; }
    public void setRecipient(UserRecord recipient) { this.recipient = recipient; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public float getIncentive() { return incentive; }
    public void setIncentive(float incentive) { this.incentive = incentive; }
}