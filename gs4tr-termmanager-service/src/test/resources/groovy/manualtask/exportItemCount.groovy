import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.termmanager.model.view.ProjectDetailView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.event.DetailState;
import org.gs4tr.termmanager.service.model.command.ForbidTermCommand;


termNameAndSearchType = builder.termNameAndSearchType([value1: "term", value2:"DEFAULT"])

entityType = builder.entityType([value1: ["TERM", "ATTRIBUTES"] as List, value2:["SOURCE", "TARGET"] as List])

exportSearchFilter = builder.exportSearchFilter([termNameAndSearchType: termNameAndSearchType, entityType: entityType,
    ascending: true, sortProperty:"someField"])

exportCommand = builder.exportCommand([projectId: 1L, exportFormat: "xlsx", sourceLocale: "en-US",
    targetLocales: ["de-DE"] as List, searchFilter: exportSearchFilter])

counts = [1L : 500L] as Map


