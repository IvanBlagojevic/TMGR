package org.gs4tr.termmanager.service.utils;

import org.gs4tr.foundation.modules.entities.model.RepositoryPath;

public class PathHelper {

    public static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

    public static RepositoryPath createRepositoryPath(String markerId) {
	StringBuilder builder = new StringBuilder();

	builder.append(markerId);
	builder.append(PATH_SEPARATOR);
	RepositoryPath path = new RepositoryPath(builder.toString());

	return path;
    }

}