package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.DoubleListFilters;
import dev.ilidaz.playbook.base.filtering.models.StringFilters;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class DoubleListFilterAdapter extends BaseListFilterAdapter<DoubleListFilters, Double> implements FilterAdapterInterface<DoubleListFilters, Collection<Double>> {
    @Override
    public List<Predicate> generatePredicates(DoubleListFilters filters, CriteriaBuilder cb, Path<Collection<Double>> path) {
        return new ArrayList<>(generateBasePredicates(filters, path, cb));
    }

    @Override
    public Class<DoubleListFilters> getFilterType() {
        return DoubleListFilters.class;
    }
}
