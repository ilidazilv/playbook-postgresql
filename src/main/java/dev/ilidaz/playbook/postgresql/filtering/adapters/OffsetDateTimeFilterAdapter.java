package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.OffsetDateTimeFilters;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;

@ApplicationScoped
public class OffsetDateTimeFilterAdapter extends DateFilterAdapter<OffsetDateTime, OffsetDateTimeFilters> implements FilterAdapterInterface<OffsetDateTimeFilters, OffsetDateTime> {
}
