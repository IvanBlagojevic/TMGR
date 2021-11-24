package org.gs4tr.termmanager.glossaryV2.update;

import java.util.Arrays;

import org.gs4tr.termmanager.glossaryV2.GlossaryUpdateRequestExt;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.springframework.stereotype.Component;

@Component("overwriteIdenticalAttributesUpdater")
public class OverwriteIdenticalAttributesUpdater implements GlossaryUpdater {

    @Override
    public BatchProcessResult update(GlossaryUpdateRequestExt request) {
	String message = String.format(Messages.getString("OverwriteIndenticalAttributesUpdater.1"), //$NON-NLS-1$
		request.getOption().name());

	return new BatchProcessResult(request.size(), 0, null, Arrays.asList(message));
    }
}
