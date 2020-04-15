package com.aerospike.helper.query.cache;

import com.aerospike.client.query.IndexType;

import java.util.Objects;

public class IndexKey {

    private final String namespace;
    private final String set;
    private final String field;
    private final IndexType type;

    public IndexKey(String namespace, String set, String field, IndexType type) {
        this.namespace = namespace;
        this.set = set;
        this.field = field;
        this.type = type;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getSet() {
        return set;
    }

    public String getField() {
        return field;
    }

    public IndexType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexKey indexKey = (IndexKey) o;
        return Objects.equals(namespace, indexKey.namespace) &&
                Objects.equals(set, indexKey.set) &&
                Objects.equals(field, indexKey.field) &&
                type == indexKey.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, set, field, type);
    }
}
