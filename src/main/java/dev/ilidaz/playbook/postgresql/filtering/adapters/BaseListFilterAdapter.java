package dev.ilidaz.playbook.postgresql.filtering.adapters;

import dev.ilidaz.playbook.base.filtering.models.BaseListFilters;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BaseListFilterAdapter<F extends BaseListFilters<T>, T extends Serializable> {

    protected Class<T> valueClass;

    protected BaseListFilterAdapter() {
        super();
        this.resolveGenericTypes();
    }

    @SuppressWarnings("unchecked")
    private void resolveGenericTypes() {
        Type genericSuperclass = getClass().getGenericSuperclass();

        // Handle cases where the class might be proxied (e.g. by Spring CGLIB)
        while (!(genericSuperclass instanceof ParameterizedType type)) {
            // Move up the hierarchy if we are inside a proxy or raw subclass
            genericSuperclass = ((Class<?>) genericSuperclass).getGenericSuperclass();
        }

        Type[] arguments = type.getActualTypeArguments();

        // Assuming your class signature is <T, F, O, I>
        this.valueClass = (Class<T>) arguments[1];
    }

    public List<Predicate> generateBasePredicates(
            F filters,
            Path<Collection<T>> path,
            CriteriaBuilder cb
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (filters.getContainsAll() != null) {
            predicates.add(
                    cb.isTrue(
                            cb.function(
                                    "array_contains",
                                    Boolean.class,
                                    path,
                                    toTypedArray(filters.getContainsAll(), cb)
                            )
                    )
            );
        }

        if (filters.getContains() != null) {
            predicates.add(
                    cb.isTrue(
                            cb.function(
                                    "array_overlaps",
                                    Boolean.class,
                                    path,
                                    toTypedArray(filters.getContains(), cb)
                            )
                    )
            );
        }

        if (filters.getNotContains() != null) {
            predicates.add(
                    cb.isFalse(
                            cb.function(
                                    "array_contains",
                                    Boolean.class,
                                    path,
                                    toTypedArray(filters.getNotContains(), cb)
                            )
                    )
            );
        }

        if (filters.getNotContainsAll() != null) {
            predicates.add(
                    cb.isFalse(
                            cb.function(
                                    "array_overlaps",
                                    Boolean.class,
                                    path,
                                    toTypedArray(filters.getNotContainsAll(), cb)
                            )
                    )
            );
        }

        if (filters.getSizeGt() != null) {
            Expression<Integer> arraySize = cb.function("cardinality", Integer.class, path);
            predicates.add(cb.gt(arraySize, filters.getSizeGt()));
        }

        if (filters.getSizeLe() != null) {
            Expression<Integer> arraySize = cb.function("cardinality", Integer.class, path);
            predicates.add(cb.le(arraySize, filters.getSizeLe()));
        }

        if (filters.getIsNull() != null) {
            if (Boolean.TRUE.equals(filters.getIsNull())) {
                predicates.add(cb.isNull(path));
            } else {
                predicates.add(cb.isNotNull(path));
            }
        }

        return predicates;
    }

    private Expression<Collection<T>> toTypedArray(Collection<T> collection, CriteriaBuilder cb) {
        String arrayString = "{" + collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",")) + "}";

        Class<Collection<T>> arrayClass = (Class<Collection<T>>) Array.newInstance(this.valueClass, 0).getClass();

        return cb.literal(arrayString).as(arrayClass);
    }
}
