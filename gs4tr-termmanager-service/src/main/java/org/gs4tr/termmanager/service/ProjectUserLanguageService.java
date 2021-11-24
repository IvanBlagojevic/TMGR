package org.gs4tr.termmanager.service;

import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;

public interface ProjectUserLanguageService {

    void cloneProjectUserLanguages(TmUserProfile user, TmProject project, String languageTo);

    void recodeProjectUserLanguage(Long projectId, String languageFrom, String languageTo);
}
