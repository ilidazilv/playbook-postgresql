package dev.ilidaz.playbook.postgresql.global.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;

import java.util.Arrays;
import java.util.Map;

@ApplicationScoped
public class SubtableService {
    public <T> Path<T> getPath(
            From<?, ?> parent,
            Map<String, From<?, ?>> subtables,
            String path,
            String parentPath
    ) {
        From<?, ?> table = joinSubTables(parent, subtables, path, parentPath, true);

        String[] route = path.split("\\.");

        return table.get(route[route.length - 1]);
    }


    public From<?, ?> joinSubTables(
            From<?, ?> parent,
            Map<String, From<?, ?>> subtables,
            String path,
            String parentPath,
            Boolean isSkipLast
    ) {
        if (!path.contains(".") && Boolean.TRUE.equals(isSkipLast)) {
            return parent;
        }

        String[] route = path.split("\\.");

        String currentSubtableName = route[0];

        From<?, ?> joinedSubtable = joinTable(parent, subtables, currentSubtableName, parentPath);
        
        if (route.length == 1) {
            return joinedSubtable;
        }

        String[] nextRoute = Arrays.copyOfRange(route, 1, route.length);

        return joinSubTables(joinedSubtable, subtables, String.join(".", nextRoute), String.join(".", parentPath, currentSubtableName), isSkipLast);
    }

    private From<?, ?> joinTable(
            From<?, ?> parent,
            Map<String, From<?, ?>> subtables,
            String subtableName,
            String parentPath
    ) {
        String subtableKey = String.join(parentPath, subtableName, ".");

        From<?, ?> cachedSubtable = subtables.get(subtableKey);
        
        if (cachedSubtable != null) {
            return cachedSubtable;
        }

        final Join<Object, Object> joined = parent.join(subtableName, JoinType.LEFT);

        subtables.put(subtableKey, joined);
        
        return joined;
    }
}
