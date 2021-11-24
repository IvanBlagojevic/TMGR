package groovy.projectLanguageDetailSearchController;

import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.view.ProjectLanguageDetailView;

import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.LanguageAlignmentEnum;

import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum
import org.gs4tr.foundation.modules.entities.model.Task
import org.gs4tr.foundation.modules.entities.model.TaskPagedList
import org.gs4tr.foundation.modules.entities.model.TaskPriority


dateModified = new Date().getTime();

english = Language.valueOf("en-US");
germany = Language.valueOf("de-DE");
french = Language.valueOf("fr-FR");

englishView = builder.projectLanguageDetailView([language: english, dateModified: dateModified, projectLanguageDetailViewId: 1L])
germanyView = builder.projectLanguageDetailView([language: germany, dateModified: dateModified, projectLanguageDetailViewId: 2L])
frenchView = builder.projectLanguageDetailView([language: french, dateModified: dateModified, projectLanguageDetailViewId: 3L])

entities = [
    englishView,
    germanyView,
    frenchView] as ProjectLanguageDetailView[]

pagedListInfo = builder.pagedListInfo([index: 0, indexesSize: 5, size: 1000, sortDirection: "ASCENDING",
    sortProperty: null])

pagedList = builder.pagedList([elements: entities, pagedListInfo: pagedListInfo, totalCount: 3])

taskPagedList= new TaskPagedList<TmProject>(pagedList)
taskPagedList.setTasks([] as Task[])