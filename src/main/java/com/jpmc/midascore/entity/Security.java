package com.jpmc.midascore.entity;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Security {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    private String category;

    @OneToMany(mappedBy = "security")
    private List<SecurityHolding> holdings;

    public Security() {}

    public Security(Long id, String name, String category,
                    List<SecurityHolding> holdings) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.holdings = holdings;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public List<SecurityHolding> getHoldings() { return holdings; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setHoldings(List<SecurityHolding> holdings) { this.holdings = holdings; }
}

