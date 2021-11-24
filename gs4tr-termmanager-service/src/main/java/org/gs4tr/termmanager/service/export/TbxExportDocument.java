package org.gs4tr.termmanager.service.export;

import static org.gs4tr.termmanager.service.utils.TermEntryUtils.ID;
import static org.gs4tr.termmanager.service.utils.TermEntryUtils.STATUS;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation3.tbx.TbxAttribute;
import org.gs4tr.foundation3.tbx.TbxConstants;
import org.gs4tr.foundation3.tbx.TbxTag;
import org.gs4tr.foundation3.tbx.constraint.ConstraintEnum;
import org.gs4tr.foundation3.tbx.constraint.ConstraintFactory;
import org.gs4tr.foundation3.tbx.constraint.ConstraintLevelEnum;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.service.utils.ServiceUtils;

public class TbxExportDocument extends ExportDocumentFactory {

    private static String END_TAG = new StringBuilder().append("\n</body>\n").append("</text>\n").append("</martif>")
	    .toString();

    private static String START_TAG = new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
	    .append("<martif type=\"TBX\" xml:lang=\"en-US\">\n").append("<martifHeader>\n").append("<fileDesc>\n")
	    .append("<sourceDesc>\n").append("<p>Exported from Term Manager</p>\n").append("</sourceDesc>\n")
	    .append("</fileDesc>\n").append("<encodingDesc>\n").append("<p type=\"XCSURI\">null</p>\n")
	    .append("</encodingDesc>\n").append("</martifHeader>\n").append("<text>\n").append("<body>").toString();

    private ConstraintFactory _constraintFactory;

    private static Namespace _ns_xml = Namespace.get(TbxConstants.XML_PREFIX, TbxConstants.XML_URI);

    private static QName _qnameLanguage = new QName(TbxAttribute.LANGUAGE.attribute(), _ns_xml);

    @Override
    public void close() throws IOException {
	getOutputStream().write(END_TAG.getBytes(StandardCharsets.UTF_8));
	getOutputStream().flush();
	getOutputStream().close();
    }

    public ConstraintFactory getConstraintFactory() {
	return _constraintFactory;
    }

    @Override
    public ConstraintFactory open(OutputStream outputStream, TermEntrySearchRequest searchRequest, String xcsFileName)
	    throws IOException {
	if (xcsFileName != null) {
	    setConstraintFactory(ConstraintFactory.getInstance(ConstraintEnum.XCS));

	}
	setSearchRequest(searchRequest);
	setOutputStream(outputStream);
	getOutputStream().write(String.format(START_TAG, xcsFileName).getBytes(StandardCharsets.UTF_8));
	setForbiddenClause(Boolean.FALSE);

	return getConstraintFactory();
    }

    public void setConstraintFactory(ConstraintFactory constraintFactory) {
	_constraintFactory = constraintFactory;
    }

    private void addDescriptionConstraint(Description termDescription) {
	ConstraintFactory constraintFactory = getConstraintFactory();
	if (constraintFactory != null) {
	    constraintFactory.addDescriptionConstraint(termDescription.getType(), ConstraintLevelEnum.TERM,
		    termDescription.getValue());
	}
    }

    private void addDescriptionInternal(Element tig, Description termDescription) {
	// add description constraint to XCS
	String descType = termDescription.getType();
	String descValue = termDescription.getValue();
	if (StringUtils.isBlank(descType) || StringUtils.isBlank(descValue)) {
	    return;
	}
	addDescriptionConstraint(termDescription);

	Element descriptionElement = tig.addElement(TbxTag.DESCRIPTION.tag());
	descriptionElement.addAttribute(TbxAttribute.TYPE.attribute(), descType);
	descriptionElement.addText(descValue);
    }

    private void addLanguageConstraint(String languageId) {
	ConstraintFactory constraintFactory = getConstraintFactory();
	if (constraintFactory != null) {
	    constraintFactory.addLanguageConstraint(Locale.makeLocale(languageId));
	}
    }

    private void addNoteConstraint(Description termNote) {
	ConstraintFactory constraintFactory = getConstraintFactory();
	if (constraintFactory != null) {
	    constraintFactory.addTermNoteConstraint(termNote.getType(), termNote.getValue());
	}
    }

    private void addNoteInternal(Element tig, Description termDescription) {
	// add note constraint to XCS
	String descType = termDescription.getType();
	String descValue = termDescription.getValue();
	if (StringUtils.isBlank(descType) || StringUtils.isBlank(descValue)) {
	    return;
	}
	addNoteConstraint(termDescription);

	Element descriptionElement = tig.addElement(TbxTag.TERM_NOTE.tag());
	descriptionElement.addAttribute(TbxAttribute.TYPE.attribute(), descType);
	descriptionElement.addText(descValue);
    }

    private void addTermEntryDescriptionConstraint(Description desc) {
	ConstraintFactory constraintFactory = getConstraintFactory();
	if (constraintFactory != null) {
	    constraintFactory.addDescriptionConstraint(desc.getType(), ConstraintLevelEnum.TERMENTRY, desc.getValue());
	}
    }

    private String asXmlString(Element element) {
	try {
	    OutputFormat format = OutputFormat.createPrettyPrint();
	    StringWriter out = new StringWriter();
	    XMLWriter writer = new XMLWriter(out, format);
	    writer.write(element);
	    writer.flush();
	    return out.toString();
	} catch (IOException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    @Override
    protected String buildTermEntryXml(TermEntry termEntry, ExportInfo exportInfo, boolean isWS,
	    ExportTermsCallback exportTermsCallback) {

	String termEntryElementString = null;

	Set<Description> termEntryDescriptions = termEntry.getDescriptions();

	List<Term> terms = termEntry.ggetTerms();

	filterTermsByLanguage(terms, getSearchRequest().getLanguagesToExport());
	filterTermsByStatus(terms);

	boolean exportedTermEntry = false;
	int totalTermEntriesExported = exportInfo.getTotalTermEntriesExported();
	Boolean addedTermEntryDescriptions = Boolean.FALSE;

	if (CollectionUtils.isNotEmpty(terms)) {
	    Element termEntryElement = new DefaultElement(TbxTag.TERM_ENTRY.tag());

	    Element termEntryId = termEntryElement.addElement(TbxTag.DESCRIPTION.tag());
	    termEntryId.addAttribute(TbxAttribute.TYPE.attribute(), ID);
	    termEntryId.addText(termEntry.getUuId());

	    for (Term term : terms) {
		String languageId = term.getLanguageId();

		if (isTermExportable(term)) {
		    if (!addedTermEntryDescriptions && CollectionUtils.isNotEmpty(termEntryDescriptions)) {

			for (Description desc : termEntryDescriptions) {
			    // add termentryDescription to xcs constraint
			    String descType = desc.getType();
			    String descValue = desc.getValue();
			    if (StringUtils.isBlank(descType) || StringUtils.isBlank(descValue)) {
				continue;
			    }
			    addTermEntryDescriptionConstraint(desc);

			    Element descriptionElement = termEntryElement.addElement(TbxTag.DESCRIPTION.tag());
			    descriptionElement.addAttribute(TbxAttribute.TYPE.attribute(), descType);
			    descriptionElement.addText(descValue);

			}
			addedTermEntryDescriptions = Boolean.TRUE;
		    }

		    addLanguageConstraint(languageId);
		    Element languageElement = termEntryElement.addElement(TbxTag.LANGUAGE_SET.tag());

		    languageElement.addAttribute(_qnameLanguage, languageId);
		    Element tig = languageElement.addElement(TbxTag.TIG.tag());
		    Element termElement = tig.addElement(TbxTag.TERM.tag());

		    termElement.setText(term.getName());

		    Set<Description> notes = ServiceUtils.getDescriptionsByType(term, Description.NOTE);
		    notes.add(new Description(Description.NOTE, STATUS,
			    ItemStatusTypeHolder.getStatusDisplayName(term.getStatus())));

		    for (Description termDescription : notes) {
			addNoteInternal(tig, termDescription);
		    }

		    Set<Description> descriptions = ServiceUtils.getDescriptionsByType(term, Description.ATTRIBUTE);
		    descriptions.add(new Description(ID, term.getUuId()));

		    for (Description termDescription : descriptions) {
			addDescriptionInternal(tig, termDescription);
		    }

		    exportInfo.incrementTotalTermsExported();
		}

		exportedTermEntry = true;
	    }

	    termEntryElementString = asXmlString(termEntryElement);
	}

	if (exportedTermEntry) {
	    exportInfo.setTotalTermEntriesExported(totalTermEntriesExported + 1);
	}

	return termEntryElementString;
    }

    protected void filterTermsByLanguage(List<Term> terms, final List<String> languages) {
	CollectionUtils.filter(terms, new Predicate() {
	    @Override
	    public boolean evaluate(Object object) {
		Term term = (Term) object;
		return languages.contains(term.getLanguageId());
	    }
	});
    }

    @Override
    protected void filterTermsByStatus(List<Term> terms) {
	CollectionUtils.filter(terms, new Predicate() {
	    @Override
	    public boolean evaluate(Object object) {
		Term term = (Term) object;
		boolean isBlacklisted = term.getStatus().equals(ItemStatusTypeHolder.BLACKLISTED.getName());
		boolean isProcessed = term.getStatus().equals(ItemStatusTypeHolder.PROCESSED.getName());
		boolean isOnHold = term.getStatus().equals(ItemStatusTypeHolder.ON_HOLD.getName());
		boolean isWaiting = term.getStatus().equals(ItemStatusTypeHolder.WAITING.getName());
		return isBlacklisted || isProcessed || isOnHold || isWaiting;
	    }
	});
    }

    @Override
    protected boolean isExportableByForbidden(Term term, Boolean exportForbidden) {
	return !term.isDisabled();
    }

    @Override
    protected Boolean isTermExportable(Term term) {
	TermEntrySearchRequest searchRequest = getSearchRequest();

	Date dateModifiedFrom = searchRequest.getDateModifiedFrom();

	List<String> languagesToExport = searchRequest.getLanguagesToExport();

	String termLocale = term.getLanguageId();
	String sourceLocale = searchRequest.getSourceLocale();
	Boolean exportableByLanguage = (languagesToExport != null && languagesToExport.contains(termLocale))
		|| ((sourceLocale != null && termLocale.equals(sourceLocale)));

	Boolean exportableByDate = dateModifiedFrom == null
		|| term.getDateModified().compareTo(dateModifiedFrom.getTime()) > 0;

	boolean exportableByName = StringUtils.isNotEmpty(term.getName());

	Boolean exportableByEnabled = !term.isDisabled();

	return exportableByName && exportableByLanguage && exportableByDate && exportableByEnabled;

    }
}
