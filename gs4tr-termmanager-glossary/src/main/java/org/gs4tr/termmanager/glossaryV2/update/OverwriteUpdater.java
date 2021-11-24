package org.gs4tr.termmanager.glossaryV2.update;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.glossaryV2.GlossaryUpdateRequestExt;
import org.gs4tr.termmanager.glossaryV2.GlossaryV2Utils;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.glossary.Term;
import org.springframework.stereotype.Component;

@Component("overwriteUpdater")
public class OverwriteUpdater extends AbstractUpdater {

    @Override
    public BatchProcessResult update(GlossaryUpdateRequestExt request) {
	int size = request.size();

	List<String> termEntryIds = request.stream().filter(t -> StringUtils.isNotEmpty(t.getId())).map(Term::getId)
		.collect(Collectors.toList());

	if (CollectionUtils.isEmpty(termEntryIds)) {
	    return new BatchProcessResult(size, 0, null);
	}

	TermEntryService termEntryService = getTermEntryService();

	long projectId = request.getProjectId();

	Set<String> attributeTypes = new HashSet<>();

	List<TermEntry> termEntries = termEntryService.findTermentriesByIds(termEntryIds, projectId);

	List<TranslationUnit> tus = new ArrayList<>();

	String status = ItemStatusTypeHolder.PROCESSED.getName();

	for (TermEntry termEntry : termEntries) {
	    Term term = findTerm(request, termEntry);
	    if (term == null) {
		continue;
	    }

	    String sourceMarkerId = GlossaryV2Utils.findTermMarkerId(termEntry, status,
		    term.getSourceLocale().getCode());
	    String targetMarkerId = GlossaryV2Utils.findTermMarkerId(termEntry, status,
		    term.getTargetLocale().getCode());

	    tus.addAll(createTranslationUnits(CommandEnum.UPDATE, sourceMarkerId, targetMarkerId, term, attributeTypes,
		    request.getUsername()));
	}

	termEntryService.updateTermEntries(tus, request.getSourceLanguageId(), projectId, Action.EDITED_REMOTELY);
	updateProjectAttributes(projectId, attributeTypes);

	return new BatchProcessResult(size, tus.size(), null);
    }
}
