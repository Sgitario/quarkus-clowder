package io.quarkiverse.clowder.tests.database.jdbc;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;

public interface PersonResource extends PanacheEntityResource<Person, Long> {
}
