package org.gs4tr.termmanager.webservice.utils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonValidatorUtils {

    public static void validateJsonProjectContent(JsonNode jsonProject) {
	assertNotNull(jsonProject);

	JsonNode jsonProjectInfo = jsonProject.get("projectInfo");
	assertNotNull(jsonProjectInfo);
	assertNotNull(jsonProjectInfo.get("enabled"));
	assertNotNull(jsonProjectInfo.get("shortCode"));
	assertNotNull(jsonProjectInfo.get("name"));

	JsonNode jsonProjectTicket = jsonProject.get("projectTicket");
	assertTrue(StringUtils.isNotBlank(jsonProjectTicket.asText()));

	JsonNode jsonOrganizationName = jsonProject.get("organizationName");
	assertTrue(StringUtils.isNotBlank(jsonOrganizationName.asText()));

	assertNotNull(jsonProject.get("languages"));
    }

    public static void validateJsonProjectMetadataContent(JsonNode jsonProjectMetadata) {
	assertNotNull(jsonProjectMetadata);

	assertNotNull(jsonProjectMetadata.get("username"));
	assertNotNull(jsonProjectMetadata.get("password"));
	assertNotNull(jsonProjectMetadata.get("organizationName"));
	assertNotNull(jsonProjectMetadata.get("projectShortcode"));
	assertNotNull(jsonProjectMetadata.get("languages"));

    }

    public static void validateJsonTermEntryContent(JsonNode jsonTermEntry) {
	assertNotNull(jsonTermEntry);

	assertNotNull(jsonTermEntry.get("creationDate"));
	assertNotNull(jsonTermEntry.get("modificationDate"));
	assertNotNull(jsonTermEntry.get("creationUser"));
	assertNotNull(jsonTermEntry.get("modificationUser"));

	JsonNode descriptions = jsonTermEntry.get("descriptions");
	assertNotNull(descriptions);

	for (JsonNode jsonDescription : descriptions) {
	    assertNotNull(jsonDescription);
	    assertNotNull(jsonDescription.get("value"));
	    assertNotNull(jsonDescription.get("type"));
	    assertNotNull(jsonDescription.get("baseType"));
	}

	JsonNode langSet = jsonTermEntry.get("langList");
	assertNotNull(langSet);

	for (JsonNode jsonLanguage : langSet) {
	    assertNotNull(jsonLanguage);
	    assertNotNull(jsonLanguage.get("locale"));
	    JsonNode jsonTerms = jsonLanguage.get("terms");
	    assertNotNull(jsonTerms);
	    for (JsonNode jsonTerm : jsonTerms) {
		String termName = jsonTerm.get("termText").asText();
		assertTrue(StringUtils.isNotBlank(termName));
		assertNotNull(jsonTerm.get("descriptions"));
		assertNotNull(jsonTerm.get("forbidden"));

		/*
		 * Glossary export term entries restV2 should return term statuses
		 */
		assertNotNull(jsonTerm.get("status"));
	    }
	}
    }

    public static void validateProjectLanguagesIncludedInResponse(JsonNode project) {
	assertEquals(2, project.get("languages").size());
    }

    // TODO: re-factor this code because violates the DRY principle.
    public static void validateSegmentTermHitContent(JsonNode termHit) {
	assertNotNull(termHit);
	assertNotNull(termHit.get("locale"));
	assertNotNull(termHit.get("termHit"));

	JsonNode descriptions = termHit.get("descriptions");
	assertNotNull(descriptions);

	for (JsonNode jsonDescription : descriptions) {
	    assertNotNull(jsonDescription);
	    assertNotNull(jsonDescription.get("value"));
	    assertNotNull(jsonDescription.get("type"));
	    assertNotNull(jsonDescription.get("baseType"));
	}

	JsonNode langSet = termHit.get("targets");
	assertNotNull(langSet);

	for (JsonNode jsonLanguage : langSet) {
	    assertNotNull(jsonLanguage);
	    assertNotNull(jsonLanguage.get("locale"));
	    JsonNode jsonTerms = jsonLanguage.get("terms");
	    assertNotNull(jsonTerms);
	    for (JsonNode jsonTerm : jsonTerms) {
		String termName = jsonTerm.get("termText").asText();
		assertTrue(StringUtils.isNotBlank(termName));
		assertNotNull(jsonTerm.get("descriptions"));
		assertNotNull(jsonTerm.get("forbidden"));
	    }
	}
    }

    // TODO: re-factor this code because violates the DRY principle.
    public static void validateTermEntryHitContent(JsonNode hit) {
	assertNotNull(hit);
	assertNotNull(hit.get("termEntryId"));

	JsonNode descriptions = hit.get("entryDescriptions");

	if (descriptions != null) {
	    for (JsonNode jsonDescription : descriptions) {
		assertNotNull(jsonDescription);
		assertNotNull(jsonDescription.get("value"));
		assertNotNull(jsonDescription.get("type"));
		assertNotNull(jsonDescription.get("baseType"));
		assertNotNull(jsonDescription.get("markerId"));
	    }
	}

	JsonNode sources = hit.get("sources");
	assertNotNull(sources);
	assertNotNull(sources.get("locale"));
	JsonNode jsonSourceTerms = sources.get("terms");
	assertNotNull(jsonSourceTerms);
	for (JsonNode jsonTerm : jsonSourceTerms) {
	    String termName = jsonTerm.get("termText").asText();
	    assertTrue(StringUtils.isNotBlank(termName));

	    JsonNode sourceTermDescription = jsonTerm.get("descriptions");

	    assertNotNull(sourceTermDescription);

	    for (JsonNode description : sourceTermDescription) {
		assertNotNull(description.get("markerId"));
	    }

	    assertNotNull(jsonTerm.get("forbidden"));
	    /* Search term entries restV2 should return term statuses */
	    assertNotNull(jsonTerm.get("status"));
	    assertNotNull(jsonTerm.get("termId"));
	}

	JsonNode targets = hit.get("targets");
	assertNotNull(targets);

	for (JsonNode jsonLanguage : targets) {
	    assertNotNull(jsonLanguage);
	    assertNotNull(jsonLanguage.get("locale"));
	    JsonNode jsonTargetTerms = jsonLanguage.get("terms");
	    assertNotNull(jsonTargetTerms);
	    for (JsonNode jsonTerm : jsonTargetTerms) {
		String termName = jsonTerm.get("termText").asText();
		assertTrue(StringUtils.isNotBlank(termName));

		JsonNode targetTermDescription = jsonTerm.get("descriptions");
		assertNotNull(targetTermDescription);

		for (JsonNode description : targetTermDescription) {
		    assertNotNull(description.get("markerId"));
		}

		assertNotNull(jsonTerm.get("forbidden"));
		/* Search term entries (restV2) should return term statuses */
		assertNotNull(jsonTerm.get("status"));
		assertNotNull(jsonTerm.get("termId"));
	    }
	}
    }

}
