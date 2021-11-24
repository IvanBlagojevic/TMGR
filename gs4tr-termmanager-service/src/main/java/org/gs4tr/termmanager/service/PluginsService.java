package org.gs4tr.termmanager.service;

import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.springframework.security.access.annotation.Secured;

public interface PluginsService {

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    ManualTaskHandler getUserTaskHandler(String taskName);

}
