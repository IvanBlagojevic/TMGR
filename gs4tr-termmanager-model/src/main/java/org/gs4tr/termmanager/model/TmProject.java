package org.gs4tr.termmanager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.Announcement;
import org.gs4tr.foundation.modules.entities.model.OrganizationHolder;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation.modules.project.model.BaseProject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@NamedQueries({
	@NamedQuery(name = "Project.findProjectById", query = "select project " + "from TmProject project "
		+ "where project.projectId = :entityId"),

	@NamedQuery(name = "Project.findProjectByShortCode", query = "select project " + "from TmProject project "
		+ "left join fetch project.organization as organization "
		+ "where project.projectInfo.shortCode = :shortCode"),

	@NamedQuery(name = "Project.findProjectIdsByShortCodes", query = "select project.projectId "
		+ "from TmProject project " + "where project.projectInfo.shortCode in (:shortCodes)"),

	@NamedQuery(name = "Project.findProjectByName", query = "select project " + "from TmProject project "
		+ "left join fetch project.projectLanguages as projectLanguages "
		+ "left join fetch project.attributes " + "where project.projectInfo.name = :name"),

	@NamedQuery(name = "Project.countAllProjects", query = "select count(project.projectId) "
		+ "from TmProject project "),

	@NamedQuery(name = "Project.findProjectLanguagesByProjectId", query = "select projectLanguage "
		+ "from ProjectLanguage projectLanguage " + "where projectLanguage.project.projectId = :projectId"),

	@NamedQuery(name = "Project.findAllEnabledProjectIds", query = "select project.projectId "
		+ "from TmProject project " + "where project.projectInfo.enabled = true"),

	@NamedQuery(name = "Project.findAllEnabledProjects", query = "select project from TmProject project "
		+ "where project.projectInfo.enabled = true"),

	@NamedQuery(name = "Project.findAllDisabledProjectIds", query = "select p.projectId from TmProject p "
		+ "where p.projectInfo.enabled = false"),

	@NamedQuery(name = "Project.findProjectsByNameLike", query = "select p.projectId from TmProject p "
		+ "where lower(p.projectInfo.name) like lower(:name)"),

	@NamedQuery(name = "Project.findProjectsByIds", query = "select project " + "from TmProject project "
		+ "where project.projectId in (:projectIds)"),

	@NamedQuery(name = "Project.getOrganizationIdByProject", query = "select project.organization.organizationId "
		+ "from TmProject project " + "where project.projectId = :projectId"),

	@NamedQuery(name = "TmProject.getAttributesByProjectId", query = "select new Map(attribute.name as attributeName, "
		+ "attribute.attributeLevel as attributeLevel, " + "attribute.synchronizable as synchronizable, "
		+ "attribute.baseTypeEnum as baseType, attribute.comboValues as comboValues) "
		+ "from TmProject project " + "left join project.attributes attribute "
		+ "where project.projectId = :projectId"),

	@NamedQuery(name = "Project.findDisabledProjectIds", query = "select project.projectId "
		+ "from TmProject project " + "where project.projectId in (:projectIds) "
		+ "and project.projectInfo.enabled is false"),

	@NamedQuery(name = "TmProject.findAttributesByProjectIds", query = "select new Map(project.projectId as projectId, "
		+ "attribute.name as attributeName, attribute.termEntryAttributeType as termEntryAttributeType, "
		+ "attribute.attributeLevel as attributeLevel, attribute.required as required, attribute.value as value, "
		+ "attribute.multiplicity as multiplicity, attribute.comboValues as comboValues, attribute.readOnly as readOnly, "
		+ "attribute.inputFieldTypeEnum as inputFieldTypeEnum, attribute.baseTypeEnum as baseTypeEnum, "
		+ "attribute.synchronizable as synchronizable) " + "from TmProject project "
		+ "left join project.attributes attribute " + "where project.projectInfo.enabled is true "
		+ "and project.projectId in (:projectIds)") })
@Entity
@Table(name = "PROJECT")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TmProject extends BaseProject implements OrganizationHolder<TmOrganization> {

    private static final long serialVersionUID = 711087799101361203L;

    private Boolean _addApprovedTerms = Boolean.FALSE;

    private List<Attribute> _attributes;

    private Boolean _availableDescription = Boolean.FALSE;

    private ItemStatusType _defaultTermStatus;

    private TmOrganization _organization;

    private ProjectDetail _projectDetail;

    private Set<ProjectLanguage> _projectLanguages;

    private Boolean _sharePendingTerms = Boolean.TRUE;

    @Column(name = "ADD_APPROVED_TERMS", nullable = false)
    public Boolean getAddApprovedTerms() {
	return _addApprovedTerms;
    }

    @Override
    @Transient
    public Set<Announcement> getAnnouncements() {
	return super.getAnnouncements();
    }

    @ElementCollection
    @CollectionTable(name = "PROJECT_ATTRIBUTE", joinColumns = @JoinColumn(name = "PROJECT_ID"))
    @Cascade(value = org.hibernate.annotations.CascadeType.ALL)
    @Embedded
    @OrderColumn(name = "IDX")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<Attribute> getAttributes() {
	return _attributes;
    }

    @Column(name = "AVAILABLE_DESCRIPTION", nullable = false)
    public Boolean getAvailableDescription() {
	return _availableDescription;
    }

    @Column(name = "STATUS", length = 20)
    @Type(type = "org.gs4tr.foundation.modules.dao.hibernate.usertype.ItemStatusTypeUserType", parameters = @Parameter(name = "typeInstanceClassName", value = "org.gs4tr.foundation.modules.entities.model.types.ItemStatusType"))
    public ItemStatusType getDefaultTermStatus() {
	return _defaultTermStatus;
    }

    @Transient
    public List<Attribute> getDescriptions() {
	List<Attribute> attributes = getAttributes();
	if (CollectionUtils.isEmpty(attributes)) {
	    return null;
	}
	List<Attribute> descriptions = new ArrayList<Attribute>();
	for (Attribute attribute : attributes) {
	    if (BaseTypeEnum.DESCRIPTION == attribute.getBaseTypeEnum()) {
		descriptions.add(attribute);
	    }
	}
	return descriptions;
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getProjectId();
    }

    @Transient
    public List<Attribute> getNotes() {
	List<Attribute> attributes = getAttributes();
	if (CollectionUtils.isEmpty(attributes)) {
	    return null;
	}
	List<Attribute> notes = new ArrayList<Attribute>();
	for (Attribute attribute : attributes) {
	    if (BaseTypeEnum.NOTE == attribute.getBaseTypeEnum()) {
		notes.add(attribute);
	    }
	}
	return notes;
    }

    @Override
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    public TmOrganization getOrganization() {
	return _organization;
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.ALL)
    public ProjectDetail getProjectDetail() {
	return _projectDetail;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.ALL)
    public Set<ProjectLanguage> getProjectLanguages() {
	return _projectLanguages;
    }

    @Column(name = "SHARE_PENDING_TERMS", nullable = false)
    public Boolean getSharePendingTerms() {
	return _sharePendingTerms;
    }

    public void setAddApprovedTerms(Boolean addApprovedTerms) {
	_addApprovedTerms = addApprovedTerms;
    }

    public void setAttributes(List<Attribute> attributes) {
	_attributes = attributes;
    }

    public void setAvailableDescription(Boolean availableDescription) {
	_availableDescription = availableDescription;
    }

    public void setDefaultTermStatus(ItemStatusType defaultTermStatus) {
	_defaultTermStatus = defaultTermStatus;
    }

    @Override
    public void setIdentifier(Long identifier) {
	setProjectId(identifier);
    }

    @Override
    public void setOrganization(TmOrganization organization) {
	_organization = organization;
    }

    public void setProjectDetail(ProjectDetail projectDetail) {
	_projectDetail = projectDetail;
    }

    public void setProjectLanguages(Set<ProjectLanguage> projectLanguages) {
	_projectLanguages = projectLanguages;
    }

    public void setSharePendingTerms(Boolean sharePendingTerms) {
	_sharePendingTerms = sharePendingTerms;
    }
}
