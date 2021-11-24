package org.gs4tr.termmanager.model;

import static java.util.function.Predicate.isEqual;
import java.util.stream.Stream;

/**
 * <p>
 * A set of values that represent the supported file extensions on import.
 * </p>
 * 
 * @since 5.0
 */
public enum ImportFileExtension {
    /**
     * The <b>TermBase eXchange</b> format (<b>TBX</b> format) is an exchange
     * format that standardizes the presentation of terminology databases and
     * renders it exchangeable between different tools or translators with a
     * minimum of information loss.
     */
    TBX("tbx"),

    /**
     * <b>XLS</b> is a file extension for a spreadsheet file format created by
     * Microsoft for use with <b>Microsoft Excel</b>. XLS stands for eXceL
     * Spreadsheet.
     */
    XLS("xls"),

    /**
     * <b>XLSX</b> is a file extension for an open <b>XML</b> spreadsheet file
     * format used by Microsoft Excel.
     */
    XLSX("xlsx"),

    /**
     * <b>ZIP</b> is an archive file format that supports lossless data
     * compression. A <b>.zip</b> file may contain one or more files that may
     * have been compressed.
     */
    ZIP("zip");

    private static final ImportFileExtension[] EXTENSIONS = ImportFileExtension.values();

    public static boolean supported(String extension) {
	String extensionLowerCase = extension.toLowerCase();
	return Stream.of(EXTENSIONS).map(ImportFileExtension::getText).anyMatch(isEqual(extensionLowerCase));
    }

    private String _text;

    private ImportFileExtension(String text) {
	_text = text;
    }

    public String getText() {
	return _text;
    }
}
