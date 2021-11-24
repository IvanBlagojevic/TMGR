package org.gs4tr.termmanager.model;

import java.io.Serializable;

public class BatchJobInfo implements Serializable {

    private static final long serialVersionUID = -5111214260739454961L;

    private String[] _alertMessages;

    private BatchJobName _batchJobName;
    private boolean _displayPopup;
    private boolean _displayPopupOnFail;
    private String _exceptionMessage;
    private String _longSuccessMessage;
    private String _msgLongSubTitle;
    private String _msgSubTitle;
    private String _projectName;
    private String _resourceTrack;
    private String _successMessage;
    private String _title;

    public BatchJobInfo() {
    }

    public BatchJobInfo(BatchJobName batchJobName) {
	_batchJobName = batchJobName;
    }

    public BatchJobInfo(BatchJobInfo batchProcessingItem) {
	_alertMessages = batchProcessingItem.getAlertMessages();

	_batchJobName = batchProcessingItem.getBatchJobName();

	_resourceTrack = batchProcessingItem.getResourceTrack();

	_successMessage = batchProcessingItem.getSuccessMessage();

	_longSuccessMessage = batchProcessingItem.getLongSuccessMessage();

	_title = batchProcessingItem.getTitle();

	_msgSubTitle = batchProcessingItem.getMsgSubTitle();

	_msgLongSubTitle = batchProcessingItem.getMsgLongSubTitle();

	_exceptionMessage = batchProcessingItem.getExceptionMessage();

	_displayPopup = batchProcessingItem.isDisplayPopup();

	_displayPopupOnFail = batchProcessingItem.isDisplayPopupOnFail();

	_projectName = batchProcessingItem.getProjectName();

    }

    public String[] getAlertMessages() {
	return _alertMessages;
    }

    public BatchJobName getBatchJobName() {
	return _batchJobName;
    }

    public String getExceptionMessage() {
	return _exceptionMessage;
    }

    public String getLongSuccessMessage() {
	return _longSuccessMessage;
    }

    public String getMsgLongSubTitle() {
	return _msgLongSubTitle;
    }

    public String getMsgSubTitle() {
	return _msgSubTitle;
    }

    public String getProjectName() {
	return _projectName;
    }

    public String getResourceTrack() {
	return _resourceTrack;
    }

    public String getSuccessMessage() {
	return _successMessage;
    }

    public String getTitle() {
	return _title;
    }

    public boolean isDisplayPopup() {
	return _displayPopup;
    }

    public boolean isDisplayPopupOnFail() {
	return _displayPopupOnFail;
    }

    public void setAlertMessages(String[] alertMessages) {
	_alertMessages = alertMessages;
    }

    public void setBatchJobName(BatchJobName batchJobName) {
	_batchJobName = batchJobName;
    }

    public void setDisplayPopup(boolean displayPopup) {
	_displayPopup = displayPopup;
    }

    public void setDisplayPopupOnFail(boolean displayPopupOnFail) {
	_displayPopupOnFail = displayPopupOnFail;
    }

    public void setExceptionMessage(String exceptionMessage) {
	_exceptionMessage = exceptionMessage;
    }

    public void setLongSuccessMessage(String longSuccessMessage) {
	_longSuccessMessage = longSuccessMessage;
    }

    public void setMsgLongSubTitle(String msgLongSubTitle) {
	_msgLongSubTitle = msgLongSubTitle;
    }

    public void setMsgSubTitle(String msgSubTitle) {
	_msgSubTitle = msgSubTitle;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setResourceTrack(String resourceTrack) {
	_resourceTrack = resourceTrack;
    }

    public void setSuccessMessage(String successMessage) {
	_successMessage = successMessage;
    }

    public void setTitle(String title) {
	_title = title;
    }
}
