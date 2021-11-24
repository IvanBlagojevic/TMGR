package org.gs4tr.termmanager.glossaryV2.update;

import org.gs4tr.termmanager.glossaryV2.GlossaryUpdateRequestExt;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.springframework.stereotype.Component;

@Component("appendUpdater")
public class AppendUpdater extends AbstractUpdater {

    @Override
    public BatchProcessResult update(GlossaryUpdateRequestExt request) {
	return addInternal(request, request.size());
    }
}
