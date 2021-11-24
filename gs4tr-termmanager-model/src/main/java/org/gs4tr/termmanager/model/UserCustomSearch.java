package org.gs4tr.termmanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.gs4tr.foundation.modules.entities.model.Identifiable;

@NamedQueries({ @NamedQuery(name = "UserCustomSearch.findAllByUserProfileId", query = "select customSearch "
	+ "from UserCustomSearch customSearch "
	// + "left join fetch customSearch.userProfile "
	+ "where customSearch.userProfile.userProfileId = :userProfileId "
	+ "and customSearch.adminFolder = :adminFolders"),

	@NamedQuery(name = "UserCustomSearch.findByFolderAndUser", query = "select customSearch "
		+ "from UserCustomSearch customSearch "
		// + "left join fetch customSearch.userProfile "
		+ "where customSearch.userProfile.userProfileId = :userProfileId "
		+ "and customSearch.customFolder = :folder") })
@Entity
@Table(name = "USER_CUSTOM_SEARCH")
public class UserCustomSearch implements Identifiable<Long> {

    private static final long serialVersionUID = 8650474321665808863L;

    private Boolean _adminFolder;

    private String _customFolder;

    private Long _customSearchId;

    private String _originalFolder;

    private String _searchJsonData;

    private String _url;

    private TmUserProfile _userProfile;

    @Column(name = "ADMIN_FOLDER", nullable = false)
    public Boolean getAdminFolder() {
	return _adminFolder;
    }

    @Column(name = "CUSTOM_FOLDER", nullable = false)
    public String getCustomFolder() {
	return _customFolder;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOM_SEARCH_ID")
    public Long getCustomSearchId() {
	return _customSearchId;
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getCustomSearchId();
    }

    @Column(name = "ORIGINAL_FOLDER", nullable = false)
    public String getOriginalFolder() {
	return _originalFolder;
    }

    @Column(name = "SEARCH_JSON_DATA", nullable = false)
    @Lob
    public String getSearchJsonData() {
	return _searchJsonData;
    }

    @Column(name = "URL", nullable = false)
    public String getUrl() {
	return _url;
    }

    @ManyToOne
    @JoinColumn(name = "USER_PROFILE_ID")
    public TmUserProfile getUserProfile() {
	return _userProfile;
    }

    public void setAdminFolder(Boolean adminFolder) {
	_adminFolder = adminFolder;
    }

    public void setCustomFolder(String customFolder) {
	_customFolder = customFolder;
    }

    public void setCustomSearchId(Long customSearchId) {
	_customSearchId = customSearchId;
    }

    @Override
    public void setIdentifier(Long identifier) {
	setCustomSearchId(identifier);
    }

    public void setOriginalFolder(String originalFolder) {
	_originalFolder = originalFolder;
    }

    public void setSearchJsonData(String searchJsonData) {
	_searchJsonData = searchJsonData;
    }

    public void setUrl(String url) {
	_url = url;
    }

    public void setUserProfile(TmUserProfile userProfile) {
	_userProfile = userProfile;
    }
}
