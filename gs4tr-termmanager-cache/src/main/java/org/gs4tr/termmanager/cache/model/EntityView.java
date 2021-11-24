package org.gs4tr.termmanager.cache.model;

public class EntityView<K, V> {

    public static class Builder<K, V> {

	public static <K, V> Builder<K, V> start() {
	    return new Builder<K, V>();
	}

	private long _cost;
	private long _creationTime;
	private long _expirationTime;
	private long _hits;
	private K _key;
	private long _lastAccessTime;
	private long _lastStoredTime;
	private long _lastUpdateTime;
	private long _ttl;
	private V _value;
	private long _version;

	private Builder() {
	}

	public EntityView<K, V> build() {
	    return new EntityView<K, V>(this);
	}

	public Builder<K, V> cost(long cost) {
	    _cost = cost;
	    return this;
	}

	public Builder<K, V> creationTime(long creationTime) {
	    _creationTime = creationTime;
	    return this;
	}

	public Builder<K, V> expirationTime(long expirationTime) {
	    _expirationTime = expirationTime;
	    return this;
	}

	public Builder<K, V> hits(long hits) {
	    _hits = hits;
	    return this;
	}

	public Builder<K, V> key(K key) {
	    _key = key;
	    return this;
	}

	public Builder<K, V> lastAccessTime(long lastAccessTime) {
	    _lastAccessTime = lastAccessTime;
	    return this;
	}

	public Builder<K, V> lastStoredTime(long lastStoredTime) {
	    _lastStoredTime = lastStoredTime;
	    return this;
	}

	public Builder<K, V> lastUpdateTime(long lastUpdateTime) {
	    _lastUpdateTime = lastUpdateTime;
	    return this;
	}

	public Builder<K, V> ttl(long ttl) {
	    _ttl = ttl;
	    return this;
	}

	public Builder<K, V> value(V value) {
	    _value = value;
	    return this;
	}

	public Builder<K, V> version(long version) {
	    _version = version;
	    return this;
	}
    }

    private final long _cost;
    private final long _creationTime;
    private final long _expirationTime;
    private final long _hits;
    private final K _key;
    private final long _lastAccessTime;
    private final long _lastStoredTime;
    private final long _lastUpdateTime;
    private final long _ttl;
    private final V _value;
    private final long _version;

    private EntityView(Builder<K, V> builder) {
	_creationTime = builder._creationTime;
	_key = builder._key;
	_value = builder._value;
	_version = builder._version;
	_lastAccessTime = builder._lastAccessTime;
	_lastStoredTime = builder._lastStoredTime;
	_lastUpdateTime = builder._lastUpdateTime;
	_cost = builder._cost;
	_hits = builder._hits;
	_ttl = builder._ttl;
	_expirationTime = builder._expirationTime;
    }

    public long getCost() {
	return _cost;
    }

    public long getCreationTime() {
	return _creationTime;
    }

    public long getExpirationTime() {
	return _expirationTime;
    }

    public long getHits() {
	return _hits;
    }

    public K getKey() {
	return _key;
    }

    public long getLastAccessTime() {
	return _lastAccessTime;
    }

    public long getLastStoredTime() {
	return _lastStoredTime;
    }

    public long getLastUpdateTime() {
	return _lastUpdateTime;
    }

    public long getTtl() {
	return _ttl;
    }

    public V getValue() {
	return _value;
    }

    public long getVersion() {
	return _version;
    }
}
