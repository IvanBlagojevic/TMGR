package org.gs4tr.termmanager.service.solr.restore.model;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.dao.ProjectLanguageDAO;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.service.ProjectService;

import edu.emory.mathcs.backport.java.util.Collections;

public class RecodeOrCloneCommandParser extends PropertyEditorSupport {

    private static final List<String> AVAILABLE_LOCALES = initLocales();

    private List<Long> _disabledProjects;

    private ProjectLanguageDAO _projectLanguageDAO;

    private ProjectService _projectService;

    public RecodeOrCloneCommandParser(ProjectService projectService, ProjectLanguageDAO projectLanguageDAO,
	    List<Long> disabledProjects) {
	super();
	setProjectService(projectService);
	setProjectLanguageDAO(projectLanguageDAO);
	setDisabledProjects(disabledProjects);
    }

    @Override
    public void setAsText(String propertiesText) {

	setValue(Collections.emptyList());

	if (StringUtils.isEmpty(propertiesText)) {
	    return;
	}

	String[] commandsText = propertiesText.split(StringConstants.SEMICOLON);

	List<RecodeOrCloneCommand> commands = new ArrayList<>();

	for (String commandText : commandsText) {
	    RecodeOrCloneCommand command = getCommand(commandText);
	    if (Objects.isNull(command)) {
		setValue(Collections.emptyList());
		throw new RuntimeException("Invalid recode or clone operation");
	    }

	    validateCommandLanguages(command);

	    commands.add(command);
	}
	setValue(commands);
    }

    private Long findProjectIdByShortCode(String projectShortCode) {
	if (StringUtils.isEmpty(projectShortCode)) {
	    throw new RuntimeException("locale.properties has property with empty project short code");
	}

	List<Long> projectIds = getProjectService()
		.findProjectIdsByShortCodes(java.util.Collections.singletonList(projectShortCode));

	if (CollectionUtils.isEmpty(projectIds)) {
	    String message = String.format("Can't find projectId for project short code: [%s]", projectShortCode);
	    throw new RuntimeException(message);
	}

	return projectIds.get(0);

    }

    private RecodeOrCloneCommand getCommand(String commandProperties) {

	validatePropertyString(commandProperties);

	RecodeOrCloneCommand command = new RecodeOrCloneCommand();

	String[] commandFields = commandProperties.split(StringConstants.COMMA);

	if (commandFields.length != 3) {
	    throw new RuntimeException("locale.properties has invalid property");
	}

	String localeFrom = commandFields[1];
	String localeTo = commandFields[2];

	validateIncomingPropertyLocales(localeFrom, localeTo);

	String projectShortCode = commandFields[0];

	Long projectId = findProjectIdByShortCode(projectShortCode);

	validateProjectDisabled(projectId, projectShortCode);

	command.setProjectShortCode(projectShortCode);
	command.setProjectId(projectId);
	command.setLocaleFrom(localeFrom);
	command.setLocaleTo(localeTo);

	return command;
    }

    private List<Long> getDisabledProjects() {
	return _disabledProjects;
    }

    private ProjectLanguageDAO getProjectLanguageDAO() {
	return _projectLanguageDAO;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private static List<String> initLocales() {

	Locale[] locales = Locale.getAvailableLocales();

	Stream<Locale> localesStream = Arrays.stream(locales);
	return localesStream.map(Locale::getCode).collect(Collectors.toList());
    }

    private boolean isProjectDisabled(Long projectId) {
	List<Long> disabledProjectsIds = getDisabledProjects();

	if (Objects.isNull(disabledProjectsIds)) {
	    return false;
	}

	return disabledProjectsIds.contains(projectId);
    }

    private void setDisabledProjects(List<Long> disabledProjects) {
	_disabledProjects = disabledProjects;
    }

    private void setProjectLanguageDAO(ProjectLanguageDAO projectLanguageDAO) {
	_projectLanguageDAO = projectLanguageDAO;
    }

    private void setProjectService(ProjectService projectService) {
	_projectService = projectService;
    }

    private void validateCommandLanguages(RecodeOrCloneCommand command) {

	ProjectLanguageDAO projectLanguageDAO = getProjectLanguageDAO();
	Set<String> projectLangCodes = projectLanguageDAO.getLanguageIdsByProjectId(command.getProjectId());

	String localeFrom = command.getLocaleFrom();
	String localeTo = command.getLocaleTo();
	Long projectId = command.getProjectId();

	if (!projectLangCodes.contains(localeFrom)) {
	    String message = String.format("Project [%s] don't have locale [%s]", projectId.toString(), localeFrom);
	    throw new RuntimeException(message);
	}

	if (projectLangCodes.contains(localeTo)) {
	    String message = String.format("Project [%s] already have locale [%s]", projectId.toString(), localeTo);
	    throw new RuntimeException(message);
	}
    }

    private void validateIncomingPropertyLocales(String localeFrom, String localeTo) {
	validateLocaleFromProperty(localeFrom);
	validateLocaleFromProperty(localeTo);
	if (localeFrom.equals(localeTo)) {
	    throw new RuntimeException("locale.properties locale from and locale to can't be the same");
	}
    }

    private void validateLocaleFromProperty(String locale) {

	boolean isEmptyLocale = StringUtils.isEmpty(locale);
	boolean isNotAvailableLocale = !AVAILABLE_LOCALES.contains(locale);

	if (isEmptyLocale) {
	    throw new RuntimeException("locale.properties has property with empty locale");
	}

	if (isNotAvailableLocale) {
	    String message = String.format("locale.properties has property with locale: [%s] that not exists", locale);
	    throw new RuntimeException(message);
	}
    }

    private void validateProjectDisabled(Long projectId, String projectShortCode) {
	if (isProjectDisabled(projectId)) {
	    String message = String.format("locale.properties has project [%s] that is disabled", projectShortCode);
	    throw new RuntimeException(message);
	}
    }

    private void validatePropertyString(String propertiesText) {
	if (StringUtils.isEmpty(propertiesText)) {
	    throw new RuntimeException("locale.properties property string is not valid");
	}
    }
}
