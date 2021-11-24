package org.gs4tr.termmanager.service;

import org.gs4tr.termmanager.service.solr.restore.model.RecodeOrCloneCommand;

public interface RecodeTermsService {

    void recodeProjectLanguage(RecodeOrCloneCommand recodeOrCloneCommand);

    void recodeProjectLanguageDetail(RecodeOrCloneCommand recodeOrCloneCommand);

    void recodeProjectUserLanguage(RecodeOrCloneCommand recodeOrCloneCommand);

    void recodeSubmission(RecodeOrCloneCommand command);

    void recodeSubmissionLanguages(RecodeOrCloneCommand command);

    void recodeSubmissionTermEntriesHistories(RecodeOrCloneCommand command);

    void recodeSubmissionTerms(RecodeOrCloneCommand command);

    void recodeTermEntriesHistories(RecodeOrCloneCommand command);

    void recodeTerms(RecodeOrCloneCommand command);
}
