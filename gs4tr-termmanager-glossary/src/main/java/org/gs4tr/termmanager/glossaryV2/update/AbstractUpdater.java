package org.gs4tr.termmanager.glossaryV2.update;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.glossaryV2.GlossaryUpdateRequestExt;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.glossary.Term;
import org.gs4tr.tm3.api.glossary.TermAttribute;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractUpdater implements GlossaryUpdater {

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermEntryService _termEntryService;

    private List<UpdateCommand> createAttributeCommands(List<TermAttribute> attributes, Set<String> attributeTypes,
	    String localeCode, CommandEnum cmd, String markerId) {
	List<UpdateCommand> attCmds = new ArrayList<>();
	if (CollectionUtils.isNotEmpty(attributes)) {
	    for (TermAttribute attribute : attributes) {
		String attributeType = attribute.getType();
		String attributeValue = attribute.getValue();
		if (StringUtils.isEmpty(attributeValue)) {
		    continue;
		}
		UpdateCommand attCmd = createCommand(cmd, TypeEnum.DESCRIP, attributeType, markerId, localeCode,
			attributeValue);
		attCmds.add(attCmd);

		attributeTypes.add(attributeType);
	    }
	}
	return attCmds;
    }

    private UpdateCommand createCommand(CommandEnum cmd, TypeEnum type, String subType, String parentMarkerId,
	    String languageId, String value) {
	return createCommand(cmd, type, subType, parentMarkerId, UUID.randomUUID().toString(), languageId, value);
    }

    private UpdateCommand createCommand(CommandEnum cmd, TypeEnum type, String subType, String parentMarkerId,
	    String markerId, String languageId, String value) {
	UpdateCommand command = new UpdateCommand();
	command.setCommand(cmd.getName());
	command.setItemType(type.getName());
	command.setLanguageId(languageId);
	command.setMarkerId(markerId);
	command.setParentMarkerId(parentMarkerId);
	command.setValue(value);
	command.setSubType(subType);
	return command;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    protected BatchProcessResult addInternal(GlossaryUpdateRequestExt request, int size) {
	Set<String> attributeTypes = new HashSet<>();

	List<TranslationUnit> tus = new ArrayList<>();
	for (Term term : request) {
	    String sourceMarkerId = UUID.randomUUID().toString();
	    String targetMarkerId = UUID.randomUUID().toString();
	    tus.addAll(createTranslationUnits(CommandEnum.ADD, sourceMarkerId, targetMarkerId, term, attributeTypes,
		    request.getUsername()));
	}

	long projectId = request.getProjectId();

	getTermEntryService().updateTermEntries(tus, request.getSourceLanguageId(), projectId, Action.ADDED_REMOTELY);
	updateProjectAttributes(projectId, attributeTypes);

	return new BatchProcessResult(size, tus.size(), null);
    }

    protected List<TranslationUnit> createTranslationUnits(CommandEnum cmd, String sourceMarkerId,
	    String targetMarkerId, Term term, Set<String> attributeTypes, String username) {
	String termEntryId = term.getId();

	TranslationUnit tu = new TranslationUnit();
	tu.setTermEntryId(termEntryId);

	// create source update commands
	String sourceCode = term.getSourceLocale().getCode();
	UpdateCommand srcCmd = createCommand(cmd, TypeEnum.TERM, null, termEntryId, sourceMarkerId, sourceCode,
		term.getSource());
	tu.addSourceTermUpdateCommand(srcCmd);

	List<UpdateCommand> srcAttributeCommands = createAttributeCommands(term.getSourceAttributes(), attributeTypes,
		sourceCode, cmd, sourceMarkerId);
	srcAttributeCommands.forEach(tu::addSourceTermUpdateCommand);

	// create target update commands
	String targetCode = term.getTargetLocale().getCode();
	UpdateCommand trgCmd = createCommand(cmd, TypeEnum.TERM, null, termEntryId, targetMarkerId, targetCode,
		term.getTarget());
	tu.addTargetTermUpdateCommand(trgCmd);

	List<UpdateCommand> trgAttributeCommands = createAttributeCommands(term.getTargetAttributes(), attributeTypes,
		targetCode, cmd, targetMarkerId);
	trgAttributeCommands.forEach(tu::addTargetTermUpdateCommand);

	tu.setUsername(username);

	List<TranslationUnit> tus = new ArrayList<>();
	tus.add(tu);

	return tus;
    }

    protected Term findTerm(List<Term> terms, TermEntry termEntry) {
	String termEntryId = termEntry.getUuId();
	for (Term term : terms) {
	    if (termEntryId.equals(term.getId())) {
		return term;
	    }
	}
	return null;
    }

    protected TermEntry findTermEntry(List<TermEntry> termEntries, Term term) {
	String termEntryId = term.getId();
	for (TermEntry termEntry : termEntries) {
	    if (termEntryId.equals(termEntry.getUuId())) {
		return termEntry;
	    }
	}
	return null;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    protected void updateProjectAttributes(long projectId, Set<String> attributeTypes) {
	if (!attributeTypes.isEmpty()) {
	    EnumMap<AttributeLevelEnum, Set<String>> projectAttributeMap = new EnumMap<>(AttributeLevelEnum.class);
	    projectAttributeMap.put(AttributeLevelEnum.LANGUAGE, attributeTypes);
	    getProjectService().addOrUpdateProjectAttributesOnImport(projectId, projectAttributeMap, true);
	}
    }
}
