package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.BooleanFilters;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

@ApplicationScoped
public class BooleanFilterAdapter extends BaseFilterAdapter<BooleanFilters, Boolean> implements FilterAdapterInterface<BooleanFilters, Boolean> {
    @Override
    public List<Predicate> generatePredicates(BooleanFilters filters, CriteriaBuilder cb, Path<Boolean> path) {
        return super.generateBasePredicates(filters, path, cb);
    }

    @Override
    public Class<BooleanFilters> getFilterType() {
        return BooleanFilters.class;
    }
}
