package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.LocalDateFilters;
import dev.ilidaz.playbook.base.filtering.models.OffsetDateTimeFilters;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@ApplicationScoped
public class LocalDateFilterAdapter extends DateFilterAdapter<LocalDate, LocalDateFilters> implements FilterAdapterInterface<LocalDateFilters, LocalDate> {
}
