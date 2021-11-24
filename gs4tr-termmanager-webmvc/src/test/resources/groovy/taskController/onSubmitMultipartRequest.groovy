package groovy.taskController

import org.gs4tr.foundation.modules.entities.model.Ticket
import org.gs4tr.termmanager.model.TaskResponse

jsonData= "{\"taskName\":\"assign termentry attributes\"}\n"

jsonTaskData = "{\"updateCommands\":[],\"resourceIdsForRemoval\":[],\"termEntryTicket\":\"6d81509b-8561-4af1-82d2-aefc15d1215b\",\"attributeTypeFileCommands\":[{\"name\":\"filefield-3041-inputEl\",\"attributeType\":\"Multimedia\"}],\"fileEditCommands\":[]}\n"

taskCommand = builder.taskCommand(jsonData:jsonData, jsonTaskData: jsonTaskData, taskName:"assign termentry attributes")

inputStream =  new ByteArrayInputStream("15-beach-sea-photography.jpg".getBytes())

resourceInfo = builder.resourceInfo([mimeType: "image/jpeg", name: "15-beach-sea-photography.jpg", size: 378688])

repositoryItem = builder.repositoryItem([resourceInfo: resourceInfo, inputStream: inputStream])

ticket = builder.ticket([ticketId:"4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q"])

taskResponse= new TaskResponse(ticket)


