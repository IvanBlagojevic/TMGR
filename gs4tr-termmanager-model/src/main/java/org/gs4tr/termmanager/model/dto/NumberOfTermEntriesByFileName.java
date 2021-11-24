package org.gs4tr.termmanager.model.dto;

public class NumberOfTermEntriesByFileName {

    private String _fileName;
    private int _numberOfTermEntries;

    public String getFileName() {
	return _fileName;
    }

    public int getNumberOfTermEntries() {
	return _numberOfTermEntries;
    }

    public void setFileName(String fileName) {
	_fileName = fileName;
    }

    public void setNumberOfTermEntries(int numberOfTermEntries) {
	_numberOfTermEntries = numberOfTermEntries;
    }
}
