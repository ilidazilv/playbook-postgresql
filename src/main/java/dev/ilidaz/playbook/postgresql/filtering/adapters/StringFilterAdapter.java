package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.StringFilters;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class StringFilterAdapter extends BaseFilterAdapter<StringFilters, String> implements FilterAdapterInterface<StringFilters, String> {
    @Override
    public List<Predicate> generatePredicates(StringFilters filters, CriteriaBuilder cb, Path<String> path) {
        List<Predicate> predicates = new ArrayList<>(generateBasePredicates(filters, path, cb));

        if (filters.getContains() != null) {
            predicates.add(cb.like(cb.lower(path), "%" + filters.getContains().toLowerCase() + "%"));
        }

        if (filters.getNotContains() != null) {
            predicates.add(cb.not(cb.like(cb.lower(path), "%" + filters.getNotContains().toLowerCase() + "%")));
        }

        if (filters.getIregex() != null) {
            predicates.add(cb.isTrue(cb.function("regexp_like", Boolean.class, path, cb.literal(filters.getIregex()), cb.literal("i"))));
        }

        if (filters.getNotIregex() != null) {
            predicates.add(cb.isFalse(cb.function("regexp_like", Boolean.class, path, cb.literal(filters.getNotIregex()), cb.literal("i"))));
        }

        if (filters.getStartsWith() != null) {
            predicates.add(cb.like(cb.lower(path), filters.getStartsWith().toLowerCase() + "%"));
        }

        if (filters.getEndsWith() != null) {
            predicates.add(cb.like(cb.lower(path), "%" + filters.getEndsWith().toLowerCase()));
        }

        return predicates;
    }

    @Override
    public Class<StringFilters> getFilterType() {
        return StringFilters.class;
    }
}
