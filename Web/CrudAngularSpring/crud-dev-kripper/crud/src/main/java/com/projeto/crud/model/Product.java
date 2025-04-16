package com.projeto.crud.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

@Table(name="product")
@Entity(name="product")
@EqualsAndHashCode(of = "id")
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private Number price_in_cents;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getPrice_in_cents() {
        return price_in_cents;
    }

    public void setPrice_in_cents(Number price_in_cents) {
        this.price_in_cents = price_in_cents;
    }
}
