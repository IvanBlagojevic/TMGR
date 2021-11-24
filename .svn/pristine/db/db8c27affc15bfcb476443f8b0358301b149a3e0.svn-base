package org.gs4tr.termmanager.model.dto;

import java.io.File;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("exportInfo")
public class ExportInfo {

    @XStreamOmitField
    private File _tempFile;

    @XStreamAlias("totalExported")
    private int _totalExported;

    public File getTempFile() {
	return _tempFile;
    }

    public int getTotalExported() {
	return _totalExported;
    }

    public void setTempFile(File tempFile) {
	_tempFile = tempFile;
    }

    public void setTotalExported(int totalExported) {
	_totalExported = totalExported;
    }

}
