package com.jpmc.midascore.entity;


import jakarta.persistence.*;

@Entity
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float amount;

    @Column
    private float incentive;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;


    // Default constructor required by JPA
    protected TransactionRecord() {
    }

    // Constructor to initialize all fields except id
    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getIncentive(){return incentive;}

    public void setIncentive(float incentive){this.incentive = incentive;}

    public UserRecord getSender() {
        return sender;
    }

    public void setSender(UserRecord sender) {
        this.sender = sender;
    }

    public UserRecord getRecipient() {
        return recipient;
    }

    public void setRecipient(UserRecord recipient) {
        this.recipient = recipient;
    }

//    @Override
//    public String toString() {
//        return String.format(
//                "TransactionRecord[id=%d, sender=%s, recipient=%s, amount=%f]",
//                id,
//                sender != null ? sender.getName() : "null",
//                recipient != null ? recipient.getName() : "null",
//                amount
//        );
//    }
}
