package org.gs4tr.termmanager.glossaryV2.update;

import org.gs4tr.termmanager.glossaryV2.GlossaryUpdateRequestExt;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.springframework.stereotype.Component;

@Component("skipUpdater")
public class SkipUpdater implements GlossaryUpdater {

    @Override
    public BatchProcessResult update(GlossaryUpdateRequestExt request) {
	return new BatchProcessResult(request.size(), 0, null);
    }
}
