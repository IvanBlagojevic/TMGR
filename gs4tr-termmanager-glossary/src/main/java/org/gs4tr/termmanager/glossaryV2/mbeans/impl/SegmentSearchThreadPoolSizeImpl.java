package org.gs4tr.termmanager.glossaryV2.mbeans.impl;

import org.gs4tr.termmanager.glossaryV2.mbeans.SegmentSearchThreadPoolSize;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("segmentSearchThreadPoolSize")
public class SegmentSearchThreadPoolSizeImpl implements SegmentSearchThreadPoolSize {

    private static final int DEFAULT_THREAD_POOL_SIZE = 2;

    private static final int THREAD_POOL_SIZE_LIMIT = 5;

    @Value("${segmentSearch.threadPoolSize:2}")
    private Integer _threadPoolSize;

    @Override
    public Integer getThreadPoolSize() {
	return _threadPoolSize;
    }

    @Override
    public void setThreadPoolSize(int threadPoolSize) {
	if (threadPoolSize < 1 || threadPoolSize > THREAD_POOL_SIZE_LIMIT) {
	    threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
	}
	_threadPoolSize = threadPoolSize;
    }

}
