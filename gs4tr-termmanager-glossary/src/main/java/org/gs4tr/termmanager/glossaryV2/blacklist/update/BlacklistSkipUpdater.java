package org.gs4tr.termmanager.glossaryV2.blacklist.update;

import org.gs4tr.termmanager.glossaryV2.blacklist.BlacklistUpdateRequestExt;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.springframework.stereotype.Component;

@Component("blacklistSkipUpdater")
public class BlacklistSkipUpdater implements BlacklistUpdater {

    @Override
    public BatchProcessResult update(BlacklistUpdateRequestExt request) {
	return new BatchProcessResult(request.size(), 0, null);
    }
}
