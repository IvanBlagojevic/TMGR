package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.service.model.command.ImportReportCommand;

public class DtoImportReportCommand implements DtoTaskHandlerCommand<ImportReportCommand> {

    private String[] _importThreadNames;

    private String _projectTicket;

    @Override
    public ImportReportCommand convertToInternalTaskHandlerCommand() {
	ImportReportCommand cmd = new ImportReportCommand();

	String projectTicket = getProjectTicket();
	if (StringUtils.isNotEmpty(projectTicket)) {
	    cmd.setProjectId(IdEncrypter.decryptGenericId(projectTicket));
	}

	String[] importThreadNames = getImportThreadNames();
	if (Objects.nonNull(importThreadNames)) {
	    cmd.setImportThreadNames(new HashSet<>(Arrays.asList(importThreadNames)));
	}

	return cmd;
    }

    public String[] getImportThreadNames() {
	return _importThreadNames;
    }

    public String getProjectTicket() {
	return _projectTicket;
    }

    public void setImportThreadNames(String[] importThreadNames) {
	_importThreadNames = importThreadNames;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }
}
