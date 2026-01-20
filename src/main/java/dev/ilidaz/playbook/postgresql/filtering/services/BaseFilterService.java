package dev.ilidaz.playbook.postgresql.filtering.services;

import dev.ilidaz.playbook.base.filtering.annotations.FieldConfig;
import dev.ilidaz.playbook.base.filtering.annotations.FilterConfig;
import dev.ilidaz.playbook.base.filtering.enums.LogicalFilterType;
import dev.ilidaz.playbook.base.filtering.enums.RelationType;
import dev.ilidaz.playbook.base.global.enums.ErrorCodes;
import dev.ilidaz.playbook.base.global.exceptions.PlaybookBaseException;
import dev.ilidaz.playbook.postgresql.filtering.interfaces.FilterAdapterInterface;
import dev.ilidaz.playbook.postgresql.global.services.SubtableService;
import io.quarkus.arc.All;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Id;
import jakarta.persistence.criteria.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
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
    public <F, E> List<Predicate> generatePredicates(
            F filters, CriteriaBuilder cb,
            From<E, ?> root,
            Map<String, From<?, ?>> subtables,
            String parentPath,
            AbstractQuery<?> query
    ) {
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
                    FieldConfig annotation = dtoField.getAnnotation(FieldConfig.class);
                    if (annotation == null || annotation.relationType().equals(RelationType.SINGLE)) {
                        predicates.addAll(handleBySubtable(dtoField, filterValues, cb, root, subtables, parentPath, query));
                    } else if (annotation.relationType().equals(RelationType.MULTIPLE)) {
                        predicates.addAll(handleMultipleRelations(dtoField, filterValues, cb, root, query));
                    }
                }

                if (List.of(LogicalFilterType.AND.name().toLowerCase(), LogicalFilterType.OR.name().toLowerCase()).contains(dtoField.getName())) {
                    predicates.addAll(handleByLogicalFilter(dtoField, filterValues, cb, root, subtables, parentPath, query));
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

    private <E> List<Predicate> handleBySubtable(
            Field dtoField,
            Object filterValues,
            CriteriaBuilder cb,
            From<E, ?> root,
            Map<String, From<?, ?>> subtables,
            String parentPath,
            AbstractQuery<?> query
    ) {
        FilterConfig annotation = dtoField.getAnnotation(FilterConfig.class);

        String path = annotation != null && annotation.path() != null && !annotation.path().isEmpty() ?
                annotation.path() : dtoField.getName();

        String subtablePath = parentPath != null && !parentPath.isEmpty() ? String.join(parentPath, path, ".") : path;
        From<?, ?> subtable = subtableService.joinSubTables(root, subtables, subtablePath, parentPath, false);

        return generatePredicates(filterValues, cb, subtable, subtables, subtablePath, query);
    }

    private <E> List<Predicate> handleByLogicalFilter(
            Field dtoField,
            Object filterValues,
            CriteriaBuilder cb,
            From<E, ?> root,
            Map<String, From<?, ?>> subtables,
            String parentPath,
            AbstractQuery<?> query
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (!(filterValues instanceof List)) {
            return predicates;
        }

        for (Object filter : (List<?>) filterValues) {
            predicates.add(cb.and(generatePredicates(filter, cb, root, subtables, parentPath, query)));
        }

        return List.of(dtoField.getName().equals(LogicalFilterType.AND.name().toLowerCase()) ? cb.and(predicates) : cb.or(predicates));
    }

    private <E, S, I> List<Predicate> handleMultipleRelations(
            Field dtoField,
            Object filterValues,
            CriteriaBuilder cb,
            From<E, ?> root,
            AbstractQuery<?> query
    ) {
        FilterConfig annotation = dtoField.getType().getAnnotation(FilterConfig.class);

        final Class<S> subClass = (Class<S>) annotation.entityClass();

        Class<I> idClass = null;
        String idPath = null;

        Field[] declaredFields = subClass.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Id.class)) {
                idClass = (Class<I>) field.getClass();
                idPath = field.getName();
            }
        }

        if (idClass == null) {
            Log.error(ErrorCodes.QUERY_GENERATING_ERROR);
            throw new PlaybookBaseException("No id found for entity %s".formatted(subClass.getName()), ErrorCodes.QUERY_GENERATING_ERROR);
        }

        Subquery<I> subquery = query.subquery(idClass);
        Root<S> subroot = subquery.from(subClass);

        List<Predicate> subrootPredicates = generatePredicates(filterValues, cb, subroot, new HashMap<>(), "", subquery);

        subquery.select(subroot.get(idPath)).where(subrootPredicates);

        return List.of(root.get(idPath).in(subquery));
    }

}
