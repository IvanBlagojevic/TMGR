package groovy.service
import java.sql.Timestamp
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmOrganization;


date = new Date();
timesatmp = new Timestamp(date.getTime());

itemStatusType = builder.itemStatusType([name: "intranslationreview"])
priority = builder.entityStatusPriority([dateCompleted: null, priority: 2, priorityAssignee: 3,
    status: itemStatusType])

processed = builder.itemStatusType(name: "processed")

organizationInfo1 = builder.organizationInfo([name: "My First Organization", domain: "DOMAIN", enabled: false,
    theme: "THEME", currencyCode: null])
organizationInfo2 = builder.organizationInfo([name: "My Second Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])
organizationInfo3 = builder.organizationInfo([name: "Emisia", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])


projectDetail1 = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 15L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0L, languageCount: 5L, languageDetails: null, project: null, projectDetailId: 1L, termCount: 15L, termEntryCount: 3L, termInSubmissionCount: 0L, userDetails: null])

projectDetail2 = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 25L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0L, languageCount: 5L, project: null, projectDetailId: 2L, termCount: 25L, termEntryCount: 5L, termInSubmissionCount: 0L, userDetails: null])

projectInfo1 = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My First Project", shortCode: "MYF000001"])
projectInfo2 = builder.projectInfo([clientIdentifier: null, enabled: false, name: "My Second Project", shortCode: "MYS000002"])

tmOrganization1 = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo1, parentOrganization: null])

tmOrganization2 = builder.tmOrganization([organizationId: 2L, organizationInfo: organizationInfo2, parentOrganization: null])

tmOrganization3 = builder.tmOrganization([organizationId: 3L, organizationInfo: organizationInfo3, parentOrganization: null,
    projects: [] as Set<TmProject>])

tmProject1 = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: processed,
    metadata: null, organization: null, projectDetail: projectDetail1, projectId: 1L, projectInfo: projectInfo1,
    projectLanguages: null])

tmProject2 = builder.tmProject([addApprovedTerms: true, announcements: null, attributes: null, defaultTermStatus: processed,
    metadata: null, organization: tmOrganization1, projectDetail: projectDetail2, projectId: 2L, projectInfo: projectInfo2,
    projectLanguages: null])

projects1 = [tmProject2] as Set<TmProject>
projects2 = [tmProject1] as Set<TmProject>

tmOrganization1.setProjects(projects1);
tmOrganization2.setProjects(projects2);

projectIds = [1L, 2L] as Long[]

allOrganizations = [
    tmOrganization1,
    tmOrganization2] as List
