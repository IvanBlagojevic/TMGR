package org.gs4tr.termmanager.service.file.analysis;

import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisReport;
import org.gs4tr.termmanager.service.file.analysis.request.Context;

/**
 * Chain of post processors that post process <code>FileAnalysisReport</code>
 * using the provided <code>Context</code>.
 * <p>
 * Concrete <code>PostProcessorChain</code> implementation will also decide
 * themselves who will be processing the <code>FileAnalysisReport</code> and
 * whether the <code>FileAnalysisReport</code> is required to be sent to the
 * next object in the chain or not.
 * </p>
 * 
 * since 5.0
 */
interface PostProcessorChain {

    void postProcess(FileAnalysisReport target, Context context);
}
