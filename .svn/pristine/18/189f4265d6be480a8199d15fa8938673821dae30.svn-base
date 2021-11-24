package org.gs4tr.termmanager.glossaryV2.blacklist.update;

import java.util.Arrays;

import org.gs4tr.termmanager.glossaryV2.blacklist.BlacklistUpdateRequestExt;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.springframework.stereotype.Component;

@Component("blacklistOverwriteIdenticalAttributesUpdater")
public class BlacklistOverwriteIdenticalAttributesUpdater implements BlacklistUpdater {

    @Override
    public BatchProcessResult update(BlacklistUpdateRequestExt request) {
	String message = String.format(Messages.getString("BlacklistOverwriteIdenticalAttributesUpdater.0"), //$NON-NLS-1$
		request.getOption().name());
	return new BatchProcessResult(request.size(), 0, null, Arrays.asList(message));
    }
}
