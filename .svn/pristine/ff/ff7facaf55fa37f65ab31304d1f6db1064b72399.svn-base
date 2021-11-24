package groovy.taskController

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.spring.utils.SpringProfileUtils;
import org.gs4tr.termmanager.model.dto.converter.AttributeConverter;
import org.gs4tr.termmanager.model.dto.converter.UserInfoConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandlerUtils;
import org.gs4tr.termmanager.service.utils.TaskHandlerConstants;

ticket=builder.ticket(ticketId:"4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q")
parentTickets=[ticket] as Ticket[]

parentIds=[1L] as Long[]

jsonData= "{\n  \"taskName\": \"import tbx\",\n  \"parentTickets\": []\n}"

taskCommand= builder.taskCommand([parentTickets: parentTickets, taskName: "edit user", jsonTaskData: jsonData])

taskModel= new TaskModel();
taskModel.addObject("ssoEnabled", false);
taskModel.addObject("generic", false);
taskModel.addObject("allowPasswordChange", null);
taskModel.addObject("password", StringUtils.EMPTY);


