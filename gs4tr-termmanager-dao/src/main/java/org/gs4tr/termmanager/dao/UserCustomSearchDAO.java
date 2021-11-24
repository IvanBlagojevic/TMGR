package org.gs4tr.termmanager.dao;

import java.util.List;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.termmanager.model.UserCustomSearch;

public interface UserCustomSearchDAO extends GenericDao<UserCustomSearch, Long> {

    List<UserCustomSearch> findByUserProfileId(Long userProfileId, boolean adminFolders);

    UserCustomSearch findByUserProfileIdAndFolderName(Long userProfileId, String folderName);
}
