package org.gs4tr.termmanager.glossaryV2.mbeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@ManagedResource(objectName = "tmgr.management:name=GlossaryV2SegmentSearchThreadPoolSize")
@Component(value = "changeSegmentSearchThreadPoolSize")
public class SegmentSearchThreadPoolSizeMBean {

    @Autowired
    private SegmentSearchThreadPoolSize _segmentSearchThreadPoolSize;

    @ManagedAttribute(description = "Segment Search Thread Pool Size")
    public Integer getThreadPoolSize() {
	return getSegmentSearchThreadPoolSize().getThreadPoolSize();
    }

    @ManagedOperation(description = "Segment Search Thread Pool Size")
    @ManagedOperationParameters({
	    @ManagedOperationParameter(name = "segmentSearchThreadPoolSize", description = "Segment Search Thread Pool Size") })
    public void updateThreadPoolSize(Integer segmentSearchThreadPoolSize) {
	getSegmentSearchThreadPoolSize().setThreadPoolSize(segmentSearchThreadPoolSize);
    }

    private SegmentSearchThreadPoolSize getSegmentSearchThreadPoolSize() {
	return _segmentSearchThreadPoolSize;
    }
}
