package dev.ilidaz.playbook.postgresql.global.repos;

import dev.ilidaz.playbook.postgresql.filtering.services.BaseFilterService;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PlaybookRepo<E, I, F> implements PanacheRepositoryBase<E, I> {
    @Inject
    BaseFilterService baseFilterService;

    protected Class<E> entityClass;
    protected Class<I> idClass;
    protected Class<F> filtersClass;
    protected String idFieldName;

    protected PlaybookRepo() {
        super();
        this.resolveGenericTypes();
        this.getIdFieldName();
    }

    @SuppressWarnings("unchecked")
    private void resolveGenericTypes() {
        Type genericSuperclass = getClass().getGenericSuperclass();

        while (!(genericSuperclass instanceof ParameterizedType type)) {
            // Move up the hierarchy if we are inside a proxy or raw subclass
            genericSuperclass = ((Class<?>) genericSuperclass).getGenericSuperclass();
        }

        Type[] arguments = type.getActualTypeArguments();

        // Assuming your class signature is <T, F, O, I>
        this.entityClass = (Class<E>) arguments[0];
        this.idClass = (Class<I>) arguments[1];
        this.filtersClass = (Class<F>) arguments[2];
    }

    // ✅ Automate finding the ID Field Name
    // Call this lazily or in a @PostConstruct method after EntityManager is injected
    protected void getIdFieldName() {
        if (this.idFieldName == null) {
            EntityType<E> entityType = getEntityManager().getMetamodel().entity(entityClass);

            // This automatically finds the field annotated with @Id
            this.idFieldName = entityType.getId(idClass).getName();
        }
    }

    public List<E> playbookFind(F filters, Integer page, Integer size) {
        // 1. Get the Builder and Query objects
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(entityClass);
        Root<E> root = cq.from(entityClass);

        Map<String, From<?, ?>> subtables = new HashMap<>();

        List<Predicate> predicates = baseFilterService.generatePredicates(filters, cb, root, subtables, "");

        // 2. Convert the List<Predicate> to an Array
        Predicate[] predicateArray = predicates.toArray(new Predicate[0]);

        // 3. Combine Predicates using AND
        // This creates a single combined condition: (p1 AND p2 AND p3...)
        Predicate finalWhereClause = cb.and(predicateArray);

        // 4. Apply the combined Predicate to the query
        cq.select(root).where(finalWhereClause);

        TypedQuery<E> typedQuery = getEntityManager().createQuery(cq);

        // 5. Execute the query
        return typedQuery.setFirstResult(page * size).setMaxResults(size).getResultList();
    }
}
