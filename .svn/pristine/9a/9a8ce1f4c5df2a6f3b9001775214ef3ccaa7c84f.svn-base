package org.gs4tr.termmanager.glossaryV2.update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.glossaryV2.GlossaryUpdateRequestExt;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.glossary.Term;
import org.springframework.stereotype.Component;

@Component("doNotOverwriteUpdater")
public class DoNotOverwriteUpdater extends AbstractUpdater {

    @Override
    public BatchProcessResult update(GlossaryUpdateRequestExt request) {
	int size = request.size();

	List<String> termEntryIds = request.stream().filter(t -> StringUtils.isNotEmpty(t.getId())).map(t -> t.getId())
		.collect(Collectors.toList());

	List<TermEntry> termEntries = getTermEntryService().findTermentriesByIds(termEntryIds, request.getProjectId());

	filterRequest(request, termEntries);

	return addInternal(request, size);
    }

    private void filterRequest(GlossaryUpdateRequestExt request, List<TermEntry> termEntries) {
	List<Term> terms = new ArrayList<>();
	for (Term term : request) {
	    TermEntry termEntry = findTermEntry(termEntries, term);
	    if (termEntry == null) {
		terms.add(term);
	    }
	}

	request.clear();
	request.addAll(terms);
    }
}
