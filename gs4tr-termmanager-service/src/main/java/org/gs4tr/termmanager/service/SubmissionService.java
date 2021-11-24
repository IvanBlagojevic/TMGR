package org.gs4tr.termmanager.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.SubmissionUser;
import org.gs4tr.termmanager.model.TermEntryTranslationUnit;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.SubmissionDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.notification.NotificationData;
import org.springframework.security.access.annotation.Secured;

public interface SubmissionService {

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    void addComments(String commentText, Long submissionId, List<String> termIds, String languageId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Set<String> cancelSubmission(Long submissionId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    NotificationData collectNotificationData(Long submissionId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Set<String> commitTranslationChanges(Long submissionId, String languageId, List<String> subTermIds);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Submission createSubmission(Long projectId, List<TermEntryTranslationUnit> translationUnits, String submissionName,
	    String submissionMarkerId, String sourceLanguage, boolean reviewIsRequired, Long submissionId,
	    List<Term> sourceTerms);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    boolean exists(String submissionName);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Submission findByIdFetchChilds(Long submissionId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<Submission> findByIds(List<Long> submissionIds);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Long findBySubmissionAndUserId(Long submissionId, Long userProfileId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Map<String, List<TermEntry>> findHistoriesByIds(Collection<String> termEntryIds);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<TermEntry> findHistoryById(String termEntryId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<TmProject> findProjectsBySubmissionIds(List<Long> submissionIds);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    TermEntry findRegularTermEntryById(String termEntryId, Long projectId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Submission findSubmissionByIdFetchChilds(Long submissionId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<SubmissionLanguage> findSubmissionLanguages(Long submissionId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    TermEntry findSubmissionTermEntryById(String termId, Long projectId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Map<String, Set<String>> findSubmissionUsers(List<Long> submissionIds);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<Submission> findSubmissionsByIdsFetchChilds(List<Long> submissionIds);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<Submission> findSubmissionsByProjectId(Long projectId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<Term> findTermsBySubmissionId(Long submissionId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<Submission> findUserSubmissionsByProjectIds(Collection<Long> projectIds);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Submission load(Long submissionId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    void notifyListeners(TmProject project, ProjectDetailInfo projectDetailInfo, TermEntry termEntry,
	    TermEntry submissionTermEntry, SubmissionDetailInfo submissionDetailInfo, Submission submission,
	    List<UpdateCommand> updateCommands, Boolean reviewRequired);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Submission reSubmitTerms(Long submissionId, List<String> submissionTermIds);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    void saveOrUpdateSubmissionLanguage(SubmissionLanguage submissionLanguage);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Submission saveSubmission(Submission submission);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    SubmissionUser saveSubmissionUser(SubmissionUser submissionUser);

    void updateLanguageByProjectId(String languageFrom, String languageTo, Long projectId);

    void updateSubmissionLanguageByProjectId(String languageFrom, String languageTo, Long projectId);
}
