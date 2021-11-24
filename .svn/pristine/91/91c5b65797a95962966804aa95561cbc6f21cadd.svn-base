package org.gs4tr.termmanager.solr.plugin.utils;

import static org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper.createDynamicFieldName;

import org.gs4tr.foundation.locale.Locale;
import org.junit.Assert;
import org.junit.Test;

public class SolrDocHelperTest {

    @Test
    public void testCreateDynamicField() {
	String result = createDynamicFieldName(Locale.ENGLISH.getCode(), SolrParentDocFields.DYN_PARENT_ID_STORE);
	Assert.assertEquals("en_parentUuid_STRING_STORE", result);

	result = createDynamicFieldName(Locale.GERMANY.getCode(), SolrParentDocFields.DYN_PARENT_ID_STORE);
	Assert.assertEquals("de-DE_parentUuid_STRING_STORE", result);

	result = createDynamicFieldName(Locale.makeLocale("sr-sp").getCode(), SolrParentDocFields.DYN_PARENT_ID_STORE);
	Assert.assertEquals("sr-SP_parentUuid_STRING_STORE", result);
	result = createDynamicFieldName(SolrParentDocFields.ATTR_PREFIX, Locale.makeLocale("nl-be").getCode(),
		SolrChildDocFileds.NGRAM_INDEX_SUFFIX);
	Assert.assertEquals("attribute_nl-BE_NGRAM_INDEX", result);
    }
}
