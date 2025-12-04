package dev.ilidaz.playbook.postgresql.filtering.interfaces;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

public interface FilterAdapterInterface<F, T> {
    List<Predicate> generatePredicates(F filters, CriteriaBuilder cb, Path<T> path);

    Class<F> getFilterType();
}
