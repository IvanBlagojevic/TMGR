package org.gs4tr.termmanager.solr.plugin;

import java.io.IOException;
import java.util.UUID;

import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.update.AddUpdateCommand;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TmgrAdditionalFieldsUpdateProcessorTest {

    private TmgrAdditionalFieldsUpdateProcessor _additionalProcessor;
    private AddUpdateCommand _command;
    private TmgrFieldsCorrectorProcessor _correctorProcessor;
    private TmgrPreUpdateProcessor _preUpdateProcessor;

    public TmgrAdditionalFieldsUpdateProcessor getAdditionalProcessor() {
	return _additionalProcessor;
    }

    public AddUpdateCommand getCommand() {
	return _command;
    }

    public TmgrFieldsCorrectorProcessor getCorrectorProcessor() {
	return _correctorProcessor;
    }

    public TmgrPreUpdateProcessor getPreUpdateProcessor() {
	return _preUpdateProcessor;
    }

    @Before
    public void init() {
	_additionalProcessor = (TmgrAdditionalFieldsUpdateProcessor) new TmgrAdditionalFieldsUpdateProcessorFactory()
		.getInstance(null, null, null);
	_correctorProcessor = (TmgrFieldsCorrectorProcessor) new TmgrFieldsCorrectorProcessorFactory().getInstance(null,
		null, null);
	_preUpdateProcessor = (TmgrPreUpdateProcessor) new TmgrPreUpdateProcessorFactory().getInstance(null, null,
		null);
	_command = new AddUpdateCommand(null);

    }

    @Test
    public void test01() throws IOException {

	SolrInputDocument document = createDocument();

	AddUpdateCommand command = getCommand();
	command.setIndexedId(new BytesRef());
	command.solrDoc = document;
	getPreUpdateProcessor().processAdd(command);
	assertAfterPreUpdateProcessor(command.solrDoc);
	getCorrectorProcessor().processAdd(command);
	assertAfterCorrectorProcessor(command.solrDoc);
	getAdditionalProcessor().processAdd(command);
	Assert.assertNotNull(command);
	assertAfterAdditionalFieldsProcessor(command.solrDoc);

    }

    // Input document en, en2, en3 -> Output document en, en1, en2
    @Test
    public void testEnglishWithoutFirstSynonym() throws IOException {
	SolrInputDocument document = createDocumentWithoutFirstSynonym();

	AddUpdateCommand command = getCommand();
	command.setIndexedId(new BytesRef());
	command.solrDoc = document;
	getPreUpdateProcessor().processAdd(command);
	assertAfterPreUpdateProcessor(command.solrDoc);
	getCorrectorProcessor().processAdd(command);
	getAdditionalProcessor().processAdd(command);

	/*
	 * ==========================================================================
	 * Note: We have main term for the language, but the first synonym does not
	 * exists. Second synonym should become first.
	 * ==========================================================================
	 */
	assertFieldsProcessorEngNoFirstSynonym(command.solrDoc);
    }

    @Test
    public void testFrenchLanguageWithoutMainTerm() throws IOException {

	// French language don't have main term (only one synonym).
	SolrInputDocument document = createDocumentWithoutFrenchMainTerm();

	AddUpdateCommand command = getCommand();
	command.setIndexedId(new BytesRef());
	command.solrDoc = document;
	getPreUpdateProcessor().processAdd(command);
	assertAfterPreUpdateProcessor(command.solrDoc);
	getCorrectorProcessor().processAdd(command);
	getAdditionalProcessor().processAdd(command);
	Assert.assertNotNull(command);

	/*
	 * =============================================================================
	 * Note: There are 3 languages. en and de languages have two and fr have only
	 * one term(only synonym). This can happen when language don't have first term.
	 * =============================================================================
	 */
	assertAfterAdditionalFieldsProcessorNoFirstTerm(command.solrDoc);
    }

    /*
     * TERII-5838 Floating Multi-editor | Bad synonym status after deleting one of
     * the synonyms
     */
    @Test
    public void testReadAllDocumentFieldsAfterSynonymDelete() throws IOException {
	SolrInputDocument document = createDocumentWithoutFirstSynonym();

	AddUpdateCommand command = getCommand();
	command.setIndexedId(new BytesRef());
	command.solrDoc = document;
	getPreUpdateProcessor().processAdd(command);
	assertAfterPreUpdateProcessor(command.solrDoc);
	getCorrectorProcessor().processAdd(command);
	getAdditionalProcessor().processAdd(command);

	assertSynonymFieldNames(command.solrDoc);
    }

    @Test
    public void testThreeSynonymsNoMainTerm() throws IOException {
	SolrInputDocument document = createDocumentThreeSynonymsNoMainTerm();

	AddUpdateCommand command = getCommand();
	command.setIndexedId(new BytesRef());
	command.solrDoc = document;
	getPreUpdateProcessor().processAdd(command);
	assertAfterPreUpdateProcessor(command.solrDoc);
	getCorrectorProcessor().processAdd(command);
	getAdditionalProcessor().processAdd(command);
	Assert.assertNotNull(command);
	assertAfterAdditionalFieldsProcessorNoMainTerm(command.solrDoc);
    }

    private void assertAfterAdditionalFieldsProcessor(SolrInputDocument document) {
	Assert.assertNotNull(document);
	Assert.assertNotNull(document.getChildDocuments());
	Assert.assertEquals("PARENT", document.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertNotNull(document.getFieldValue(SolrParentDocFields.PARENT_ID_STORE));
	Assert.assertEquals(6, document.getChildDocuments().size());
	SolrInputDocument child = document.getChildDocuments().get(0);
	Assert.assertEquals("de", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("term on german", child.getFieldValue("termName_de_NGRAM_INDEX"));
	Assert.assertEquals("term on german", child.getFieldValue("termName_de_SUB_STEMMED_INDEX"));
	Assert.assertEquals("term on german", child.getFieldValue("termName_de_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	child = document.getChildDocuments().get(1);
	Assert.assertEquals("de", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("synonym on german", child.getFieldValue("termName_de_NGRAM_INDEX"));
	Assert.assertEquals("synonym on german", child.getFieldValue("termName_de_SUB_STEMMED_INDEX"));
	Assert.assertEquals("synonym on german", child.getFieldValue("termName_de_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	child = document.getChildDocuments().get(2);

	Assert.assertEquals("en", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("term on english", child.getFieldValue("termName_en_NGRAM_INDEX"));
	Assert.assertEquals("term on english", child.getFieldValue("termName_en_SUB_STEMMED_INDEX"));
	Assert.assertEquals("term on english", child.getFieldValue("termName_en_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	child = document.getChildDocuments().get(3);

	Assert.assertEquals("en", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("synonym on english", child.getFieldValue("termName_en_NGRAM_INDEX"));
	Assert.assertEquals("synonym on english", child.getFieldValue("termName_en_SUB_STEMMED_INDEX"));
	Assert.assertEquals("synonym on english", child.getFieldValue("termName_en_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	child = document.getChildDocuments().get(4);

	Assert.assertEquals("fr", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("term on french", child.getFieldValue("termName_fr_NGRAM_INDEX"));
	Assert.assertEquals("term on french", child.getFieldValue("termName_fr_SUB_STEMMED_INDEX"));
	Assert.assertEquals("term on french", child.getFieldValue("termName_fr_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	child = document.getChildDocuments().get(5);

	Assert.assertEquals("fr", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("synonym on french", child.getFieldValue("termName_fr_NGRAM_INDEX"));
	Assert.assertEquals("synonym on french", child.getFieldValue("termName_fr_SUB_STEMMED_INDEX"));
	Assert.assertEquals("synonym on french", child.getFieldValue("termName_fr_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

    }

    private void assertAfterAdditionalFieldsProcessorNoFirstTerm(SolrInputDocument document) {
	Assert.assertNotNull(document);
	Assert.assertNotNull(document.getChildDocuments());
	Assert.assertEquals("PARENT", document.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertNotNull(document.getFieldValue(SolrParentDocFields.PARENT_ID_STORE));

	// SOLR document should have only 5 child documents. French langu
	Assert.assertEquals(5, document.getChildDocuments().size());
	SolrInputDocument child = document.getChildDocuments().get(0);
	Assert.assertEquals("de", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("term on german", child.getFieldValue("termName_de_NGRAM_INDEX"));
	Assert.assertEquals("term on german", child.getFieldValue("termName_de_SUB_STEMMED_INDEX"));
	Assert.assertEquals("term on german", child.getFieldValue("termName_de_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	child = document.getChildDocuments().get(1);
	Assert.assertEquals("de", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("synonym on german", child.getFieldValue("termName_de_NGRAM_INDEX"));
	Assert.assertEquals("synonym on german", child.getFieldValue("termName_de_SUB_STEMMED_INDEX"));
	Assert.assertEquals("synonym on german", child.getFieldValue("termName_de_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	child = document.getChildDocuments().get(2);

	Assert.assertEquals("en", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("term on english", child.getFieldValue("termName_en_NGRAM_INDEX"));
	Assert.assertEquals("term on english", child.getFieldValue("termName_en_SUB_STEMMED_INDEX"));
	Assert.assertEquals("term on english", child.getFieldValue("termName_en_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	child = document.getChildDocuments().get(3);

	Assert.assertEquals("en", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("synonym on english", child.getFieldValue("termName_en_NGRAM_INDEX"));
	Assert.assertEquals("synonym on english", child.getFieldValue("termName_en_SUB_STEMMED_INDEX"));
	Assert.assertEquals("synonym on english", child.getFieldValue("termName_en_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	child = document.getChildDocuments().get(4);

	/*
	 * ============================================================================
	 * Note: Synonym will be used as main term because fr language don't have first
	 * term in DB. During rebuild index will be saved as main term for the fr lang.
	 * ============================================================================
	 */

	Assert.assertEquals("fr", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("synonym on french", child.getFieldValue("termName_fr_NGRAM_INDEX"));
	Assert.assertEquals("synonym on french", child.getFieldValue("termName_fr_SUB_STEMMED_INDEX"));
	Assert.assertEquals("synonym on french", child.getFieldValue("termName_fr_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

    }

    private void assertAfterAdditionalFieldsProcessorNoMainTerm(SolrInputDocument document) {
	Assert.assertNotNull(document);
	Assert.assertNotNull(document.getChildDocuments());
	Assert.assertEquals("PARENT", document.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertNotNull(document.getFieldValue(SolrParentDocFields.PARENT_ID_STORE));
	Assert.assertEquals(3, document.getChildDocuments().size());
	SolrInputDocument child = document.getChildDocuments().get(0);
	Assert.assertEquals("en", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("synonym on english 1", child.getFieldValue("termName_en_NGRAM_INDEX"));
	Assert.assertEquals("synonym on english 1", child.getFieldValue("termName_en_SUB_STEMMED_INDEX"));
	Assert.assertEquals("synonym on english 1", child.getFieldValue("termName_en_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	child = document.getChildDocuments().get(1);
	Assert.assertEquals("en", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("synonym on english 2", child.getFieldValue("termName_en_NGRAM_INDEX"));
	Assert.assertEquals("synonym on english 2", child.getFieldValue("termName_en_SUB_STEMMED_INDEX"));
	Assert.assertEquals("synonym on english 2", child.getFieldValue("termName_en_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	child = document.getChildDocuments().get(2);

	Assert.assertEquals("en", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("synonym on english 3", child.getFieldValue("termName_en_NGRAM_INDEX"));
	Assert.assertEquals("synonym on english 3", child.getFieldValue("termName_en_SUB_STEMMED_INDEX"));
	Assert.assertEquals("synonym on english 3", child.getFieldValue("termName_en_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));
    }

    private void assertAfterCorrectorProcessor(SolrInputDocument document) {
	Assert.assertNotNull(document);

	Assert.assertFalse(document.hasChildDocuments());
	Assert.assertEquals("idjurasevic", document.getFieldValue("fr_userCreated_STRING_BASIC_SORT"));
	Assert.assertEquals("PROCESSED", document.getFieldValue("en1_status_STRING_STORE"));
	Assert.assertEquals("3b214059-8b33-4f13-85fa-e2ec036ebda4", document.getFieldValue(SolrConstants.ID_FIELD));
	// document id for delete
	Assert.assertNull(document.getFieldValues("de_deleted_STRING_STORE_MULTI"));

	Assert.assertEquals(1479894716731L, document.getFieldValue("fr_dateCreated_LONG_SORT"));
	Assert.assertEquals("ADDED", document.getFieldValue(SolrParentDocFields.HISTORY_ACTION));

    }

    private void assertAfterPreUpdateProcessor(SolrInputDocument document) {
	Assert.assertNotNull(document);
	Assert.assertEquals("PARENT", document.get(SolrParentDocFields.TYPE_INDEX).getValue());
	Assert.assertNotNull(document.getFieldValue(SolrConstants.ID_FIELD));
    }

    private void assertFieldsProcessorEngNoFirstSynonym(SolrInputDocument document) {
	Assert.assertNotNull(document);
	Assert.assertNotNull(document.getChildDocuments());
	Assert.assertEquals("PARENT", document.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertNotNull(document.getFieldValue(SolrParentDocFields.PARENT_ID_STORE));
	Assert.assertEquals(3, document.getChildDocuments().size());

	SolrInputDocument child = document.getChildDocuments().get(0);
	Assert.assertEquals("en", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("term on english", child.getFieldValue("termName_en_NGRAM_INDEX"));
	Assert.assertEquals("term on english", child.getFieldValue("termName_en_SUB_STEMMED_INDEX"));
	Assert.assertEquals("term on english", child.getFieldValue("termName_en_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	// Second synonym is now saved as first
	child = document.getChildDocuments().get(1);
	Assert.assertEquals("en", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("synonym on english 2", child.getFieldValue("termName_en_NGRAM_INDEX"));
	Assert.assertEquals("synonym on english 2", child.getFieldValue("termName_en_SUB_STEMMED_INDEX"));
	Assert.assertEquals("synonym on english 2", child.getFieldValue("termName_en_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));

	child = document.getChildDocuments().get(2);

	// Third synonym is saved as second
	Assert.assertEquals("en", child.getFieldValue("languageId_STRING_INDEX"));
	Assert.assertEquals(false, child.getFieldValue("disabled_BOOL_INDEX"));
	Assert.assertEquals("PROCESSED", child.getFieldValue("status_STRING_INDEX"));
	Assert.assertNotNull(child.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertNull(child.getFieldValue(SolrParentDocFields.TYPE_INDEX));
	Assert.assertEquals("idjurasevic", child.getFieldValue("userModified_STRING_INDEX"));
	Assert.assertEquals("synonym on english 3", child.getFieldValue("termName_en_NGRAM_INDEX"));
	Assert.assertEquals("synonym on english 3", child.getFieldValue("termName_en_SUB_STEMMED_INDEX"));
	Assert.assertEquals("synonym on english 3", child.getFieldValue("termName_en_SUB_INDEX"));
	Assert.assertNotNull(child.getFieldValue("TERM_CHECKSUM_STRING_INDEX"));
    }

    private void assertSynonymFieldNames(SolrInputDocument document) {
	Assert.assertNotNull(document);

	/* Note: Main term must have sort fields (Only specific fields) */
	String fieldValue = (String) document.getFieldValue("en_ID_STRING_BASIC_SORT");
	Assert.assertNotNull(fieldValue);

	fieldValue = (String) document.getFieldValue("en_status_STRING_BASIC_SORT");
	Assert.assertEquals("PROCESSED", fieldValue);

	fieldValue = (String) document.getFieldValue("en_userCreated_STRING_BASIC_SORT");
	Assert.assertEquals("idjurasevic", fieldValue);

	fieldValue = (String) document.getFieldValue("en_userModified_STRING_BASIC_SORT");
	Assert.assertEquals("idjurasevic", fieldValue);

	fieldValue = (String) document.getFieldValue("en_dateCreated_LONG_SORT");
	Assert.assertNotNull(fieldValue);

	fieldValue = (String) document.getFieldValue("en_dateModified_LONG_SORT");
	Assert.assertNotNull(fieldValue);

	fieldValue = (String) document.getFieldValue("en_termName_STRING_STORE_SORT");
	Assert.assertEquals("term on english", fieldValue);

	/* Note: Synonym term must have store fields */
	fieldValue = (String) document.getFieldValue("en1_ID_STRING_STORE");
	Assert.assertNotNull(fieldValue);

	fieldValue = (String) document.getFieldValue("en1_status_STRING_STORE");
	Assert.assertEquals("PROCESSED", fieldValue);

	fieldValue = (String) document.getFieldValue("en1_userCreated_STRING_STORE");
	Assert.assertEquals("idjurasevic", fieldValue);

	fieldValue = (String) document.getFieldValue("en1_userModified_STRING_STORE");
	Assert.assertEquals("idjurasevic", fieldValue);

	fieldValue = (String) document.getFieldValue("en1_dateCreated_LONG_STORE");
	Assert.assertNotNull(fieldValue);

	fieldValue = (String) document.getFieldValue("en1_dateModified_LONG_STORE");
	Assert.assertNotNull(fieldValue);

	fieldValue = (String) document.getFieldValue("en1_termName_STRING_STORE");
	Assert.assertEquals("synonym on english 2", fieldValue);

    }

    private SolrInputDocument createDocument() {
	SolrInputDocument document = new SolrInputDocument();

	document.setField(SolrParentDocFields.HISTORY_ACTION, "ADDED");
	document.setField(SolrConstants.ID_FIELD, "3b214059-8b33-4f13-85fa-e2ec036ebda4");
	document.setField(SolrParentDocFields.PROJECT_ID_INDEX_STORE, "2");
	document.setField(SolrParentDocFields.PARENT_ID_STORE, UUID.randomUUID());
	document.setField(SolrParentDocFields.SHORTCODE_INDEX_STORE, "IVA0002");
	document.setField(SolrParentDocFields.PROJECT_NAME_INDEX_STORE, "ivan");
	document.setField("termEntryDateCreated_LONG", "1479894716726");
	document.setField(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, new String[] { "de", "en", "fr" });
	document.setField("de_ID_STRING_BASIC_SORT", "80464d26-6bb5-496f-a2ba-7a2b84c4729a");
	document.setField("de_status_STRING_BASIC_SORT", "PROCESSED");
	document.setField("de_userCreated_STRING_BASIC_SORT", "idjurasevic");
	document.setField("de_userModified_STRING_BASIC_SORT", "idjurasevic");
	document.setField("de_dateCreated_LONG_SORT", "1479894716731");
	document.setField("de_dateModified_LONG_SORT", "1479894716731");
	document.setField("de_termName_STRING_STORE_SORT", "term on german");
	document.setField("de_forbidden_BOOL_STORE", "false");
	document.setField("de_disabled_BOOL_STORE", false);
	document.setField("de_userLatestChange_LONG_STORE", 3);
	document.setField("de_inTranslationAsSource_BOOL_STORE", false);
	document.setField("de1_ID_STRING_STORE", "f997bf2b-3c0c-42fe-a02f-c1a969dedfc8");
	document.setField("de1_status_STRING_STORE", "PROCESSED");
	document.setField("de1_userCreated_STRING_STORE", "idjurasevic");
	document.setField("de1_userModified_STRING_STORE", "idjurasevic");
	document.setField("de1_dateCreated_LONG_STORE", "1479894716732");
	document.setField("de1_dateModified_LONG_STORE", "1479894716732");
	document.setField("de1_termName_STRING_STORE", "synonym on german");
	document.setField("de1_forbidden_BOOL_STORE", false);
	document.setField("de1_disabled_BOOL_STORE", false);
	document.setField("de1_userLatestChange_LONG_STORE", 3);
	document.setField("de1_inTranslationAsSource_BOOL_STORE", false);
	document.setField("en_ID_STRING_BASIC_SORT", "cabc772b-6a20-4a27-ac4c-8048adbf602e");
	document.setField("en_status_STRING_BASIC_SORT", "PROCESSED");
	document.setField("en_userCreated_STRING_BASIC_SORT", "idjurasevic");
	document.setField("en_userModified_STRING_BASIC_SORT", "idjurasevic");
	document.setField("en_dateCreated_LONG_SORT", "1479894716730");
	document.setField("en_dateModified_LONG_SORT", "1479894716730");
	document.setField("en_termName_STRING_STORE_SORT", "term on english");
	document.setField("en_forbidden_BOOL_STORE", false);
	document.setField("en_disabled_BOOL_STORE", false);
	document.setField("en_userLatestChange_LONG_STORE", 3);
	document.setField("en_inTranslationAsSource_BOOL_STORE", false);
	document.setField("en1_ID_STRING_STORE", "d605ccaf-3922-4488-a428-68bc0ab68857");
	document.setField("en1_status_STRING_STORE", "PROCESSED");
	document.setField("en1_userCreated_STRING_STORE", "idjurasevic");
	document.setField("en1_userModified_STRING_STORE", "idjurasevic");
	document.setField("en1_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en1_dateModified_LONG_STORE", "1479894716731");
	document.setField("en1_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en1_termName_STRING_STORE", "synonym on english");
	document.setField("en1_forbidden_BOOL_STORE", false);
	document.setField("en1_disabled_BOOL_STORE", false);
	document.setField("en1_userLatestChange_LONG_STORE", 3);
	document.setField("en1_inTranslationAsSource_BOOL_STORE", false);
	document.setField("fr1_ID_STRING_STORE", "aece7714-e946-43f4-a46c-e9f0d2b72525");
	document.setField("fr1_status_STRING_STORE", "PROCESSED");
	document.setField("fr1_userCreated_STRING_STORE", "idjurasevic");
	document.setField("fr1_userModified_STRING_STORE", "idjurasevic");
	document.setField("fr1_dateCreated_LONG_STORE", "1479894716731");
	document.setField("fr1_dateModified_LONG_STORE", "1479894716731");
	document.setField("fr1_termName_STRING_STORE", "synonym on french");
	document.setField("fr1_forbidden_BOOL_STORE", false);
	document.setField("fr1_disabled_BOOL_STORE", false);
	document.setField("fr1_userLatestChange_LONG_STORE", 3);
	document.setField("fr_ID_STRING_BASIC_SORT", "a1cdeaba-41de-4d4d-8c59-e7c746dcd633");
	document.setField("fr_status_STRING_BASIC_SORT", "PROCESSED");
	document.setField("fr_userCreated_STRING_BASIC_SORT", "idjurasevic");
	document.setField("fr_userModified_STRING_BASIC_SORT", "idjurasevic");
	document.setField("fr_dateCreated_LONG_SORT", 1479894716731L);
	document.setField("fr_dateModified_LONG_SORT", 1479894716731L);
	document.setField("fr_termName_STRING_STORE_SORT", "term on french");
	document.setField("fr_forbidden_BOOL_STORE", false);
	document.setField("fr_disabled_BOOL_STORE", false);
	document.setField("fr_userLatestChange_LONG_STORE", 3);
	document.setField("fr_inTranslationAsSource_BOOL_STORE", false);
	return document;
    }

    private SolrInputDocument createDocumentThreeSynonymsNoMainTerm() {
	SolrInputDocument document = new SolrInputDocument();

	document.setField(SolrParentDocFields.HISTORY_ACTION, "ADDED");
	document.setField(SolrConstants.ID_FIELD, "3b214059-8b33-4f13-85fa-e2ec036ebda4");
	document.setField(SolrParentDocFields.PROJECT_ID_INDEX_STORE, "2");
	document.setField(SolrParentDocFields.PARENT_ID_STORE, UUID.randomUUID());
	document.setField(SolrParentDocFields.SHORTCODE_INDEX_STORE, "IVA0002");
	document.setField(SolrParentDocFields.PROJECT_NAME_INDEX_STORE, "ivan");
	document.setField("termEntryDateCreated_LONG", "1479894716726");
	document.setField(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, new String[] { "en" });
	document.setField("en1_ID_STRING_STORE", "d605ccaf-3922-4488-a428-68bc0ab68857");
	document.setField("en1_status_STRING_STORE", "PROCESSED");
	document.setField("en1_userCreated_STRING_STORE", "idjurasevic");
	document.setField("en1_userModified_STRING_STORE", "idjurasevic");
	document.setField("en1_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en1_dateModified_LONG_STORE", "1479894716731");
	document.setField("en1_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en1_termName_STRING_STORE", "synonym on english 1");
	document.setField("en1_forbidden_BOOL_STORE", false);
	document.setField("en1_disabled_BOOL_STORE", false);
	document.setField("en1_userLatestChange_LONG_STORE", 3);
	document.setField("en1_inTranslationAsSource_BOOL_STORE", false);

	document.setField("en2_ID_STRING_STORE", "d605ccaf-3922-4488-a428-68bc0ab68858");
	document.setField("en2_status_STRING_STORE", "PROCESSED");
	document.setField("en2_userCreated_STRING_STORE", "idjurasevic");
	document.setField("en2_userModified_STRING_STORE", "idjurasevic");
	document.setField("en2_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en2_dateModified_LONG_STORE", "1479894716731");
	document.setField("en2_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en2_termName_STRING_STORE", "synonym on english 2");
	document.setField("en2_forbidden_BOOL_STORE", false);
	document.setField("en2_disabled_BOOL_STORE", false);
	document.setField("en2_userLatestChange_LONG_STORE", 3);
	document.setField("en2_inTranslationAsSource_BOOL_STORE", false);

	document.setField("en3_ID_STRING_STORE", "d605ccaf-3922-4488-a428-68bc0ab68859");
	document.setField("en3_status_STRING_STORE", "PROCESSED");
	document.setField("en3_userCreated_STRING_STORE", "idjurasevic");
	document.setField("en3_userModified_STRING_STORE", "idjurasevic");
	document.setField("en3_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en3_dateModified_LONG_STORE", "1479894716731");
	document.setField("en3_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en3_termName_STRING_STORE", "synonym on english 3");
	document.setField("en3_forbidden_BOOL_STORE", false);
	document.setField("en3_disabled_BOOL_STORE", false);
	document.setField("en3_userLatestChange_LONG_STORE", 3);
	document.setField("en3_inTranslationAsSource_BOOL_STORE", false);
	return document;
    }

    private SolrInputDocument createDocumentWithoutFirstSynonym() {
	SolrInputDocument document = new SolrInputDocument();

	document.setField(SolrParentDocFields.HISTORY_ACTION, "ADDED");
	document.setField(SolrConstants.ID_FIELD, "3b214059-8b33-4f13-85fa-e2ec036ebda4");
	document.setField(SolrParentDocFields.PROJECT_ID_INDEX_STORE, "2");
	document.setField(SolrParentDocFields.PARENT_ID_STORE, UUID.randomUUID());
	document.setField(SolrParentDocFields.SHORTCODE_INDEX_STORE, "IVA0002");
	document.setField(SolrParentDocFields.PROJECT_NAME_INDEX_STORE, "ivan");
	document.setField("termEntryDateCreated_LONG", "1479894716726");
	document.setField(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, new String[] { "en" });
	document.setField("en_ID_STRING_BASIC_SORT", "cabc772b-6a20-4a27-ac4c-8048adbf602e");
	document.setField("en_status_STRING_BASIC_SORT", "PROCESSED");
	document.setField("en_userCreated_STRING_BASIC_SORT", "idjurasevic");
	document.setField("en_userModified_STRING_BASIC_SORT", "idjurasevic");
	document.setField("en_dateCreated_LONG_SORT", "1479894716730");
	document.setField("en_dateModified_LONG_SORT", "1479894716730");
	document.setField("en_termName_STRING_STORE_SORT", "term on english");
	document.setField("en_forbidden_BOOL_STORE", false);
	document.setField("en_disabled_BOOL_STORE", false);
	document.setField("en_userLatestChange_LONG_STORE", 3);
	document.setField("en_inTranslationAsSource_BOOL_STORE", false);

	document.setField("en2_ID_STRING_STORE", "d605ccaf-3922-4488-a428-68bc0ab68857");
	document.setField("en2_status_STRING_STORE", "PROCESSED");
	document.setField("en2_userCreated_STRING_STORE", "idjurasevic");
	document.setField("en2_userModified_STRING_STORE", "idjurasevic");
	document.setField("en2_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en2_dateModified_LONG_STORE", "1479894716731");
	document.setField("en2_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en2_termName_STRING_STORE", "synonym on english 2");
	document.setField("en2_forbidden_BOOL_STORE", false);
	document.setField("en2_disabled_BOOL_STORE", false);
	document.setField("en2_userLatestChange_LONG_STORE", 3);
	document.setField("en2_inTranslationAsSource_BOOL_STORE", false);

	document.setField("en3_ID_STRING_STORE", "d605ccaf-3922-4488-a428-68bc0ab68857");
	document.setField("en3_status_STRING_STORE", "PROCESSED");
	document.setField("en3_userCreated_STRING_STORE", "idjurasevic");
	document.setField("en3_userModified_STRING_STORE", "idjurasevic");
	document.setField("en3_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en3_dateModified_LONG_STORE", "1479894716731");
	document.setField("en3_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en3_termName_STRING_STORE", "synonym on english 3");
	document.setField("en3_forbidden_BOOL_STORE", false);
	document.setField("en3_disabled_BOOL_STORE", false);
	document.setField("en3_userLatestChange_LONG_STORE", 3);
	document.setField("en3_inTranslationAsSource_BOOL_STORE", false);
	return document;
    }

    private SolrInputDocument createDocumentWithoutFrenchMainTerm() {
	SolrInputDocument document = new SolrInputDocument();

	document.setField(SolrParentDocFields.HISTORY_ACTION, "ADDED");
	document.setField(SolrConstants.ID_FIELD, "3b214059-8b33-4f13-85fa-e2ec036ebda4");
	document.setField(SolrParentDocFields.PROJECT_ID_INDEX_STORE, "2");
	document.setField(SolrParentDocFields.PARENT_ID_STORE, UUID.randomUUID());
	document.setField(SolrParentDocFields.SHORTCODE_INDEX_STORE, "IVA0002");
	document.setField(SolrParentDocFields.PROJECT_NAME_INDEX_STORE, "ivan");
	document.setField("termEntryDateCreated_LONG", "1479894716726");
	document.setField(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, new String[] { "de", "en", "fr" });
	document.setField("de_ID_STRING_BASIC_SORT", "80464d26-6bb5-496f-a2ba-7a2b84c4729a");
	document.setField("de_status_STRING_BASIC_SORT", "PROCESSED");
	document.setField("de_userCreated_STRING_BASIC_SORT", "idjurasevic");
	document.setField("de_userModified_STRING_BASIC_SORT", "idjurasevic");
	document.setField("de_dateCreated_LONG_SORT", "1479894716731");
	document.setField("de_dateModified_LONG_SORT", "1479894716731");
	document.setField("de_termName_STRING_STORE_SORT", "term on german");
	document.setField("de_forbidden_BOOL_STORE", "false");
	document.setField("de_disabled_BOOL_STORE", false);
	document.setField("de_userLatestChange_LONG_STORE", 3);
	document.setField("de_inTranslationAsSource_BOOL_STORE", false);
	document.setField("de1_ID_STRING_STORE", "f997bf2b-3c0c-42fe-a02f-c1a969dedfc8");
	document.setField("de1_status_STRING_STORE", "PROCESSED");
	document.setField("de1_userCreated_STRING_STORE", "idjurasevic");
	document.setField("de1_userModified_STRING_STORE", "idjurasevic");
	document.setField("de1_dateCreated_LONG_STORE", "1479894716732");
	document.setField("de1_dateModified_LONG_STORE", "1479894716732");
	document.setField("de1_termName_STRING_STORE", "synonym on german");
	document.setField("de1_forbidden_BOOL_STORE", false);
	document.setField("de1_disabled_BOOL_STORE", false);
	document.setField("de1_userLatestChange_LONG_STORE", 3);
	document.setField("de1_inTranslationAsSource_BOOL_STORE", false);

	document.setField("en_ID_STRING_BASIC_SORT", "cabc772b-6a20-4a27-ac4c-8048adbf602e");
	document.setField("en_status_STRING_BASIC_SORT", "PROCESSED");
	document.setField("en_userCreated_STRING_BASIC_SORT", "idjurasevic");
	document.setField("en_userModified_STRING_BASIC_SORT", "idjurasevic");
	document.setField("en_dateCreated_LONG_SORT", "1479894716730");
	document.setField("en_dateModified_LONG_SORT", "1479894716730");
	document.setField("en_termName_STRING_STORE_SORT", "term on english");
	document.setField("en_forbidden_BOOL_STORE", false);
	document.setField("en_disabled_BOOL_STORE", false);
	document.setField("en_userLatestChange_LONG_STORE", 3);
	document.setField("en_inTranslationAsSource_BOOL_STORE", false);
	document.setField("en1_ID_STRING_STORE", "d605ccaf-3922-4488-a428-68bc0ab68857");
	document.setField("en1_status_STRING_STORE", "PROCESSED");
	document.setField("en1_userCreated_STRING_STORE", "idjurasevic");
	document.setField("en1_userModified_STRING_STORE", "idjurasevic");
	document.setField("en1_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en1_dateModified_LONG_STORE", "1479894716731");
	document.setField("en1_dateCreated_LONG_STORE", "1479894716731");
	document.setField("en1_termName_STRING_STORE", "synonym on english");
	document.setField("en1_forbidden_BOOL_STORE", false);
	document.setField("en1_disabled_BOOL_STORE", false);
	document.setField("en1_userLatestChange_LONG_STORE", 3);
	document.setField("en1_inTranslationAsSource_BOOL_STORE", false);

	/* French language don't have main term */
	document.setField("fr1_ID_STRING_STORE", "aece7714-e946-43f4-a46c-e9f0d2b72525");
	document.setField("fr1_status_STRING_STORE", "PROCESSED");
	document.setField("fr1_userCreated_STRING_STORE", "idjurasevic");
	document.setField("fr1_userModified_STRING_STORE", "idjurasevic");
	document.setField("fr1_dateCreated_LONG_STORE", "1479894716731");
	document.setField("fr1_dateModified_LONG_STORE", "1479894716731");
	document.setField("fr1_termName_STRING_STORE", "synonym on french");
	document.setField("fr1_forbidden_BOOL_STORE", false);
	document.setField("fr1_disabled_BOOL_STORE", false);
	document.setField("fr1_userLatestChange_LONG_STORE", 3);
	return document;
    }
}
