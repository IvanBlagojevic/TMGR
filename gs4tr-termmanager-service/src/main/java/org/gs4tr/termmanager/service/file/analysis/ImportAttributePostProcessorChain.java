package org.gs4tr.termmanager.service.file.analysis;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute;
import org.gs4tr.termmanager.service.file.analysis.model.ProjectAttribute;
import org.gs4tr.termmanager.service.file.analysis.model.Status;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;
import org.gs4tr.termmanager.service.file.analysis.request.Context;
import org.springframework.stereotype.Component;

@Component("importAttributePostProcessorChain")
class ImportAttributePostProcessorChain implements PostProcessorChain {

    private static final Log LOG = LogFactory.getLog(ImportAttributePostProcessorChain.class);

    /**
     * <code>_nextChain</code> is set to <code>null</code> because
     * <code>_nextChain</code> does not exist (i.e currently,
     * <code>ImportAttributePostProcessorChain</code> is the last one in the
     * chain)
     */
    private final PostProcessorChain _nextChain = null;

    @Override
    public void postProcess(FileAnalysisReport target, Context context) {
	List<ImportAttribute> attributes = target.getImportAttributeReport().getImportAttributes();
	Map<Level, List<ImportAttribute>> grouped = attributes.stream().collect(groupingBy(ImportAttribute::getLevel));
	for (final Entry<Level, List<ImportAttribute>> entry : grouped.entrySet()) {
	    postProcessImportAttributes(entry.getValue(), context);
	}
    }

    private Map<Level, Set<String>> getAttributesByLevelOrEmpty(Context context) {
	Map<Level, Set<String>> attributesByLevel = context.getAttributesByLevel();
	return MapUtils.isNotEmpty(attributesByLevel) ? attributesByLevel : Collections.emptyMap();
    }

    private void postProcessImportAttributes(List<ImportAttribute> attributes, Context context) {
	Set<String> importAttributeNames = attributes.stream().map(ImportAttribute::getName).collect(toSet());

	Map<Level, Set<String>> attributesByLevel = getAttributesByLevelOrEmpty(context);
	for (final ImportAttribute importAttribute : attributes) {
	    final Level level = importAttribute.getLevel();
	    Set<String> projectAttributeNames = attributesByLevel.getOrDefault(level, emptySet());
	    String importAttributeName = importAttribute.getName();
	    if (projectAttributeNames.contains(importAttributeName)) {
		importAttribute.setStatus(Status.EXISTING);
		String format = Messages.getString("ImportAttributePostProcessor.0"); //$NON-NLS-1$
		LogHelper.debug(LOG, String.format(format, importAttributeName));
		continue;
	    }

	    if (!importAttributeNames.containsAll(projectAttributeNames)) {
		for (String projectAttributeName : projectAttributeNames) {
		    List<ProjectAttribute> projectAttributes = importAttribute.getProjectAttributes();
		    projectAttributes.add(new ProjectAttribute(projectAttributeName, level));
		    String format = Messages.getString("ImportAttributePostProcessor.1"); // $NON-NLS-1$
		    LogHelper.warn(LOG, String.format(format, importAttributeName, projectAttributeName));
		}
	    }
	    importAttribute.setStatus(Status.NEW);
	}
    }
    
    public PostProcessorChain getNextChain() {
	return _nextChain;
    }
}
