package org.gs4tr.termmanager.model.event;

import java.util.HashMap;
import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.NotifyingMessage;

public class EventMessage implements NotifyingMessage {

    public static final String EVENT_ADD_COMMENT = "add-comment";

    public static final String EVENT_UPDATE_TERMENTRY = "update-termEntry";

    public static final String VARIABLE_COMMAND = "command";

    public static final String VARIABLE_DETAIL_INFO = "projectDetailInfo";

    public static final String VARIABLE_PROJECT = "project";

    public static final String VARIABLE_PROJECT_ID = "projectId";

    public static final String VARIABLE_REMOTE_USER = "user";

    public static final String VARIABLE_REVIEW_REQUIRED = "reviewRequired";

    public static final String VARIABLE_STATISTICS = "statistics";

    public static final String VARIABLE_STATUS_TYPE = "termStatusType";

    public static final String VARIABLE_SUBMISSION = "submission";

    public static final String VARIABLE_SUBMISSION_INFO = "submissionDetailInfo";

    public static final String VARIABLE_SUBMISSION_TERM_ENTRY = "submissionTermEntry";

    public static final String VARIABLE_TERM_ENTRY = "termEntry";

    public static final String VARIABLE_USER_LATEST_CHANGE = "userLatestChange";

    public static EventMessage createEvent(String messageId) {
	return new EventMessage(messageId);
    }

    private final Map<String, Object> _messageContext;

    private final String _messageId;

    public EventMessage(String messageId) {
	_messageId = messageId;
	_messageContext = new HashMap<>();
    }

    public void addContextVariable(String key, Object value) {
	getMessageContext().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getContextVariable(String key) {
	return (T) getMessageContext().get(key);
    }

    @Override
    public String getNotifyingMessageId() {
	return _messageId;
    }

    private Map<String, Object> getMessageContext() {
	return _messageContext;
    }
}
