package org.gs4tr.termmanager.persistence.solr.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.junit.Assert;
import org.junit.Test;

public class SolrQueryUtilsTest {
    private static final String SEARCH_TEXT = "searchText";

    @Test
    public void createConcordanceTermTextQuery() {
	List<String> languages = new ArrayList<String>();
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setTempTermSearch(false);
	languages.add(Locale.GERMAN.getCode());
	languages.add(Locale.FRENCH.getCode());
	filter.setSourceLanguage(Locale.ENGLISH.getCode());
	filter.setTargetLanguages(languages);
	TextFilter text = new TextFilter(SEARCH_TEXT);
	text.setAllTextSearch(true);
	text.setExactMatch(true);
	text.setCaseSensitive(true);
	text.setSegmentSearch(false);
	filter.setTextFilter(text);

	SolrQuery query = SolrQueryUtils.createSearchQuery(null, filter);

	Assert.assertEquals(
		"{!cqp cqp.cs=\"true\" cqp.t=\"EXACT\" df=\"termName_en_NGRAM_INDEX,attribute_en_NGRAM_INDEX_MULTI,note_en_NGRAM_INDEX_MULTI,termName_de_NGRAM_INDEX,attribute_de_NGRAM_INDEX_MULTI,note_de_NGRAM_INDEX_MULTI,termName_fr_NGRAM_INDEX,attribute_fr_NGRAM_INDEX_MULTI,note_fr_NGRAM_INDEX_MULTI\" pf=\"type_STRING_INDEX\\:PARENT\"}searchText",
		query.getQuery());
    }

    @Test
    public void createConcordanceTextQuery() {
	List<String> languages = new ArrayList<String>();
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setTempTermSearch(true);
	languages.add(Locale.GERMAN.getCode());
	languages.add(Locale.FRENCH.getCode());
	filter.setSourceLanguage(Locale.ENGLISH.getCode());
	filter.setTargetLanguages(languages);
	TextFilter text = new TextFilter(SEARCH_TEXT);
	text.setAllTextSearch(true);
	text.setExactMatch(true);
	text.setCaseSensitive(true);
	text.setSegmentSearch(false);
	filter.setTextFilter(text);

	SolrQuery query = SolrQueryUtils.createSearchQuery(null, filter);

	Assert.assertEquals(
		"{!cqp cqp.cs=\"true\" cqp.t=\"EXACT\" df=\"tempTermName_en_NGRAM_INDEX,attribute_en_NGRAM_INDEX_MULTI,note_en_NGRAM_INDEX_MULTI,tempTermName_de_NGRAM_INDEX,attribute_de_NGRAM_INDEX_MULTI,note_de_NGRAM_INDEX_MULTI,tempTermName_fr_NGRAM_INDEX,attribute_fr_NGRAM_INDEX_MULTI,note_fr_NGRAM_INDEX_MULTI\" pf=\"type_STRING_INDEX\\:PARENT\"}searchText",
		query.getQuery());
    }

    @Test
    public void createSegmentConcordanceTextQueryWithAttributes() {
	List<String> languages = new ArrayList<String>();
	TmgrSearchFilter filter = new TmgrSearchFilter();
	Set<String> attrbute = new HashSet<String>();
	attrbute.add("filename");
	attrbute.add("context");
	Set<String> note = new HashSet<String>();
	attrbute.add("note");
	attrbute.add("note1");
	filter.setAttributeTypeFilter(attrbute);
	filter.setNoteTypeFilter(note);
	languages.add(Locale.GERMAN.getCode());
	languages.add(Locale.FRENCH.getCode());
	filter.setSourceLanguage(Locale.ENGLISH.getCode());
	filter.setTargetLanguages(languages);
	TextFilter text = new TextFilter(SEARCH_TEXT);

	text.setSegmentSearch(false);
	text.setFuzzyMatch(true);
	filter.setTextFilter(text);

	SolrQuery query = SolrQueryUtils.createSearchQuery(null, filter);
	Assert.assertEquals(
		"{!cqp cqp.cs=\"true\" cqp.t=\"EXACT\" df=\"attribute_en_NGRAM_INDEX_MULTI,attribute_de_NGRAM_INDEX_MULTI,attribute_fr_NGRAM_INDEX_MULTI,note_en_NGRAM_INDEX_MULTI,note_de_NGRAM_INDEX_MULTI,note_fr_NGRAM_INDEX_MULTI\" pf=\"type_STRING_INDEX\\:PARENT\"}note filename context note1 ",
		query.getQuery());

    }

    @Test
    public void createSegmentExactTextQuery() {
	List<String> languages = new ArrayList<String>();
	TmgrSearchFilter filter = new TmgrSearchFilter();
	languages.add(Locale.GERMAN.getCode());
	languages.add(Locale.FRENCH.getCode());
	filter.setSourceLanguage(Locale.ENGLISH.getCode());
	filter.setTargetLanguages(languages);
	TextFilter text = new TextFilter(SEARCH_TEXT);

	text.setSegmentSearch(true);
	text.setFuzzyMatch(false);
	filter.setTextFilter(text);

	SolrQuery query = SolrQueryUtils.createSearchQuery(null, filter);
	Assert.assertEquals(
		"{!sdqp df=\"termName_en_SUB_INDEX,termName_de_SUB_INDEX,termName_fr_SUB_INDEX\" pf=\"type_STRING_INDEX\\:PARENT\"}searchText",
		query.getQuery());
    }

    @Test
    public void createSegmentFuzzyTextQuery() {
	List<String> languages = new ArrayList<String>();
	TmgrSearchFilter filter = new TmgrSearchFilter();
	languages.add(Locale.GERMAN.getCode());
	languages.add(Locale.FRENCH.getCode());
	filter.setSourceLanguage(Locale.ENGLISH.getCode());
	filter.setTargetLanguages(languages);
	TextFilter text = new TextFilter(SEARCH_TEXT);

	text.setSegmentSearch(true);
	text.setFuzzyMatch(true);
	filter.setTextFilter(text);

	SolrQuery query = SolrQueryUtils.createSearchQuery(null, filter);

	Assert.assertEquals(
		"{!sdqp df=\"termName_en_SUB_STEMMED_INDEX,termName_de_SUB_STEMMED_INDEX,termName_fr_SUB_STEMMED_INDEX\" pf=\"type_STRING_INDEX\\:PARENT\"}searchText",
		query.getQuery());
    }
}
