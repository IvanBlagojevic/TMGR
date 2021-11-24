package org.gs4tr.termmanager.model;

public enum ImportTypeEnum {
    TBX("tbx"), XLS("xls");

    public static ImportTypeEnum getImportType(String importFileExtension) {
	return isXlsOrXlsx(importFileExtension.toLowerCase()) ? ImportTypeEnum.XLS : ImportTypeEnum.TBX;
    }

    private static boolean isXlsOrXlsx(String extensionLowerCase) {
	String xlsFileExtension = ImportFileExtension.XLS.getText();
	String xlsxFileExtension = ImportFileExtension.XLSX.getText();
	return extensionLowerCase.equals(xlsFileExtension) || extensionLowerCase.equals(xlsxFileExtension);
    }

    private final String _text;

    private ImportTypeEnum(String text) {
	_text = text;
    }

    public String getText() {
	return _text;
    }
}
