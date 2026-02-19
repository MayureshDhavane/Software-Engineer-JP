package com.jpmc.midascore.entity;

import jakarta.persistence.*;


import java.time.LocalDate;
import java.util.List;

@Entity
public class Portfolio {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Client client;

    @Column
    private String portfolioName;

    @Column
    private LocalDate createdDate;

    @OneToMany(mappedBy = "portfolio")
    private List<SecurityHolding> holdings;

    public Portfolio() {}

    public Portfolio(Long id, Client client, String portfolioName,
                     LocalDate createdDate, List<SecurityHolding> holdings) {
        this.id = id;
        this.client = client;
        this.portfolioName = portfolioName;
        this.createdDate = createdDate;
        this.holdings = holdings;
    }

    public Long getId() { return id; }
    public Client getClient() { return client; }
    public String getPortfolioName() { return portfolioName; }
    public LocalDate getCreatedDate() { return createdDate; }
    public List<SecurityHolding> getHoldings() { return holdings; }

    public void setClient(Client client) { this.client = client; }
    public void setPortfolioName(String portfolioName) { this.portfolioName = portfolioName; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
    public void setHoldings(List<SecurityHolding> holdings) { this.holdings = holdings; }
}
