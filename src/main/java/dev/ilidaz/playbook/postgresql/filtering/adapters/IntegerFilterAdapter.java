package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.IntegerFilters;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IntegerFilterAdapter extends NumberFilterAdapter<Integer, IntegerFilters> implements FilterAdapterInterface<IntegerFilters, Integer> {
}
