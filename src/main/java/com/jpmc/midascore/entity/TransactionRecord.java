package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserRecord sender;

    @ManyToOne
    private UserRecord recipient;

    private float amount;

    protected TransactionRecord() {} // default, required constructor

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }
    // getters
    public Long getId() { return id;}
    public UserRecord getSender() {return sender;}
    public UserRecord getRecipient() {return recipient;}
    public float getAmount() {return amount;}

    @Override
    public String toString(){
        return "TransactionRecord{"+
                "id=" + id +
                ", senderId=" + (sender != null ? sender.getName() : "null") +
                ", recipient" + (recipient != null ? recipient.getName() : "null") +
                ", amount=" + amount +
                '}';
                }
    }


    
