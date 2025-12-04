package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.InstantFilters;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;

@ApplicationScoped
public class InstantFilterAdapter extends DateFilterAdapter<Instant, InstantFilters> implements FilterAdapterInterface<InstantFilters, Instant> {
}
