package org.gs4tr.termmanager.tests;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Comment;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("add_comment")
public class AddCommentTaskHandlerTest extends AbstractSolrGlossaryTest {

    @Test
    @TestCase("process_tasks")
    public void addCommentPostTest() throws Exception {
	Long submissionId = 1L;
	String submissionTicket = TicketConverter.fromInternalToDto(submissionId);

	String taskName = "add comment";

	String commentText = "This is dummy comment";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "addComment.json",
		new String[] { "$commentText", commentText }, new String[] { "$submissionTicket", submissionTicket },
		new String[] { "$ticket1", SUB_TERM_ID_01 }, new String[] { "$ticket2", SUB_TERM_ID_02 });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	Term submissionTerm = getSubmissionTermService().findById(SUB_TERM_ID_01, PROJECT_ID);
	Assert.assertNotNull(submissionTerm);

	Set<Comment> termComments = submissionTerm.getComments();
	Assert.assertTrue(CollectionUtils.isNotEmpty(termComments));

	List<Comment> comments = termComments.stream().filter(c -> c.getText().equals(commentText))
		.collect(Collectors.toList());

	Assert.assertTrue(CollectionUtils.isNotEmpty(comments));
    }
}
