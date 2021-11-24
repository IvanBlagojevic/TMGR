package org.gs4tr.termmanager.glossaryV2.blacklist.update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.glossaryV2.GlossaryV2Utils;
import org.gs4tr.termmanager.glossaryV2.blacklist.BlacklistUpdateRequestExt;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.blacklist.BlacklistTerm;
import org.springframework.stereotype.Component;

@Component("blacklistOverwriteUpdater")
public class BlacklistOverwriteUpdater extends AbstractBlacklistUpdater {

    @Override
    public BatchProcessResult update(BlacklistUpdateRequestExt request) {
	int size = request.size();

	List<String> termEntryIds = request.stream().filter(t -> StringUtils.isNotEmpty(t.getId())).map(t -> t.getId())
		.collect(Collectors.toList());

	if (CollectionUtils.isEmpty(termEntryIds)) {
	    return new BatchProcessResult(size, 0, null);
	}

	TermEntryService termEntryService = getTermEntryService();

	List<TermEntry> termEntries = termEntryService.findTermentriesByIds(termEntryIds, request.getProjectId());

	List<TranslationUnit> tus = new ArrayList<TranslationUnit>();

	String status = ItemStatusTypeHolder.BLACKLISTED.getName();

	for (TermEntry termEntry : termEntries) {
	    BlacklistTerm term = findTerm(request, termEntry);
	    if (term == null) {
		continue;
	    }

	    String termMarkerId = GlossaryV2Utils.findTermMarkerId(termEntry, status, term.getLocale().getCode());

	    tus.addAll(createTranslationUnits(CommandEnum.UPDATE, termMarkerId, term));
	}

	termEntryService.updateTermEntries(tus, request.getSourceLanguageId(), request.getProjectId(),
		Action.EDITED_REMOTELY);

	return new BatchProcessResult(size, tus.size(), null);
    }
}
