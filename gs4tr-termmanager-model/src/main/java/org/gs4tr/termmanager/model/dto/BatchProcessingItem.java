package org.gs4tr.termmanager.model.dto;

public class BatchProcessingItem {

    private String[] _alertMessages;

    private String _batchProcess;
    private String _exceptionMessage;
    private String _longSucessMessage;
    private String _msgLongSubTitle;
    private String _msgSubTitle;
    private String _resourceTrack;
    private String _sucessMessage;
    private String _title;

    public String[] getAlertMessages() {
	return _alertMessages;
    }

    public String getBatchProcess() {
	return _batchProcess;
    }

    public String getExceptionMessage() {
	return _exceptionMessage;
    }

    public String getLongSucessMessage() {
	return _longSucessMessage;
    }

    public String getMsgLongSubTitle() {
	return _msgLongSubTitle;
    }

    public String getMsgSubTitle() {
	return _msgSubTitle;
    }

    public String getResourceTrack() {
	return _resourceTrack;
    }

    public String getSucessMessage() {
	return _sucessMessage;
    }

    public String getTitle() {
	return _title;
    }

    public void setAlertMessages(String[] alertMessages) {
	_alertMessages = alertMessages;
    }

    public void setBatchProcess(String batchProcess) {
	_batchProcess = batchProcess;
    }

    public void setExceptionMessage(String exceptionMessage) {
	_exceptionMessage = exceptionMessage;
    }

    public void setLongSucessMessage(String longSucessMessage) {
	_longSucessMessage = longSucessMessage;
    }

    public void setMsgLongSubTitle(String msgLongSubTitle) {
	_msgLongSubTitle = msgLongSubTitle;
    }

    public void setMsgSubTitle(String msgSubTitle) {
	_msgSubTitle = msgSubTitle;
    }

    public void setResourceTrack(String resourceTrack) {
	_resourceTrack = resourceTrack;
    }

    public void setSucessMessage(String sucessMessage) {
	_sucessMessage = sucessMessage;
    }

    public void setTitle(String title) {
	_title = title;
    }
}
