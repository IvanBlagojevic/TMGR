package org.gs4tr.termmanager.glossaryV2.blacklist.update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.glossaryV2.blacklist.BlacklistUpdateRequestExt;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.blacklist.BlacklistTerm;
import org.springframework.stereotype.Component;

@Component("blacklistDoNotOverwriteUpdater")
public class BlacklistDoNotOverwriteUpdater extends AbstractBlacklistUpdater {

    @Override
    public BatchProcessResult update(BlacklistUpdateRequestExt request) {
	int size = request.size();

	List<String> termEntryIds = request.stream().filter(t -> StringUtils.isNotEmpty(t.getId())).map(t -> t.getId())
		.collect(Collectors.toList());

	List<TermEntry> termEntries = getTermEntryService().findTermentriesByIds(termEntryIds, request.getProjectId());

	filterRequest(request, termEntries);

	return addInternal(request, size);
    }

    private void filterRequest(BlacklistUpdateRequestExt request, List<TermEntry> termEntries) {
	List<BlacklistTerm> terms = new ArrayList<BlacklistTerm>();
	for (BlacklistTerm term : request) {
	    TermEntry termEntry = findTermEntry(termEntries, term);
	    if (termEntry == null) {
		terms.add(term);
	    }
	}

	request.clear();
	request.addAll(terms);
    }
}
