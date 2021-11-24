package org.gs4tr.termmanager.service.impl;

import java.io.Serializable;

import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.service.utils.ServiceUtils;

public class ExportAdapter implements ExportNotificationCallback, ExportDocumentStatusInfo, Serializable {

    private static final long serialVersionUID = -8864150322115784313L;

    private long _endTime;

    private ExportInfo _exportInfo;

    private boolean _processingFinished = false;

    private long _startTime;

    private long _termEntryProcessed;

    private String _threadName;

    private long _totalTermEntryCount;

    public ExportAdapter() {
    }

    public ExportAdapter(String threadName, long startTime) {
	_threadName = threadName;
	_startTime = startTime;
    }

    @Override
    public ExportInfo getExportInfo() {
	return _exportInfo;
    }

    @Override
    public int getProcessingProgress() {
	return (int) (((double) getTermEntryProcessed() / getTotalTermEntryCount()) * 100);
    }

    @Override
    public String getThreadName() {
	return _threadName;
    }

    @Override
    public String getTotalExportTimeFormatted() {
	return ServiceUtils.getTotalTimeFormatted(getEndTime(), getStartTime());
    }

    @Override
    public long getTotalExportTimeInMillis() {
	return getEndTime() - getStartTime();
    }

    @Override
    public void init(long totalNumberOfItems) {
	setTotalTermEntryCount(totalNumberOfItems);
    }

    @Override
    public boolean isProcessingFinished() {
	return _processingFinished;
    }

    @Override
    public void notifyItemProcessingFinished() {
	_termEntryProcessed++;
    }

    @Override
    public void notifyProcessingFinished(ExportInfo exportInfo) {
	setEndTime(System.currentTimeMillis());
	setExportInfo(exportInfo);
	setProcessingFinished(true);
    }

    private long getEndTime() {
	return _endTime;
    }

    private long getStartTime() {
	return _startTime;
    }

    private long getTermEntryProcessed() {
	return _termEntryProcessed;
    }

    private long getTotalTermEntryCount() {
	return _totalTermEntryCount;
    }

    private void setEndTime(long endTime) {
	_endTime = endTime;
    }

    private void setExportInfo(ExportInfo exportInfo) {
	_exportInfo = exportInfo;
    }

    private void setProcessingFinished(boolean processingFinished) {
	_processingFinished = processingFinished;
    }

    private void setTotalTermEntryCount(long totalTermEntryCount) {
	_totalTermEntryCount = totalTermEntryCount;
    }
}
