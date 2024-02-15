package io.quarkiverse.clowder.tests.database.jdbc;

import jakarta.persistence.Entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

@Entity
public class Person extends PanacheEntity {
    public String name;
}
