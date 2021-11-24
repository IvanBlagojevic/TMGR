package org.gs4tr.termmanager.service;

import org.gs4tr.termmanager.model.event.SubmissionDetailInfo;
import org.springframework.security.access.annotation.Secured;

public interface SubmissionDetailService {

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void updateSubmissionDetail(SubmissionDetailInfo detailInfo);
}
