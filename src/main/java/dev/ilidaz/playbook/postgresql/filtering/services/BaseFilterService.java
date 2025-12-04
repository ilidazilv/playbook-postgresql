package dev.ilidaz.playbook.postgresql.filtering.services;

import dev.ilidaz.playbook.base.filtering.annotations.FieldConfig;
import dev.ilidaz.playbook.base.filtering.annotations.FilterConfig;
import dev.ilidaz.playbook.base.filtering.enums.LogicalFilterType;
import dev.ilidaz.playbook.base.global.enums.ErrorCodes;
import dev.ilidaz.playbook.base.global.exceptions.PlaybookBaseException;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import dev.ilidaz.playbook.postgresql.global.services.SubtableService;
import io.quarkus.arc.All;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class BaseFilterService {
    @Inject
    SubtableService subtableService;

    private final Map<Object, FilterAdapterInterface<?, ?>> adaptersMap;

    @Inject
    public BaseFilterService(
            @All List<FilterAdapterInterface<?, ?>> adapters
    ) {
        adaptersMap = adapters.stream()
                .collect(Collectors.toMap(FilterAdapterInterface::getFilterType,
                        Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <F, E> List<Predicate> generatePredicates(F filters, CriteriaBuilder cb, From<E, ?> root, Map<String, From<?, ?>> subtables, String parentPath) {
        List<Predicate> predicates = new ArrayList<>();
        for (Field dtoField : filters.getClass().getDeclaredFields()) {
            dtoField.setAccessible(true);

            try {
                Object filterValues = dtoField.get(filters);

                if (filterValues == null) {
                    continue;
                }

                if (adaptersMap.containsKey(dtoField.getType())) {
                    predicates.addAll(handleByAdapter(dtoField, filterValues, cb, root, subtables, parentPath));
                }

                if (dtoField.getType().isAnnotationPresent(FilterConfig.class)) {
                    predicates.addAll(handleBySubtable(dtoField, filterValues, cb, root, subtables, parentPath));
                }

                if (List.of(LogicalFilterType.AND.name().toLowerCase(), LogicalFilterType.OR.name().toLowerCase()).contains(dtoField.getName())) {
                    predicates.addAll(handleByLogicalFilter(dtoField, filterValues, cb, root, subtables, parentPath));
                }


            } catch (Exception e) {
                Log.error(ErrorCodes.QUERY_GENERATING_ERROR, e);
                throw new PlaybookBaseException(e.getMessage(), ErrorCodes.QUERY_GENERATING_ERROR);
            } finally {
                dtoField.setAccessible(false);
            }
        }

        return predicates;
    }

    private <E> List<Predicate> handleByAdapter(Field dtoField, Object filterValues, CriteriaBuilder cb, From<E, ?> root, Map<String, From<?, ?>> subtables, String parentPath) {
        FilterAdapterInterface service = adaptersMap.get(dtoField.getType());

        FieldConfig annotation = dtoField.getAnnotation(FieldConfig.class);

        String path = annotation != null && annotation.path() != null && !annotation.path().isEmpty() ?
                annotation.path() : dtoField.getName();

        Path<?> field = subtableService.getPath(root, subtables, path, parentPath);
        return service.generatePredicates(filterValues, cb, field);
    }

    private <E> List<Predicate> handleBySubtable(Field dtoField, Object filterValues, CriteriaBuilder cb, From<E, ?> root, Map<String, From<?, ?>> subtables, String parentPath) {
        FilterConfig annotation = dtoField.getAnnotation(FilterConfig.class);

        String path = annotation != null && annotation.path() != null && !annotation.path().isEmpty() ?
                annotation.path() : dtoField.getName();

        String subtablePath = parentPath != null && !parentPath.isEmpty() ? String.join(parentPath, path, ".") : path;
        From<?, ?> subtable = subtableService.joinSubTables(root, subtables, subtablePath, parentPath, false);

        return generatePredicates(filterValues, cb, subtable, subtables, subtablePath);
    }

    private <E> List<Predicate> handleByLogicalFilter(Field dtoField, Object filterValues, CriteriaBuilder cb, From<E, ?> root, Map<String, From<?, ?>> subtables, String parentPath) {
        List<Predicate> predicates = new ArrayList<>();

        if (!(filterValues instanceof List)) {
            return predicates;
        }

        for (Object filter : (List<?>) filterValues) {
            predicates.add(cb.and(generatePredicates(filter, cb, root, subtables, parentPath)));
        }

        return List.of(dtoField.getName().equals(LogicalFilterType.AND.name().toLowerCase()) ? cb.and(predicates) : cb.or(predicates));
    }

}
