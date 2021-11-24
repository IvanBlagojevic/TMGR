package org.gs4tr.termmanager.service.listeners;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.utils.StatisticsUtils;
import org.springframework.stereotype.Component;

@Component("updateStatisticsEventListener")
public class UpdateStatisticsEventListener implements NotifyingMessageListener<EventMessage> {

    private static final EnumSet<TypeEnum> TYPE_ENUMS = EnumSet.of(TypeEnum.NOTE, TypeEnum.DESCRIP);

    @Override
    public Class<EventMessage> getNotifyingMessageClass() {
	return EventMessage.class;
    }

    @Override
    public void notify(EventMessage message) {

	Long projectId = message.getContextVariable(EventMessage.VARIABLE_PROJECT_ID);

	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	TermEntry termEntry = message.getContextVariable(EventMessage.VARIABLE_TERM_ENTRY);

	String status = resolveStatus(message, command);

	CommandEnum commandEnum = resolveCommand(command);

	Set<StatisticsInfo> statistics = message.getContextVariable(EventMessage.VARIABLE_STATISTICS);

	updateStatistics(projectId, status, commandEnum, statistics, termEntry, command);
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    CommandEnum commandEnum = command.getCommandEnum();
	    TypeEnum typeEnum = command.getTypeEnum();
	    if (TypeEnum.TERM == typeEnum && (CommandEnum.ADD == commandEnum || CommandEnum.UPDATE == commandEnum
		    || CommandEnum.REMOVE == commandEnum)) {
		supports = true;
	    } else if ((TypeEnum.DESCRIP == typeEnum || TypeEnum.NOTE == typeEnum)
		    && StringUtils.isNotBlank(command.getLanguageId()) && (CommandEnum.UPDATE == commandEnum
			    || CommandEnum.REMOVE == commandEnum || CommandEnum.ADD == commandEnum)) {
		supports = true;
	    }
	}

	return supports;
    }

    private Optional<StatisticsInfo> findStatisticInfo(Long projectId, String languageId,
	    Set<StatisticsInfo> statistics) {
	return statistics.stream().filter(statisticsInfo -> statisticsInfo.getProjectId().equals(projectId)
		&& statisticsInfo.getLanguageId().equals(languageId)).findFirst();
    }

    private void incrementStatistic(StatisticsInfo statistic, String status, CommandEnum commandEnum,
	    TermEntry termEntry, UpdateCommand command) {

	switch (commandEnum) {
	case ADD:
	    StatisticsUtils.incrementAddedStatisticByStatus(statistic, status);
	    break;
	case UPDATE:

	    /*
	     * TODO: Decide by term name and term status. If both are changed increment both
	     */

	    if (Objects.isNull(command.getMarkerId())) {
		break;
	    }

	    Term updatedTerm = termEntry.ggetTermById(command.getMarkerId());

	    if (Objects.isNull(updatedTerm)) {
		break;
	    }

	    if (!StringUtils.equals(command.getStatus(), command.getStatusOld())) {
		StatisticsUtils.incrementStatisticByStatus(statistic, status);
	    }

	    if (!StringUtils.equals(command.getValue(), command.getOldValue())) {
		statistic.incrementUpdatedCount();
	    }
	    break;
	case REMOVE:
	    statistic.incrementDeletedCount();
	    break;
	default:
	    break;
	}

    }

    private CommandEnum resolveCommand(UpdateCommand command) {
	return TYPE_ENUMS.contains(command.getTypeEnum()) ? CommandEnum.UPDATE : command.getCommandEnum();
    }

    private String resolveStatus(EventMessage message, UpdateCommand command) {
	return StringUtils.isBlank(command.getStatus()) ? message.getContextVariable(EventMessage.VARIABLE_STATUS_TYPE)
		: command.getStatus();
    }

    private void updateStatistics(Long projectId, String status, CommandEnum commandEnum,
	    Set<StatisticsInfo> statistics, TermEntry termEntry, UpdateCommand command) {
	Optional<StatisticsInfo> optional = findStatisticInfo(projectId, command.getLanguageId(), statistics);
	if (optional.isPresent()) {
	    incrementStatistic(optional.get(), status, commandEnum, termEntry, command);
	}
    }
}
