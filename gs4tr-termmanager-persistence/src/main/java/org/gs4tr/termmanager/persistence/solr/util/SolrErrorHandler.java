package org.gs4tr.termmanager.persistence.solr.util;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation3.solr.model.client.AddResponse;
import org.gs4tr.foundation3.solr.model.update.UpdateProcessorCommandInfo;
import org.gs4tr.foundation3.solr.model.update.UpdateProcessorInfo;
import org.gs4tr.foundation3.solr.model.update.UpdateProcessorSubInfo;
import org.gs4tr.termmanager.persistence.solr.impl.Messages;
import org.gs4tr.tm3.api.TmException;

public class SolrErrorHandler {

    public static void handleAddResponse(AddResponse res, int reqBatchSize) throws TmException {
	if (Objects.isNull(res)) {
	    return;
	}

	UpdateProcessorInfo info = res.getInfo();
	if (Objects.isNull(info)) {
	    return;
	}

	StringBuilder builder = new StringBuilder();

	List<String> unhandledExceptions = info.getUnhandledExceptions();
	if (CollectionUtils.isNotEmpty(unhandledExceptions)) {
	    unhandledExceptions.forEach(e -> appendErrorMessage(builder, e));
	}

	if (info.isPartial()) {
	    appendErrorMessage(builder, Messages.getString("TmgrGlossaryUpdater.11")); //$NON-NLS-1$
	}

	List<UpdateProcessorSubInfo> subInfos = info.getSubInfos();
	if (CollectionUtils.isNotEmpty(subInfos)) {

	    if (reqBatchSize != subInfos.size()) {
		appendErrorMessage(builder, String.format(Messages.getString("TmgrGlossaryUpdater.14"), //$NON-NLS-1$
			subInfos.size(), reqBatchSize));
	    }

	    for (UpdateProcessorSubInfo subInfo : subInfos) {

		String errorSubInfo = subInfo.getError();
		if (Objects.isNull(errorSubInfo)) {

		    for (UpdateProcessorCommandInfo cmdInfo : subInfo.getCommandInfos()) {
			String cmdError = cmdInfo.getError();
			if (Objects.nonNull(cmdError)) {
			    appendErrorMessage(builder, cmdError);
			}
		    }
		} else {
		    appendErrorMessage(builder, errorSubInfo);
		}
	    }
	}

	String message = Objects.nonNull(builder) ? builder.toString() : null;
	if (StringUtils.isNotEmpty(message)) {
	    throw new TmException(message);
	}
    }

    private static void appendErrorMessage(StringBuilder builder, String errorMessage) {
	if (Objects.isNull(builder)) {
	    builder = new StringBuilder();
	}

	builder.append(errorMessage);
	builder.append(StringConstants.NEW_LINE);
    }
}
