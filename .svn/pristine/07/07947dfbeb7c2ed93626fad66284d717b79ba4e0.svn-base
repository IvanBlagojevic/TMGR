package org.gs4tr.termmanager.tests;

import java.util.Arrays;
import java.util.Set;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("approve_term_translation")
public class ApproveTermTranslationTaskHandlerTest extends AbstractSolrGlossaryTest {

    private Long ID_PROJECT = 1L;
    private String ID_SUB_TERM = "c502608e-uuid-sub-term-005";
    private String ID_SUB_TERM_ENTRY = "uuid-sub-term-entry-003";
    private String ID_TERM = "c502608e-uuid-term-0122";
    private String ID_TERM_ENTRY = "c502608e-uuid-term-entry-005";

    private String PROJECT_NAME = "projectName";

    private final String languageId = "en-US";

    @Test
    @TestCase("process_tasks")
    public void approveTermTranslationDateModifiedTest() throws TmException {

	String taskName = "approve term translation status";

	String submissionTicket = TicketConverter.fromInternalToDto(1L);
	String targetTickets = ID_SUB_TERM;

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "approveTermTranslation.json",
		new String[] { "$submissionTicket", submissionTicket },
		new String[] { "$targetTickets", targetTickets });

	ProjectDetail projectDetailBefore = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);

	Term termBefore = getTermService().findTermById(ID_TERM, 1L);
	TermEntry termEntryBefore = getTermEntryService().findTermEntryById(ID_TERM_ENTRY, 1L);

	taskHandler.processTasks(null, null, command, null);

	Term termAfter = getTermService().findTermById(ID_TERM, 1L);
	TermEntry termEntryAfter = getTermEntryService().findTermEntryById(ID_TERM_ENTRY, 1L);

	ProjectDetail projectDetailAfter = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);

	Assert.assertTrue(
		projectDetailBefore.getDateModified().getTime() < projectDetailAfter.getDateModified().getTime());

	Assert.assertTrue(termBefore.getDateModified() < termAfter.getDateModified());
	Assert.assertTrue(termEntryBefore.getDateModified() < termEntryAfter.getDateModified());

	Set<ProjectLanguageDetail> projectLanguageDetailsBefore = projectDetailBefore.getLanguageDetails();
	Set<ProjectLanguageDetail> projectLanguageDetailsAfter = projectDetailAfter.getLanguageDetails();

	// Check if language date modified is changed for IN FINAL REVIEW
	// language
	Assert.assertTrue(isProjectLanguageDetailDateModifiedChanged("en-US", projectLanguageDetailsBefore,
		projectLanguageDetailsAfter));

	Assert.assertFalse(isProjectLanguageDetailDateModifiedChanged("sr-RS", projectLanguageDetailsBefore,
		projectLanguageDetailsAfter));

	Assert.assertFalse(isProjectLanguageDetailDateModifiedChanged("de-DE", projectLanguageDetailsBefore,
		projectLanguageDetailsAfter));

	Assert.assertFalse(isProjectLanguageDetailDateModifiedChanged("fr-FR", projectLanguageDetailsBefore,
		projectLanguageDetailsAfter));
    }

    private void addInFinalReviewTerms() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(ID_TERM_ENTRY);
	termEntry.setProjectId(ID_PROJECT);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserModified("pm");
	termEntry.setUserCreated("pm");

	Term term1 = createTerm(ID_TERM, "en-US", "Cat", false, ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(), "pm",
		true);
	termEntry.addTerm(term1);

	getTermEntryService().updateRegularTermEntries(ID_PROJECT, Arrays.asList(termEntry));

	TermEntry subTermEntry = new TermEntry();
	subTermEntry.setParentUuId(ID_TERM_ENTRY);
	subTermEntry.setUuId(ID_SUB_TERM_ENTRY);
	subTermEntry.setProjectId(ID_PROJECT);
	subTermEntry.setProjectName(PROJECT_NAME);
	subTermEntry.setSubmissionId(1L);
	subTermEntry.setUserModified("pm");
	subTermEntry.setUserCreated("pm");

	Term subTerm1 = createTerm(ID_SUB_TERM, "en-US", "Cat", false, ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(),
		"pm", true);

	subTerm1.setParentUuId(term1.getUuId());
	subTerm1.setDateSubmitted(System.currentTimeMillis());

	subTermEntry.addTerm(subTerm1);

	getTermEntryService().updateSubmissionTermEntries(ID_PROJECT, Arrays.asList(subTermEntry));
    }

    @Override
    protected void populate() throws Exception {
	super.populate();
	addInFinalReviewTerms();
    }

}
