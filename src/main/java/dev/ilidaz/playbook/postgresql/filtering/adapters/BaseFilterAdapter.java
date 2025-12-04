package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.BaseFilters;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaseFilterAdapter <F extends BaseFilters<T>, T extends Serializable> {

    public List<Predicate> generateBasePredicates(
            F filters,
            Path<T> path,
            CriteriaBuilder cb
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (filters.getIn() != null) {
            predicates.add(path.in(filters.getIn()));
        }

        if (filters.getNotIn() != null) {
            predicates.add(path.in(filters.getNotIn()).not());
        }

        if (filters.getEqualTo() != null) {
            predicates.add(cb.equal(path, filters.getEqualTo()));
        }

        if (filters.getIsNull() != null) {
            if (Boolean.TRUE.equals(filters.getIsNull())) {
                predicates.add(cb.isNull(path));
            } else {
                predicates.add(cb.isNotNull(path));
            }
        }

        return predicates;
    }
}
