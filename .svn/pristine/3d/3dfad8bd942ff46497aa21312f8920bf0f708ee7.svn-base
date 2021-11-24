package groovy.organizationSearchController;

import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.model.TmOrganization
import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum
import org.gs4tr.foundation.modules.entities.model.Task
import org.gs4tr.foundation.modules.entities.model.TaskPagedList
import org.gs4tr.foundation.modules.entities.model.TaskPriority

pagedListInfo = builder.pagedListInfo([index: 0, indexesSize: 5, size: 50, sortDirection: "ASCENDING",
    sortProperty: "entity.organizationInfo.name"])

organizationInfo = builder.organizationInfo([name: "Emisia", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

elements = [tmOrganization] as TmOrganization[]

pagedList = builder.pagedList([elements: elements, pagedListInfo: pagedListInfo, totalCount: 1])

addOrganization = builder.task([name: "add organization", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_FIVE])

editOrganization = builder.task([name: "edit organization", selectStyle: SelectStyleEnum.SINGLE_SELECT, priority: TaskPriority.LEVEL_FIVE])

assignOrganizationProject = builder.task([name: "assign organization project", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_FIVE])

getOrganizationUsers = builder.task([name: "get organization users", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_FIVE])

enableOrganization = builder.task([name: "enable organization", selectStyle: SelectStyleEnum.SINGLE_SELECT, priority: TaskPriority.LEVEL_FIVE])

tasks=[
    assignOrganizationProject,
    addOrganization,
    editOrganization,
    getOrganizationUsers,
    enableOrganization] as Task[]

taskPagedList= new TaskPagedList<TmOrganization>(pagedList)
taskPagedList.setTasks(tasks)