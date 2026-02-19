package com.jpmc.midascore.entity;
import jakarta.persistence.*;


import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
public class Client {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private FinancialAdvisor advisor;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String email;

    @OneToOne(mappedBy = "client")
    private Portfolio portfolio;

    public Client() {}

    public Client(Long id, FinancialAdvisor advisor, String firstName,
                  String lastName, String email, Portfolio portfolio) {
        this.id = id;
        this.advisor = advisor;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.portfolio = portfolio;

    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public  FinancialAdvisor getAdvisor() { return advisor; }
    public Portfolio getPortfolio() {return portfolio;}


    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setAdvisor(FinancialAdvisor advisor) { this.advisor = advisor; }
    public void setPortfolio(Portfolio portfolio) { this.portfolio = portfolio; }


}

