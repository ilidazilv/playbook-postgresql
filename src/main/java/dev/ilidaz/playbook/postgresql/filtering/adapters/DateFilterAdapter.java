package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.interfaces.DateFilters;
import dev.ilidaz.playbook.base.filtering.models.BaseFilters;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

public abstract class DateFilterAdapter<T extends Temporal & Serializable & Comparable, F extends DateFilters<T>> extends BaseFilterAdapter<BaseFilters<T>, T> {

    protected Class<F> filtersClass;

    protected DateFilterAdapter() {
        super();
        this.resolveGenericTypes();
    }

    @SuppressWarnings("unchecked")
    private void resolveGenericTypes() {
        Type genericSuperclass = getClass().getGenericSuperclass();

        // Handle cases where the class might be proxied (e.g. by Spring CGLIB)
        while (!(genericSuperclass instanceof ParameterizedType type)) {
            // Move up the hierarchy if we are inside a proxy or raw subclass
            genericSuperclass = ((Class<?>) genericSuperclass).getGenericSuperclass();
        }

        Type[] arguments = type.getActualTypeArguments();

        // Assuming your class signature is <T, F, O, I>
        this.filtersClass = (Class<F>) arguments[0];
    }

    @SuppressWarnings("unchecked")
    public List<Predicate> generatePredicates(F filters, CriteriaBuilder cb, Path<T> path) {
        List<Predicate> predicates = new ArrayList<>(generateBasePredicates((BaseFilters<T>) filters, path, cb));

        if (filters.getGreaterThan() != null) {
            predicates.add(cb.greaterThan(path, filters.getGreaterThan()));
        }

        if (filters.getGreaterThanEquals() != null) {
            predicates.add(cb.greaterThanOrEqualTo(path, filters.getGreaterThan()));
        }

        if (filters.getLowerThan() != null) {
            predicates.add(cb.lessThan(path, filters.getLowerThan()));
        }

        if (filters.getLowerThanEquals() != null) {
            predicates.add(cb.lessThanOrEqualTo(path, filters.getLowerThanEquals()));
        }

        return predicates;
    }

    public Class<F> getFilterType() {
        return filtersClass;
    }
}
