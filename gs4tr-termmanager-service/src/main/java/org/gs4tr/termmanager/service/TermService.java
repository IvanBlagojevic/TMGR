package org.gs4tr.termmanager.service;

import java.util.Collection;
import java.util.List;

import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.glossary.Term;
import org.springframework.security.access.annotation.Secured;

public interface TermService {

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void approveTerms(List<String> termIds, Long projectId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    void changeForbiddStatus(List<String> termIds, Long projectId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void deleteTermDescriptionsByType(List<String> type, Long projectId, String baseType, List<String> languageIds);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void demoteTerms(List<String> termIds, Long projectId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    Term findTermById(String termId, Long projectId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<Term> findTermsByIds(Collection<String> termIds, List<Long> projectIds);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<Term> getAllTermsForMerge(Long projectId, String languageId, List<String> termNames);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<Term> getAllTermsInTermEntry(String termEntryId, Long projectId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<Term> getTermsByTermEntryIds(Collection<String> termEntryIds, List<Long> projectIds);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void renameTermDescriptions(Long projectId, String baseType, List<Attribute> attributes, List<String> languageIds);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void updateUserLatestChangedTerms(Long userId, List<Long> projectIds);
}
