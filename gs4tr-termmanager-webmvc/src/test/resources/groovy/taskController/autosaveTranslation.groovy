package groovy.taskController

import org.gs4tr.foundation.modules.entities.model.Ticket;

ticket=builder.ticket(ticketId:"4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q")
parentTickets=[ticket] as Ticket[]

jsonData = "{\"taskName\":\"auto save translation\"}\n"

jsonTaskData = "{\"autoSaveCommands\":[{\"entityTicket\":\"5efe4d49-d679-465d-a290-d467c200b903\",\"term\":true,\"text\":\"ready for review now\"}]}"

taskCommand= builder.taskCommand([parentTickets: parentTickets, taskName: "auto save translation", jsonTaskData: jsonData,
    jsonTaskData: jsonTaskData])


