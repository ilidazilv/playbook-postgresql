package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.IntegerListFilters;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class IntegerListFilterAdapter extends BaseListFilterAdapter<IntegerListFilters, Integer> implements FilterAdapterInterface<IntegerListFilters, Collection<Integer>> {
    @Override
    public List<Predicate> generatePredicates(IntegerListFilters filters, CriteriaBuilder cb, Path<Collection<Integer>> path) {
        return new ArrayList<>(generateBasePredicates(filters, path, cb));
    }

    @Override
    public Class<IntegerListFilters> getFilterType() {
        return IntegerListFilters.class;
    }
}
