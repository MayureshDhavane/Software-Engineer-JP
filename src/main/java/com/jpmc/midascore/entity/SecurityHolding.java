package com.jpmc.midascore.entity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class SecurityHolding {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Security security;

    @ManyToOne
    private Portfolio portfolio;

    @Column
    private Integer quantity;

    @Column
    private BigDecimal purchasePrice;

    @Column
    private LocalDate purchaseDate;

    public SecurityHolding() {}

    public SecurityHolding(Long id, Security security, Portfolio portfolio,
                           Integer quantity, BigDecimal purchasePrice,
                           LocalDate purchaseDate) {
        this.id = id;
        this.security = security;
        this.portfolio = portfolio;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.purchaseDate = purchaseDate;
    }

    public Long getId() { return id; }
    public Security getSecurity() { return security; }
    public Portfolio getPortfolio() { return portfolio; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public LocalDate getPurchaseDate() { return purchaseDate; }

    public void setSecurity(Security security) { this.security = security; }
    public void setPortfolio(Portfolio portfolio) { this.portfolio = portfolio; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
}

