package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.LongFilters;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LongFilterAdapter extends NumberFilterAdapter<Long, LongFilters> implements FilterAdapterInterface<LongFilters, Long> {
}
