package org.gs4tr.termmanager.service.export;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation3.io.model.BOM;
import org.gs4tr.foundation3.tbx.constraint.ConstraintFactory;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.LastOperationEnum;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.dto.Description;
import org.gs4tr.termmanager.model.dto.ExportTermModel;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.service.utils.JsonUtils;

public abstract class ExportDocumentFactory {

    public static ExportDocumentFactory getInstance(ExportFormatEnum exportFormat) throws Exception {
	setFileFormat(exportFormat.getFileFormat());
	return (ExportDocumentFactory) Class.forName(exportFormat.getInstanceName()).newInstance();
    }

    private Set<String> _descriptionTypes;

    private static String _fileFormat;

    private Boolean _forbiddenClause;

    private OutputStream _outputStream;

    private TermEntrySearchRequest _searchRequest;

    public abstract void close() throws Exception;

    public void flush() throws IOException {
	getOutputStream().flush();
    }

    public Set<String> getDescriptionTypes() {
	return _descriptionTypes;
    }

    public Boolean getForbiddenClause() {
	return _forbiddenClause;
    }

    public abstract ConstraintFactory open(OutputStream outputStream, TermEntrySearchRequest searchRequest,
	    String xcsFileName) throws IOException;

    public void setDescriptionTypes(Set<String> descriptionTypes) {
	_descriptionTypes = descriptionTypes;
    }

    public void setForbiddenClause(Boolean forbiddenClause) {
	_forbiddenClause = forbiddenClause;
    }

    public void setOutputStream(OutputStream outputStream) {
	_outputStream = outputStream;
    }

    public void write(TermEntry termEntry, ExportInfo exportInfo, boolean isWS, ExportTermsCallback exportTermsCallback)
	    throws IOException {

	String termEntryXml = buildTermEntryXml(termEntry, exportInfo, isWS, exportTermsCallback);

	if (StringUtils.isNotEmpty(termEntryXml)) {
	    getOutputStream().write(termEntryXml.getBytes(StandardCharsets.UTF_8));
	}
    }

    public void writeBOM() throws IOException {
	getOutputStream().write(BOM.UTF_8.getBytes());
    }

    public void writeForbiddenTerms(Term term) throws IOException {
	getOutputStream().write(buildForbiddenTermJson(term).getBytes(StandardCharsets.UTF_8));
    }

    private String buildForbiddenTermJson(Term term) {
	ExportTermModel model = new ExportTermModel();
	model.setSource(term.getName());
	model.setSuggestions(ArrayUtils.EMPTY_STRING_ARRAY);
	model.setModificationDate(term.getDateModified());
	model.setOperation(LastOperationEnum.CREATED.getShortLabel());
	model.setCreationDate(term.getDateCreated());
	model.setCreationUser(term.getUserCreated());
	model.setModificationUser(term.getUserModified());
	model.setForbidden(Boolean.TRUE);
	model.setTicket(TicketConverter.fromInternalToDto(term.getUuId()));
	model.setSourceAttributes(new Description[0]);
	model.setTargetAttributes(new Description[0]);

	String jsonString = JsonUtils.writeValueAsString(model);

	return jsonString.concat(StringConstants.NEW_LINE);
    }

    protected abstract String buildTermEntryXml(TermEntry termEntry, ExportInfo exportInfo, boolean isWS,
	    ExportTermsCallback exportTermsCallback) throws IOException;

    protected void filterTermsByStatus(List<Term> terms) {
	CollectionUtils.filter(terms, new Predicate() {
	    @Override
	    public boolean evaluate(Object item) {
		Term term = (Term) item;
		List<String> allowedStatuses = getSearchRequest().getStatuses();
		String status = term.getStatus();
		return allowedStatuses.contains(status);
	    }
	});
    }

    protected static String getFileFormat() {
	return _fileFormat;
    }

    protected OutputStream getOutputStream() {
	return _outputStream;
    }

    protected TermEntrySearchRequest getSearchRequest() {
	return _searchRequest;
    }

    protected abstract boolean isExportableByForbidden(Term term, Boolean exportForbidden);

    protected Boolean isTermExportable(Term term) {
	TermEntrySearchRequest searchRequest = getSearchRequest();

	Date dateModifiedFrom = searchRequest.getDateModifiedFrom();

	List<String> languagesToExport = searchRequest.getLanguagesToExport();

	Boolean exportForbidden = null;

	if (searchRequest.getStatuses().contains(ItemStatusTypeHolder.BLACKLISTED.getName())) {
	    exportForbidden = Boolean.TRUE;
	}

	String termLocale = term.getLanguageId();

	boolean exportableByLanguage = languagesToExport.contains(termLocale);
	boolean exportableByForbidden = isExportableByForbidden(term, exportForbidden);
	boolean exportableByDate = dateModifiedFrom == null
		|| term.getDateModified().compareTo(dateModifiedFrom.getTime()) > 0;
	boolean exportableByName = StringUtils.isNotEmpty(term.getName());
	boolean exportableByEnabled = !term.isDisabled();

	if (exportableByName && exportableByForbidden && exportableByLanguage && exportableByDate
		&& exportableByEnabled) {
	    return Boolean.TRUE;
	} else {
	    return Boolean.FALSE;
	}
    }

    protected static void setFileFormat(String fileFormat) {
	_fileFormat = fileFormat;
    }

    protected void setSearchRequest(TermEntrySearchRequest searchRequest) {
	_searchRequest = searchRequest;
    }
}
