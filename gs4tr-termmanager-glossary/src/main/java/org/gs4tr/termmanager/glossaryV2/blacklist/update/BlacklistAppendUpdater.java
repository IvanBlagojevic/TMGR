package org.gs4tr.termmanager.glossaryV2.blacklist.update;

import org.gs4tr.termmanager.glossaryV2.blacklist.BlacklistUpdateRequestExt;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.springframework.stereotype.Component;

@Component("blacklistAppendUpdater")
public class BlacklistAppendUpdater extends AbstractBlacklistUpdater {

    @Override
    public BatchProcessResult update(BlacklistUpdateRequestExt request) {
	return addInternal(request, request.size());
    }
}
