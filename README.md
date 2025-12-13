# Playbook-PostgreSQL

Playbook-PostgreSQL is a powerful and flexible library for building dynamic database queries in Java applications using Quarkus and Hibernate ORM with Panache. It simplifies the process of filtering data from a PostgreSQL database by allowing you to define filter criteria using simple data objects.

The library is designed to be highly extensible, allowing you to easily add support for new filter types and custom filtering logic.

## How it Works

The core of the library is the `BaseFilterService`, which dynamically constructs JPA Criteria API queries based on a filter object you provide. Here's a breakdown of the process:

1.  **Filter Object:** You define a plain Java object (DTO) that represents the filters you want to apply. The fields in this object correspond to the fields you want to filter on.
2.  **Adapters:** The library uses a set of `FilterAdapterInterface` implementations to handle different data types (e.g., `String`, `Integer`, `LocalDate`). Each adapter knows how to generate the appropriate JPA `Predicate` for its corresponding type.
3.  **Annotations:** You can use annotations to customize the filtering behavior:
    *   `@FieldConfig`: Specifies the database column name or path if it differs from the field name in your filter object.
    *   `@FilterConfig`: Marks a field as a sub-filter, enabling you to filter on related entities (e.g., in a one-to-one or many-to-one relationship).
4.  **Query Generation:** The `BaseFilterService` iterates over the fields in your filter object, uses the appropriate adapters and annotations to generate a list of `Predicate` objects, and then combines them to create the final query.

This approach allows you to build complex queries with multiple conditions, including nested filters on related entities, without writing complex boilerplate code.

## Features

### Done

- [x] **String, Number, Date Filters:** Basic filtering for common data types.
- [x] **Base List and Double List Filters:** Support for `IN` and `BETWEEN` clauses.
- [x] **Field Config:** Customize mapping between filter fields and database columns.
- [x] **Subtable Filtering (One-to-One, Many-to-One):** Filter on related entities.
- [x] **`playbookFind` Method:** A convenient method for executing the generated query.

### Todo

- [ ] **Many-to-Many, One-to-Many Filters:** Extend subtable filtering to support more complex relationships.
- [ ] **`count` and `findById` Methods:** Add support for counting results and retrieving single entities by ID.
- [ ] **Enrich List of Filters:** Expand the set of available filter types for both base and list types.

### Plan for Later

- [ ] **Orders:** Add support for dynamic sorting.
- [ ] **Projections:** Allow specifying which fields to return in the query results.
