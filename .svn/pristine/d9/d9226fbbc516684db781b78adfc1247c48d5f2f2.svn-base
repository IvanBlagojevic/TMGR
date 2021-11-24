package org.gs4tr.termmanager.service.file.analysis.request;

import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang.Validate;

/**
 * A request for the <code>FilesAnalysisRequestHandler</code>.
 *
 * @since 5.0
 */
public final class FilesAnalysisRequest {

    private final Context _context;

    /**
     * Indicates directory name on file system where files for analysis are
     * stored.
     */
    private final String _directory;
    private final String _processingId;

    public FilesAnalysisRequest(String directory, Context context) {
	Validate.notEmpty(directory, "The directory name must not be empty");
	_directory = directory;
	_context = Objects.requireNonNull(context, "The context must not be null");
	_processingId = UUID.randomUUID().toString();
    }

    public Context getContext() {
	return _context;
    }

    public String getDirectory() {
	return _directory;
    }

    public String getProcessingId() {
	return _processingId;
    }
}
