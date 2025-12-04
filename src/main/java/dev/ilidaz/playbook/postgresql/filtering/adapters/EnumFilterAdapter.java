package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.BaseFilters;
import dev.ilidaz.playbook.base.filtering.models.EnumFilters;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@SuppressWarnings({"rawtypes", "unchecked"})
public class EnumFilterAdapter extends BaseFilterAdapter
        implements FilterAdapterInterface<EnumFilters, Enum> {

    @Override
    public List<Predicate> generatePredicates(EnumFilters filters, CriteriaBuilder cb, Path<Enum> path) {
        BaseFilters<Enum> enumFiltersBaseFilters = BaseFilters.baseFiltersBuilder()
                .in(filters.getIn())
                .notIn(filters.getNotIn())
                .equalTo(filters.getEqualTo())
                .notEqualTo(filters.getNotEqualTo())
                .isNull(filters.getIsNull())
                .build();

        return new ArrayList<>(generateBasePredicates(enumFiltersBaseFilters, path, cb));
    }

    @Override
    public Class<EnumFilters> getFilterType() {
        return EnumFilters.class;
    }
}
