package org.gs4tr.termmanager.tests;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.gs4tr.termmanager.dao.backup.DbTermEntryDAO;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.AssignProjectAttributesCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("assign_project_attributes")
public class AssignProjectAttributesTaskHandlerTest extends AbstractSolrGlossaryTest {

    @Autowired
    private DbTermEntryDAO _dbTermEntryDAO;

    @Test
    @TestCase("get_task_infos")
    public void assignAttributesGetTest() throws Exception {
	ManualTaskHandler taskHandler = getHandler("assign project attributes");

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1), new Long(1) },
		"assign project attributes", null);

	String result = JsonUtils.writeValueAsString(taskInfos);

	System.out.println(result);

	assertJSONResponse(result, "assingProjectAttributes.json");
    }

    @Test
    @TestCase("process_tasks")
    public void assignAttributesProcessTasksTest() throws Exception {
	ManualTaskHandler taskHandler = getHandler("assign project attributes");

	String renameValue = "definition";

	Object command = getTaskHandlerCommand(taskHandler, "assingProjectAttributes.json",
		new String[] { "$renameValue", renameValue });

	Long projectId = 1L;

	Long[] taskIds = { projectId };

	taskHandler.processTasks(null, taskIds, command, null);

	List<Long> projectIds = Arrays.asList(taskIds);

	Map<Long, List<Attribute>> projectAttributes = getProjectService().findAttributesByProjectId(projectIds);

	Assert.assertTrue(!projectAttributes.isEmpty());

	List<Attribute> attributes = projectAttributes.get(projectId);

	boolean flag = false;
	for (Attribute attribute : attributes) {
	    if (renameValue.equals(attribute.getName())) {
		flag = true;
		break;
	    }
	}

	Assert.assertTrue(flag);

	Term term = getTermService().findTermById(TERM_ID_01, projectId);

	Set<Description> descriptions = term.getDescriptions();
	CollectionUtils.filter(descriptions, new Predicate() {
	    @Override
	    public boolean evaluate(Object item) {
		Description description = (Description) item;
		if (description.getBaseType().equals(Description.ATTRIBUTE)) {
		    return true;
		}
		return false;
	    }
	});

	Assert.assertTrue(CollectionUtils.isNotEmpty(descriptions));

	flag = false;
	for (Description description : descriptions) {
	    if (renameValue.equals(description.getType())) {
		flag = true;
		break;
	    }
	}

	Thread.sleep(500);
    }

    /*
     * TERII-6037 Term List | Missing terms for recoded language
     */
    @Test
    @TestCase("process_tasks")
    public void missingTermsAfterRemovingAttributesTest() {
	ManualTaskHandler taskHandler = getHandler("assign project attributes");

	Long projectId = 1L;

	DbTermEntry dbTermEntryBefore = getDbTermEntryDAO().findByUUID(TERM_ENTRY_ID_01);
	Assert.assertNotNull(dbTermEntryBefore);

	Set<DbTerm> termsBefore = dbTermEntryBefore.getTerms();
	Assert.assertTrue(CollectionUtils.isNotEmpty(termsBefore));

	// Remove Language and TermEntry Attributes
	AssignProjectAttributesCommand command = new AssignProjectAttributesCommand();
	command.setProjectAttributes(new ArrayList<>());

	Long[] taskIds = { projectId };

	taskHandler.processTasks(null, taskIds, command, null);

	waitServiceThreadPoolThreads();

	DbTermEntry dbTermEntryAfter = getDbTermEntryDAO().findByUUID(TERM_ENTRY_ID_01);
	Assert.assertNotNull(dbTermEntryAfter);

	Set<DbTerm> termsAfter = dbTermEntryAfter.getTerms();
	Assert.assertTrue(CollectionUtils.isNotEmpty(termsAfter));

	// Number of terms remains the same
	Assert.assertEquals(termsBefore.size(), termsAfter.size());
	termsBefore.forEach(termBefore -> termsAfter.forEach(termAfter -> {
	    if (termBefore.getUuId().equals(termAfter.getUuId())) {
		Assert.assertEquals(termBefore.getNameAsString(), termAfter.getNameAsString());
		Assert.assertEquals(termBefore.getLanguageId(), termAfter.getLanguageId());
		Assert.assertEquals(termBefore.getUserModified(), termAfter.getUserModified());
		Assert.assertEquals(termBefore.getUserCreated(), termAfter.getUserCreated());
		Assert.assertEquals(termBefore.getStatus(), termAfter.getStatus());
		Assert.assertEquals(termBefore.getForbidden(), termAfter.getForbidden());
		Assert.assertEquals(termBefore.getFirst(), termAfter.getFirst());
		Assert.assertEquals(termBefore.getInTranslationAsSource(), termAfter.getInTranslationAsSource());
		Assert.assertEquals(termBefore.getDateCreated(), termAfter.getDateCreated());
		Assert.assertTrue(termBefore.getDateModified().getTime() <= termAfter.getDateModified().getTime());
		Assert.assertTrue(isEmpty(termAfter.getDescriptions()));
	    }
	}));
    }

    private DbTermEntryDAO getDbTermEntryDAO() {
	return _dbTermEntryDAO;
    }
}
