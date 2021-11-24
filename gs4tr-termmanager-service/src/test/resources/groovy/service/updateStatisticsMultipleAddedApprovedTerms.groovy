package groovy.service

import org.gs4tr.termmanager.model.ItemStatusTypeHolder
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum
import org.gs4tr.termmanager.model.event.EventMessage
import org.gs4tr.termmanager.model.event.ProjectDetailInfo
import org.gs4tr.termmanager.model.event.StatisticsInfo

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKYPE000001"])

tmProject = builder.tmProject([projectId: 1L, projectInfo: projectInfo])

detailInfo = new ProjectDetailInfo(1L)

processed = ItemStatusTypeHolder.PROCESSED.getName()

entry = builder.termEntry([uuId: UUID.randomUUID().toString(), projectId: 1L, shortCode: "SKYPE000001", projectName: "Skype",
    userCreated: "super", userModified: "super"])

command = builder.updateCommand([command: CommandEnum.ADD.value(), markerId: UUID.randomUUID().toString(),
                                 value  : "source term", languageId: "en-US", status: processed, projectId: 1L])
command.setItemType("term")

userLanguage = builder.projectUserLanguage([language: "en-US", project: tmProject, projectUserLanguageId: 1L]);

statisticsInfoEN = builder.statisticsInfo(new StatisticsInfo(1L, "en-US"));
statisticsInfoDE = builder.statisticsInfo(new StatisticsInfo(1L, "de-DE"));

statistics = [statisticsInfoEN, statisticsInfoDE] as Set

message = EventMessage.createEvent(EventMessage.EVENT_UPDATE_TERMENTRY)

message.addContextVariable(EventMessage.VARIABLE_PROJECT, tmProject);
message.addContextVariable(EventMessage.VARIABLE_DETAIL_INFO, detailInfo);
message.addContextVariable(EventMessage.VARIABLE_PROJECT_ID, 1L);
message.addContextVariable(EventMessage.VARIABLE_COMMAND, command);
message.addContextVariable(EventMessage.VARIABLE_TERM_ENTRY, entry);
message.addContextVariable(EventMessage.VARIABLE_STATUS_TYPE, processed);
message.addContextVariable(EventMessage.VARIABLE_USER_LATEST_CHANGE, Boolean.TRUE);

message.addContextVariable(EventMessage.VARIABLE_STATISTICS, statistics);

userLanguages = [userLanguage] as List
