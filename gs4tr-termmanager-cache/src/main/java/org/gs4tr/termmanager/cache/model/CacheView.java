package org.gs4tr.termmanager.cache.model;

public class CacheView {

    public static class Builder {

	public static Builder start() {
	    return new Builder();
	}

	private long _backupEntryCount;
	private long _creationTime;
	private long _getOperationCount;
	private long _heapCost;
	private long _hits;
	private long _maxGetLatency;
	private long _maxPutLatency;
	private long _ownedEntryCount;
	private long _ownedEntryMemoryCost;
	private long _putOperationCount;
	private long _total;

	private Builder() {
	}

	public Builder backupEntryCount(long backupEntryCount) {
	    _backupEntryCount = backupEntryCount;
	    return this;
	}

	public CacheView build() {
	    return new CacheView(this);
	}

	public Builder creationTime(long creationTime) {
	    _creationTime = creationTime;
	    return this;
	}

	public Builder getOperationCount(long getOperationCount) {
	    _getOperationCount = getOperationCount;
	    return this;
	}

	public Builder heapCost(long heapCost) {
	    _heapCost = heapCost;
	    return this;
	}

	public Builder hits(long hits) {
	    _hits = hits;
	    return this;
	}

	public Builder maxGetLatency(long maxGetLatency) {
	    _maxGetLatency = maxGetLatency;
	    return this;
	}

	public Builder maxPutLatency(long maxPutLatency) {
	    _maxPutLatency = maxPutLatency;
	    return this;
	}

	public Builder ownedEntryCount(long ownedEntryCount) {
	    _ownedEntryCount = ownedEntryCount;
	    return this;
	}

	public Builder ownedEntryMemoryCost(long ownedEntryMemoryCost) {
	    _ownedEntryMemoryCost = ownedEntryMemoryCost;
	    return this;
	}

	public Builder putOperationCount(long putOperationCount) {
	    _putOperationCount = putOperationCount;
	    return this;
	}

	public Builder total(long total) {
	    _total = total;
	    return this;
	}
    }

    private final long _backupEntryCount;
    private final long _creationTime;
    private final long _getOperationCount;
    private final long _heapCost;
    private final long _hits;
    private final long _maxGetLatency;
    private final long _maxPutLatency;
    private final long _ownedEntryCount;
    private final long _ownedEntryMemoryCost;
    private final long _putOperationCount;
    private final long _total;

    private CacheView(Builder builder) {
	_ownedEntryMemoryCost = builder._ownedEntryMemoryCost;
	_backupEntryCount = builder._backupEntryCount;
	_creationTime = builder._creationTime;
	_getOperationCount = builder._getOperationCount;
	_maxPutLatency = builder._maxPutLatency;
	_maxGetLatency = builder._maxGetLatency;
	_ownedEntryCount = builder._ownedEntryCount;
	_putOperationCount = builder._putOperationCount;
	_heapCost = builder._heapCost;
	_total = builder._total;
	_hits = builder._hits;
    }

    public long getBackupEntryCount() {
	return _backupEntryCount;
    }

    public long getCreationTime() {
	return _creationTime;
    }

    public long getGetOperationCount() {
	return _getOperationCount;
    }

    public long getHeapCost() {
	return _heapCost;
    }

    public long getHits() {
	return _hits;
    }

    public long getMaxGetLatency() {
	return _maxGetLatency;
    }

    public long getMaxPutLatency() {
	return _maxPutLatency;
    }

    public long getOwnedEntryCount() {
	return _ownedEntryCount;
    }

    public long getOwnedEntryMemoryCost() {
	return _ownedEntryMemoryCost;
    }

    public long getPutOperationCount() {
	return _putOperationCount;
    }

    public long getTotal() {
	return _total;
    }
}
