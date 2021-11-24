package org.gs4tr.termmanager.service.mocking;

import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.SubmissionDetailInfo;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.listeners.SendToReviewEventListener;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("service")
public class SendToReviewEventListenerTest extends AbstractServiceTest {

    @Autowired
    private SendToReviewEventListener _sendToReviewEventListener;

    /*
     * TERII-5638 On project dashboard and details pending approval terms count is
     * incorrect
     */
    @Test
    @TestCase("sendToReviewEventListener")
    public void sentToPendingApprovalTermTest() {

	ProjectDetailInfo projectDetailInfo = new ProjectDetailInfo(1L);
	TermEntry regularTermEntry = getModelObject("termEntry2", TermEntry.class);

	EventMessage message = createEventMessage(projectDetailInfo, regularTermEntry);
	getSendToReviewEventListener().notify(message);

	long pendingApprovalTermCount = projectDetailInfo.getLanguagePendingTermCount().get("de-DE").get();

	Assert.assertEquals(-1, pendingApprovalTermCount);

    }

    @Test
    @TestCase("sendToReviewEventListener")
    public void sentToReviewOnHoldTermTest() {

	ProjectDetailInfo projectDetailInfo = new ProjectDetailInfo(1L);
	TermEntry regularTermEntry = getModelObject("termEntry1", TermEntry.class);

	EventMessage message = createEventMessage(projectDetailInfo, regularTermEntry);
	getSendToReviewEventListener().notify(message);

	long onHoldTermCount = projectDetailInfo.getLanguageOnHoldTermCount().get("de-DE").get();

	Assert.assertEquals(-1, onHoldTermCount);

    }

    private EventMessage createEventMessage(ProjectDetailInfo projectDetailInfo, TermEntry regularTermEntry) {

	EventMessage message = new EventMessage("MessageId");

	regularTermEntry.setProjectId(1L);
	TermEntry submissionTermEntry = new TermEntry();
	Submission submission = getModelObject("submission", Submission.class);
	SubmissionDetailInfo submissionInfo = new SubmissionDetailInfo(1L, "en-US");
	UpdateCommand command = getModelObject("command", UpdateCommand.class);

	message.addContextVariable(EventMessage.VARIABLE_DETAIL_INFO, projectDetailInfo);
	message.addContextVariable(EventMessage.VARIABLE_TERM_ENTRY, regularTermEntry);
	message.addContextVariable(EventMessage.VARIABLE_SUBMISSION_TERM_ENTRY, submissionTermEntry);
	message.addContextVariable(EventMessage.VARIABLE_SUBMISSION, submission);
	message.addContextVariable(EventMessage.VARIABLE_SUBMISSION_INFO, submissionInfo);
	message.addContextVariable(EventMessage.VARIABLE_REVIEW_REQUIRED, Boolean.FALSE);
	message.addContextVariable(EventMessage.VARIABLE_COMMAND, command);

	return message;
    }

    private SendToReviewEventListener getSendToReviewEventListener() {
	return _sendToReviewEventListener;
    }
}
