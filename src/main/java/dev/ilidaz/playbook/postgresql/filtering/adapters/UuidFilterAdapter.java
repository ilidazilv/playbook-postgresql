package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.UuidFilters;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UuidFilterAdapter extends BaseFilterAdapter<UuidFilters, UUID> implements FilterAdapterInterface<UuidFilters, UUID> {
    @Override
    public List<Predicate> generatePredicates(UuidFilters filters, CriteriaBuilder cb, Path<UUID> path) {
        return super.generateBasePredicates(filters, path, cb);
    }

    @Override
    public Class<UuidFilters> getFilterType() {
        return UuidFilters.class;
    }
}
