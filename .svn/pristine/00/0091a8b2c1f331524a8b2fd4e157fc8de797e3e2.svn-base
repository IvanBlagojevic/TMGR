package org.gs4tr.termmanager.service.file.manager;

import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.gs4tr.termmanager.model.StringConstants;

/**
 * <p>
 * Utility class used to create unique file names from original and vice versa.
 * </p>
 * 
 * @Immutable
 * @since 5.0
 */
public final class ImportFileNameMaker {

    private static final String FILENAME_SEPARATOR = StringConstants.RS;

    public static String makeOriginalFrom(String uniqueFileName) {
	Validate.isTrue(uniqueFileName.contains(FILENAME_SEPARATOR),
		String.format(Messages.getString("FileNameMaker.0"), FILENAME_SEPARATOR)); //$NON-NLS-1$
	return uniqueFileName.substring(uniqueFileName.indexOf(FILENAME_SEPARATOR) + 1);
    }

    public static String makeUniqueFrom(String originalFileName) {
	Validate.notEmpty(originalFileName, Messages.getString("FileNameMaker.1")); //$NON-NLS-1$
	StringBuilder uniqueFileName = new StringBuilder(UUID.randomUUID().toString());
	uniqueFileName.append(FILENAME_SEPARATOR);
	uniqueFileName.append(originalFileName);
	return uniqueFileName.toString();
    }

    /**
     * Suppresses default constructor, ensuring non-instantiability.
     */
    private ImportFileNameMaker() {
	throw new AssertionError(String.format(Messages.getString("ImportFileNameMaker.0"), getClass())); //$NON-NLS-1$
    }
}
