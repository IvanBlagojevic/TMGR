package org.gs4tr.termmanager.service.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ExportFormatEnum {

    CSVEXPORT("CSV", CsvExportDocument.class.getName(), Boolean.FALSE),

    CSVEXPORT_MS("CSV MSOffice", CsvExportDocument.class.getName(), Boolean.FALSE),

    CSVSYNC("CSV", CsvSyncDocument.class.getName(), Boolean.TRUE),

    JSON("JSON", JSONExportDocument.class.getName(), Boolean.TRUE),

    JSON_V2("JSON_V2", JSONv2ExportDocument.class.getName(), Boolean.TRUE),

    TAB("TAB delimited", TabExportDocument.class.getName(), Boolean.FALSE),

    TAB_MS("TAB delimited MSOffice", TabExportDocument.class.getName(), Boolean.FALSE),

    TABSYNC("TAB delimited", TabSyncExportDocument.class.getName(), Boolean.TRUE),

    TBX("TBX", TbxExportDocument.class.getName(), Boolean.FALSE),

    XLS("XLS", XlsExportDocument.class.getName(), Boolean.FALSE),

    XLSX("XLSX", XlsExportDocument.class.getName(), Boolean.FALSE);

    private static Map<ExportFormatEnum, String> _formatMap = new HashMap<ExportFormatEnum, String>();

    private static Map<ExportFormatEnum, Boolean> _multilingualMap = new HashMap<ExportFormatEnum, Boolean>();

    static {
	_multilingualMap.put(TBX, Boolean.TRUE);
	_multilingualMap.put(CSVEXPORT, Boolean.FALSE);
	_multilingualMap.put(CSVEXPORT_MS, Boolean.FALSE);
	_multilingualMap.put(TAB, Boolean.FALSE);
	_multilingualMap.put(TAB_MS, Boolean.FALSE);
	_multilingualMap.put(TABSYNC, Boolean.FALSE);
	_multilingualMap.put(XLS, Boolean.TRUE);
	_multilingualMap.put(XLSX, Boolean.TRUE);

	_formatMap.put(TBX, "tbx");
	_formatMap.put(CSVEXPORT, "csv");
	_formatMap.put(CSVEXPORT_MS, "csv");
	_formatMap.put(TAB, "txt");
	_formatMap.put(TAB_MS, "txt");
	_formatMap.put(XLS, "xls");
	_formatMap.put(XLSX, "xlsx");
    };

    public static List<ExportFormatEnum> getExportFormats() {
	ExportFormatEnum[] allFormats = values();
	List<ExportFormatEnum> finalList = new ArrayList<ExportFormatEnum>();
	for (ExportFormatEnum exportFormatEnum : allFormats) {
	    if (!exportFormatEnum.getHidden()) {
		finalList.add(exportFormatEnum);
	    }
	}
	return finalList;
    }

    private String _exportName;

    private Boolean _hidden;

    private String _instanceName;

    private ExportFormatEnum(String exportName, String instanceName, Boolean hidden) {
	setExportName(exportName);
	setInstanceName(instanceName);
	setHidden(hidden);
    }

    public String getExportFormat() {
	return this.name().toLowerCase();
    }

    public String getExportName() {
	return _exportName;
    }

    public String getFileFormat() {
	return _formatMap.get(this);
    }

    public Boolean getHidden() {
	return _hidden;
    }

    public String getInstanceName() {
	return _instanceName;
    }

    public Boolean isMultilingual() {
	return _multilingualMap.get(this);
    }

    private void setExportName(String exportName) {
	_exportName = exportName;
    }

    private void setHidden(Boolean hidden) {
	_hidden = hidden;
    }

    private void setInstanceName(String instanceName) {
	_instanceName = instanceName;
    }
}
