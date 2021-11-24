package org.gs4tr.termmanager.tests;

import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.manualtask.MergeManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.MergeCommands;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@TestSuite("merge_action")
public class MergeManualTaskHandlerTest extends AbstractSolrGlossaryTest {


    @Test
    @TestCase("process_tasks")
    public void mergeMultipleTermEntriesWithBlacklistedTermsAsSourceAndTargetAndFourLanguages() {
        String mergeTaskName = "merge term entries";

        ManualTaskHandler manualTaskHandler = getHandler(mergeTaskName);
        assertEquals(manualTaskHandler.getClass(), MergeManualTaskHandler.class);

        Object command = getTaskHandlerCommand(manualTaskHandler, "merge8.json",
                new String[]{"$matchedTermEntryId1", TERM_ENTRY_ID_07},
                new String[]{"$matchedTermEntryId2", TERM_ENTRY_ID_08},
                new String[]{"$matchedTermEntryId3", TERM_ENTRY_ID_09},
                new String[]{"$matchedTermEntryId4", TERM_ENTRY_ID_10});

        assertEquals(command.getClass(), MergeCommands.class);

        // Before merging
        TermEntry existingBeforeMerging = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_07, PROJECT_ID);
        TermEntry incoming1BeforeMerging = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_08, PROJECT_ID);
        TermEntry incoming2BeforeMerging = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_09, PROJECT_ID);
        TermEntry incoming3BeforeMerging = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_10, PROJECT_ID);

        assertEquals(3, existingBeforeMerging.getLanguageTerms().size());
        assertEquals(3, incoming1BeforeMerging.getLanguageTerms().size());
        assertEquals(2, incoming2BeforeMerging.getLanguageTerms().size());
        assertEquals(2, incoming3BeforeMerging.getLanguageTerms().size());

        Term germanBlacklistedTerm = getTermService().findTermById(TERM_ID_19, PROJECT_ID);
        Term englishBlacklistedTerm = getTermService().findTermById(TERM_ID_23, PROJECT_ID);

        String blacklisted = ItemStatusTypeHolder.BLACKLISTED.getName();
        assertEquals(blacklisted, germanBlacklistedTerm.getStatus());
        assertEquals(blacklisted, englishBlacklistedTerm.getStatus());

        assertTrue(existingBeforeMerging.getLanguageTerms().get("de-DE").contains(germanBlacklistedTerm));
        assertTrue(incoming2BeforeMerging.getLanguageTerms().get("en-US").contains(englishBlacklistedTerm));

        // Merging
        TaskResponse taskResponse = manualTaskHandler.processTasks(null, null, command, null);
        assertNotNull(taskResponse);

        // After merging
        TermEntry existing = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_07, PROJECT_ID);
        TermEntry incoming1 = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_08, PROJECT_ID);
        TermEntry incoming2 = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_09, PROJECT_ID);
        TermEntry incoming3 = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_10, PROJECT_ID);

        assertEquals(0, incoming1.getLanguageTerms().size());
        assertEquals(0, incoming2.getLanguageTerms().size());
        assertEquals(0, incoming3.getLanguageTerms().size());
        assertEquals(4, existing.getLanguageTerms().size());

        Map<String, Set<Term>> languageTerms = existing.getLanguageTerms();
        Set<Term> englishTerms = languageTerms.get("en-US");
        Set<Term> frenchTerms = languageTerms.get("fr-FR");
        Set<Term> germanTerms = languageTerms.get("de-DE");
        Set<Term> norwegianTerms = languageTerms.get("no-NO");

        assertEquals(3, englishTerms.size());
        assertEquals(2, frenchTerms.size());
        assertEquals(3, germanTerms.size());
        assertEquals(1, norwegianTerms.size());
        assertTrue(englishTerms.contains(englishBlacklistedTerm));
        assertTrue(germanTerms.contains(germanBlacklistedTerm));

    }

    @Test
    @TestCase("process_tasks")
    public void mergeMultipleTermEntriesWithFourDifferentLanguages() {

        String mergeTaskName = "merge term entries";

        ManualTaskHandler manualTaskHandler = getHandler(mergeTaskName);
        assertEquals(manualTaskHandler.getClass(), MergeManualTaskHandler.class);

        Object command = getTaskHandlerCommand(manualTaskHandler, "merge8.json",
                new String[]{"$matchedTermEntryId1", TERM_ENTRY_ID_03},
                new String[]{"$matchedTermEntryId2", TERM_ENTRY_ID_04},
                new String[]{"$matchedTermEntryId3", TERM_ENTRY_ID_05},
                new String[]{"$matchedTermEntryId4", TERM_ENTRY_ID_06});

        assertEquals(command.getClass(), MergeCommands.class);

        TaskResponse taskResponse = manualTaskHandler.processTasks(null, null, command, null);
        assertNotNull(taskResponse);

        // After merging
        TermEntry existing = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_03, PROJECT_ID);
        TermEntry incoming1 = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_04, PROJECT_ID);
        TermEntry incoming2 = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_05, PROJECT_ID);
        TermEntry incoming3 = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_06, PROJECT_ID);

        assertEquals(0, incoming1.getLanguageTerms().size());
        assertEquals(0, incoming2.getLanguageTerms().size());
        assertEquals(0, incoming3.getLanguageTerms().size());
        assertEquals(4, existing.getLanguageTerms().size());

    }

    @Test(expected = UserException.class)
    @TestCase("process_tasks")
    public void mergingTwoTermEntriesFromDifferentProjectsThrowsException() {

        String mergeTaskName = "merge term entries";

        ManualTaskHandler manualTaskHandler = getHandler(mergeTaskName);
        assertEquals(manualTaskHandler.getClass(), MergeManualTaskHandler.class);

        Object command = getTaskHandlerCommand(manualTaskHandler, "merge9.json",
                new String[]{"$matchedTermEntryId1", TERM_ENTRY_ID_14},
                new String[]{"$matchedTermEntryId2", TERM_ENTRY_ID_15});


        assertEquals(command.getClass(), MergeCommands.class);

        //Before merging
        TermEntry existingBeforeMerging = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_14, PROJECT_ID);
        TermEntry incoming1BeforeMerging = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_15, PROJECT_ID_2);

        assertTrue(!existingBeforeMerging.getProjectId().equals(incoming1BeforeMerging.getProjectId()));

        // Merging
        TaskResponse taskResponse = manualTaskHandler.processTasks(null, null, command, null);
        assertNotNull(taskResponse);

    }

    @Test
    @TestCase("process_tasks")
    public void testExistingHasHigherLevel() {

        String mergeTaskName = "merge term entries";

        ManualTaskHandler manualTaskHandler = getHandler(mergeTaskName);
        assertEquals(manualTaskHandler.getClass(), MergeManualTaskHandler.class);

        Object command = getTaskHandlerCommand(manualTaskHandler, "merge8.json",
                new String[]{"$matchedTermEntryId1", TERM_ENTRY_ID_04},
                new String[]{"$matchedTermEntryId2", TERM_ENTRY_ID_07});

        assertEquals(command.getClass(), MergeCommands.class);

        // Before merging
        TermEntry existingBeforeMerging = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_04, PROJECT_ID);
        TermEntry incomingBeforeMerging = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_07, PROJECT_ID);

        Term englishApprovedTerm = getTermService().findTermById(TERM_ID_10, PROJECT_ID);
        Term englishPendingTerm = getTermService().findTermById(TERM_ID_17, PROJECT_ID);

        assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), englishApprovedTerm.getStatus());
        assertEquals(ItemStatusTypeHolder.WAITING.getName(), englishPendingTerm.getStatus());

        assertTrue(existingBeforeMerging.getLanguageTerms().get("en-US").contains(englishApprovedTerm));
        assertTrue(incomingBeforeMerging.getLanguageTerms().get("en-US").contains(englishPendingTerm));

        assertEquals(2, existingBeforeMerging.getLanguageTerms().size());
        assertEquals(3, incomingBeforeMerging.getLanguageTerms().size());

        // Merging
        TaskResponse taskResponse = manualTaskHandler.processTasks(null, null, command, null);
        assertNotNull(taskResponse);

        // After merging
        TermEntry existingAfterMerge = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_04, PROJECT_ID);
        TermEntry incomingAfterMerge = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_07, PROJECT_ID);

        assertEquals(3, existingAfterMerge.getLanguageTerms().size());
        assertEquals(0, incomingAfterMerge.getLanguageTerms().size());

        Map<String, Set<Term>> languageTerms = existingAfterMerge.getLanguageTerms();
        Set<Term> englishTerms = languageTerms.get("en-US");

        assertEquals(1, englishTerms.size());

        Term englishApprovedTermAfterMerge = getTermService().findTermById(TERM_ID_10, PROJECT_ID);
        Term englishPendingTermAfterMerge = getTermService().findTermById(TERM_ID_17, PROJECT_ID);

        assertTrue(englishTerms.contains(englishApprovedTermAfterMerge));
        assertNull(englishPendingTermAfterMerge);

    }

    @Test
    @TestCase("process_tasks")
    public void testExistingHasLowerLevel() throws Exception {

        String mergeTaskName = "merge term entries";

        ManualTaskHandler manualTaskHandler = getHandler(mergeTaskName);
        assertEquals(manualTaskHandler.getClass(), MergeManualTaskHandler.class);

        Object command = getTaskHandlerCommand(manualTaskHandler, "merge8.json",
                new String[]{"$matchedTermEntryId1", TERM_ENTRY_ID_07},
                new String[]{"$matchedTermEntryId2", TERM_ENTRY_ID_08});

        assertEquals(command.getClass(), MergeCommands.class);

        // Before merging
        TermEntry existingBeforeMerging = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_07, PROJECT_ID);
        TermEntry incomingBeforeMerging = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_08, PROJECT_ID);

        Term englishApprovedTerm = getTermService().findTermById(TERM_ID_20, PROJECT_ID);
        Term englishPendingTerm = getTermService().findTermById(TERM_ID_17, PROJECT_ID);

        assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), englishApprovedTerm.getStatus());
        assertEquals(ItemStatusTypeHolder.WAITING.getName(), englishPendingTerm.getStatus());

        assertTrue(existingBeforeMerging.getLanguageTerms().get("en-US").contains(englishPendingTerm));
        assertTrue(incomingBeforeMerging.getLanguageTerms().get("en-US").contains(englishApprovedTerm));

        assertEquals(3, existingBeforeMerging.getLanguageTerms().size());
        assertEquals(3, incomingBeforeMerging.getLanguageTerms().size());

        // Merging
        TaskResponse taskResponse = manualTaskHandler.processTasks(null, null, command, null);
        assertNotNull(taskResponse);

        // After merging
        TermEntry existingAfterMerge = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_07, PROJECT_ID);
        TermEntry incomingAfterMerge = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_08, PROJECT_ID);

        assertEquals(3, existingAfterMerge.getLanguageTerms().size());
        assertEquals(0, incomingAfterMerge.getLanguageTerms().size());

        Map<String, Set<Term>> languageTerms = existingAfterMerge.getLanguageTerms();
        Set<Term> englishTerms = languageTerms.get("en-US");

        assertEquals(1, englishTerms.size());

        Term englishApprovedTermAfterMerge = getTermService().findTermById(TERM_ID_17, PROJECT_ID);
        Term englishPendingTermAfterMerge = getTermService().findTermById(TERM_ID_20, PROJECT_ID);

        assertTrue(englishTerms.contains(englishApprovedTermAfterMerge));
        assertNull(englishPendingTermAfterMerge);

    }

    @Test
    @TestCase("process_tasks")
    public void whenOneOfTermsHasStatusBlacklistedItIsSetAsSynonyme() throws Exception {

        String mergeTaskName = "merge term entries";

        ManualTaskHandler manualTaskHandler = getHandler(mergeTaskName);
        assertEquals(manualTaskHandler.getClass(), MergeManualTaskHandler.class);

        Object command = getTaskHandlerCommand(manualTaskHandler, "merge8.json",
                new String[]{"$matchedTermEntryId1", TERM_ENTRY_ID_07},
                new String[]{"$matchedTermEntryId2", TERM_ENTRY_ID_10});

        assertEquals(command.getClass(), MergeCommands.class);

        // Before merging
        TermEntry existingBeforeMerging = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_07, PROJECT_ID);
        TermEntry incomingBeforeMerging = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_10, PROJECT_ID);

        Map<String, Set<Term>> existingLanguageTerms = existingBeforeMerging.getLanguageTerms();
        Term germanBlacklistedTerm = getTermService().findTermById(TERM_ID_19, PROJECT_ID);
        assertEquals("germanTerm", germanBlacklistedTerm.getName());
        assertEquals(ItemStatusTypeHolder.BLACKLISTED.getName(), germanBlacklistedTerm.getStatus());
        Set<Term> existingGermanTerms = existingLanguageTerms.get("de-DE");
        assertEquals(1, existingGermanTerms.size());
        assertTrue(existingGermanTerms.contains(germanBlacklistedTerm));

        Map<String, Set<Term>> incomingLanguageTerms = incomingBeforeMerging.getLanguageTerms();
        Term germanApprovedTerm = getTermService().findTermById(TERM_ID_26, PROJECT_ID);
        assertEquals("germanTerm", germanApprovedTerm.getName());
        assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), germanApprovedTerm.getStatus());
        Set<Term> incomingGermanTerms = incomingLanguageTerms.get("de-DE");
        assertEquals(1, incomingGermanTerms.size());
        assertTrue(incomingGermanTerms.contains(germanApprovedTerm));

        // Merging
        TaskResponse taskResponse = manualTaskHandler.processTasks(null, null, command, null);
        assertNotNull(taskResponse);

        // After merging
        TermEntry existingAfterMerge = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_07, PROJECT_ID);
        TermEntry incomingAfterMerge = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_10, PROJECT_ID);

        Set<Term> germanTermsAfterMerge = existingAfterMerge.getLanguageTerms().get("de-DE");
        assertEquals(2, germanTermsAfterMerge.size());
        assertEquals(0, incomingAfterMerge.getLanguageTerms().size());
        assertTrue(germanTermsAfterMerge.contains(germanApprovedTerm));
        assertTrue(germanTermsAfterMerge.contains(germanBlacklistedTerm));

    }
}
