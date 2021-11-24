package org.gs4tr.termmanager.webservice.utils;

import static java.util.Collections.emptySet;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.ImportAttributeReplacement;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute;
import org.gs4tr.termmanager.webservice.model.request.ImportCommand;

public class V2Utils {

    public static TmProject getProject(Long projectId, ProjectService projectService) {

	try {

	    return projectService.load(projectId);

	} catch (Exception e) {
	    throw new RuntimeException(Messages.getString("V2Utils.1"), e); //$NON-NLS-1$
	}
    }

    public static String getProjectName(Long projectId, ProjectService projectService) {

	return getProjectName(getProject(projectId, projectService));
    }

    public static String getProjectName(TmProject project) {

	String projectName = project.getProjectInfo().getName();

	if (_logger.isDebugEnabled()) {
	    _logger.debug(String.format(Messages.getString("V2Utils.0"), projectName)); //$NON-NLS-1$
	}

	return projectName;
    }

    public static Map<String, Set<String>> resolveAllowedTermDescription(ImportCommand command) {

	Set<String> termDescriptions = new HashSet<>();
	Set<String> termNotes = new HashSet<>();

	Map<String, Set<String>> allowedTermDescriptions = new HashMap<>();
	Set<String> allowedTermAttributes = command.getAllowedTermAttributes();
	Set<String> allowedTermNotes = command.getAllowedTermNotes();

	if (isNotEmpty(allowedTermAttributes)) {
	    termDescriptions.addAll(allowedTermAttributes);
	}
	if (isNotEmpty(allowedTermNotes)) {
	    termNotes.addAll(allowedTermNotes);
	}

	allowedTermDescriptions.put(Description.ATTRIBUTE, termDescriptions);
	allowedTermDescriptions.put(Description.NOTE, termNotes);

	return allowedTermDescriptions;

    }

    public static Set<String> resolveAllowedTermEntryDescriptions(ImportCommand command) {

	Set<String> termEntryDescriptions = command.getAllowedTermEntryAttributes();

	return isNotEmpty(termEntryDescriptions) ? termEntryDescriptions : emptySet();

    }

    public static void resolveAttributeReplacements(ImportOptionsModel model,
	    List<ImportAttributeReplacement> attributeReplacements) {

	Map<String, Map<String, String>> termAttributeReplacements = new HashMap<>(2);
	Map<String, String> termEntryAttributeReplacements = new HashMap<>();

	if (isNotEmpty(attributeReplacements)) {

	    String termEntryLevel = ImportAttribute.Level.TERM_ENTRY.name();
	    String termDescriptionLevel = ImportAttribute.Level.TERM_ATTRIBUTE.name();
	    String termNoteLevel = ImportAttribute.Level.TERM_NOTE.name();

	    for (ImportAttributeReplacement attributeReplacement : attributeReplacements) {

		String importAttributeName = attributeReplacement.getImportAttributeName();
		String replacement = attributeReplacement.getReplacement();
		final String level = attributeReplacement.getLevel();

		if (termEntryLevel.equals(level)) {
		    termEntryAttributeReplacements.put(importAttributeName, replacement);
		} else if (termDescriptionLevel.equals(level)) {
		    termAttributeReplacements.computeIfAbsent(Description.ATTRIBUTE, key -> new HashMap<>())
			    .put(importAttributeName, replacement);
		} else if (termNoteLevel.equals(level)) {
		    termAttributeReplacements.computeIfAbsent(Description.NOTE, key -> new HashMap<>())
			    .put(importAttributeName, replacement);
		}
	    }
	}

	model.setTermEntryAttributeReplacements(termEntryAttributeReplacements);
	model.setAttributeNoteReplacements(termAttributeReplacements);

    }

    private static Log _logger = LogFactory.getLog(V2Utils.class);

}