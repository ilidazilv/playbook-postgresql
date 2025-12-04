package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.DoubleFilters;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DoubleFilterAdapter extends NumberFilterAdapter<Double, DoubleFilters> implements FilterAdapterInterface<DoubleFilters, Double> {

}
