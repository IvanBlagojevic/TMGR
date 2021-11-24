package org.gs4tr.termmanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BatchJobsInfo implements Serializable {

    private static final long serialVersionUID = 1273208652275716978L;

    private final List<BatchJobInfo> _batchJobsCompleted;

    private final List<BatchJobName> _batchJobsInProcess;

    public BatchJobsInfo() {
	_batchJobsInProcess = new ArrayList<>();
	_batchJobsCompleted = new ArrayList<>();
    }

    public List<BatchJobInfo> getBatchJobsCompleted() {
	return _batchJobsCompleted;
    }

    public List<BatchJobName> getBatchJobsInProcess() {
	return _batchJobsInProcess;
    }
}
