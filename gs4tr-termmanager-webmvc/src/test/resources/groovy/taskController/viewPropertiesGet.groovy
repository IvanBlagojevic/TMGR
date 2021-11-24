package groovy.taskController

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.TaskModel;

date = new Date();

item = builder.historyItem([bold : true, date: date.getTime(), fieldName: "TERM", isRTL: false, user: "sdulin",
    newStatus: ItemStatusTypeHolder.PROCESSED.getName(), newValue: "new source term", oldStatus: StringUtils.EMPTY,
    path: "English"])

history = [item] as List

model=new HashMap<String, Object>()
model.put("sourceIsRTL", Boolean.FALSE)
model.put("sourceLanguage", "English")
model.put("history", history)
model.put("sourceNotes", [])
model.put("allAttributes", [])
model.put("allNotes", [])
model.put("sourceDescriptions", [])

taskModel= new TaskModel();
taskModel.addAllObjects(model);

taskModels=[taskModel]as TaskModel[]


