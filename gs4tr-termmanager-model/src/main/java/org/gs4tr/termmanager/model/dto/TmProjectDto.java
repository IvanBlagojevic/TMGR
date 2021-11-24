package org.gs4tr.termmanager.model.dto;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;

/**
 * Data transfer object used to transfer <code>TmProject</code> and
 * <code>ProjectLanguage</code> data to UI.
 * 
 * @since 5.0
 */
public class TmProjectDto {

    public static class TermStatusDto {
	private final String _name;
	private final String _value;

	public TermStatusDto(ItemStatusType statusType) {
	    _name = statusType.getName();
	    _value = statusType.getName();
	}

	public String getName() {
	    return _name;
	}

	public String getValue() {
	    return _value;
	}
    }

    private TermStatusDto _defaultTermStatus;
    private final List<ProjectLanguage> _projectLanguages;
    private final String _projectName;
    private final Ticket _projectTicket;
    private final List<TermStatusDto> _termStatusDtos;

    public TmProjectDto(String projectName, Long projectId) {
	_projectLanguages = new ArrayList<>();
	_projectTicket = new Ticket(projectId);
	_projectName = projectName;
	_termStatusDtos = new ArrayList<>();
    }

    public void addLanguage(String languageCode) {
	Language languageDto = LanguageConverter.fromInternalToDto(languageCode);
	ProjectLanguage projectLanguage = new ProjectLanguage();
	projectLanguage.setLanguage(languageDto);
	getProjectLanguages().add(projectLanguage);
    }

    public void addTermStatus(TermStatusDto termStatusDto) {
	getTermStatuses().add(termStatusDto);
    }

    public TermStatusDto getDefaultTermStatus() {
	return _defaultTermStatus;
    }

    public List<ProjectLanguage> getProjectLanguages() {
	return _projectLanguages;
    }

    public String getProjectName() {
	return _projectName;
    }

    public Ticket getProjectTicket() {
	return _projectTicket;
    }

    public List<TermStatusDto> getTermStatuses() {
	return _termStatusDtos;
    }

    public void setDefaultTermStatus(TermStatusDto termStatusDto) {
	_defaultTermStatus = termStatusDto;
    }
}
