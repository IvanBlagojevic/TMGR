package org.gs4tr.termmanager.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.cache.entry.processor.executor.HzEntryProcessorExecutor;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.TermEntryExporter;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.service.export.ExportTermsCallback;
import org.gs4tr.termmanager.service.termentry.synchronization.EqualsBuilderHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.map.AbstractEntryProcessor;

@Component
public class TermEntryExporterImpl implements TermEntryExporter {

    private final EqualsBuilderHelper _equalsBuilder;

    @Autowired
    private HzEntryProcessorExecutor _hzEntryProcessorExecutor;

    private final Set<String> _requestedThreadsToStop = new HashSet<>();

    public TermEntryExporterImpl() {
	_equalsBuilder = new EqualsBuilderHelper();
    }

    @Override
    public Boolean exportInternal(List<TermEntry> termEntries, ExportNotificationCallback exportTbxNotificationCallback,
	    ExportDocumentFactory exportDocumentFactory, ExportInfo exportInfo) throws IOException {
	String threadName = exportTbxNotificationCallback.getThreadName();
	Boolean cancelRequested = isCancelRequested(threadName);
	if (cancelRequested) {
	    notifyCancelFinished(threadName);
	    return Boolean.TRUE;
	}

	ExportTermsCallback exportTermsCallback = termEntry -> new ArrayList<>(termEntry.ggetTerms());

	for (TermEntry termEntry : termEntries) {
	    if (isExportable(termEntry, exportInfo)) {
		exportDocumentFactory.write(termEntry, exportInfo, false, exportTermsCallback);
	    }
	    exportTbxNotificationCallback.notifyItemProcessingFinished();
	    getHzEntryProcessorExecutor().executeOnKey(CacheName.EXPORT_PROGRESS_STATUS, threadName,
		    new NotifyItemProcessingFinishedEntryProcessor());
	}
	return Boolean.FALSE;
    }

    @Override
    public void requestStopExport(String threadName) {
	addToRequestedSet(threadName);
    }

    private void addToRequestedSet(String threadName) {
	getRequestedThreadsToStop().add(threadName);
    }

    private EqualsBuilderHelper getEqualsBuilder() {
	return _equalsBuilder;
    }

    private HzEntryProcessorExecutor getHzEntryProcessorExecutor() {
	return _hzEntryProcessorExecutor;
    }

    private Set<String> getRequestedThreadsToStop() {
	return _requestedThreadsToStop;
    }

    private Boolean isCancelRequested(String threadName) {
	return getRequestedThreadsToStop().contains(threadName);
    }

    private boolean isExportable(TermEntry termEntry, ExportInfo exportInfo) {
	Set<Description> requiredDescriptions = exportInfo.getDescriptionFilter();
	if (CollectionUtils.isEmpty(requiredDescriptions)) {
	    return true;
	}

	EqualsBuilderHelper builder = getEqualsBuilder();
	if (builder.containsDescription(requiredDescriptions, termEntry.getDescriptions())) {
	    return true;
	}

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	if (Objects.isNull(languageTerms)) {
	    return false;
	}

	List<String> languageIds = exportInfo.getLanguageIds();
	for (String languageId : languageIds) {
	    Set<Term> terms = languageTerms.get(languageId);
	    if (Objects.nonNull(terms)) {
		for (Term term : terms) {
		    if (builder.containsDescription(requiredDescriptions, term.getDescriptions())) {
			return true;
		    }
		}
	    }
	}

	return false;
    }

    private void notifyCancelFinished(String threadName) {
	removeFromRequestedSet(threadName);
    }

    private void removeFromRequestedSet(String threadName) {
	getRequestedThreadsToStop().remove(threadName);
    }

    private static class NotifyItemProcessingFinishedEntryProcessor
	    extends AbstractEntryProcessor<String, ExportAdapter> {

	private static final long serialVersionUID = 1983106914365802988L;

	@Override
	public Object process(Entry<String, ExportAdapter> entry) {
	    ExportAdapter exportAdapter = entry.getValue();
	    exportAdapter.notifyItemProcessingFinished();
	    entry.setValue(exportAdapter);
	    return null;
	}
    }

}
