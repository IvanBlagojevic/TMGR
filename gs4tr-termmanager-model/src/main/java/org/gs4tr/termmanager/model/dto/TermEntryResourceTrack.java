package org.gs4tr.termmanager.model.dto;

public class TermEntryResourceTrack {

    private String _name;
    private String _fileName;
    private String _resourceTicket;
    private String _mediaType;
    private String _format;

    public String getFileName() {
	return _fileName;
    }

    public String getFormat() {
	return _format;
    }

    public String getMediaType() {
	return _mediaType;
    }

    public String getName() {
	return _name;
    }

    public String getResourceTicket() {
	return _resourceTicket;
    }

    public void setFileName(String fileName) {
	_fileName = fileName;
    }

    public void setFormat(String format) {
	_format = format;
    }

    public void setMediaType(String mediaType) {
	_mediaType = mediaType;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setResourceTicket(String resourceTicket) {
	_resourceTicket = resourceTicket;
    }

}
