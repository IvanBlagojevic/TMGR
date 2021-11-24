package org.gs4tr.termmanager.service.utils;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.model.dto.EntityTypeEnum;
import org.gs4tr.termmanager.model.dto.RoleTypeEnum;
import org.gs4tr.termmanager.service.deserializers.EntityTypeEnumDeserializer;
import org.gs4tr.termmanager.service.deserializers.RoleTypeEnumDeserializer;
import org.gs4tr.termmanager.service.model.command.JsonCommand;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class JsonUtils {

    private static Log _logger = LogFactory.getLog(JsonUtils.class);

    private static final ObjectMapper OBJECT_MAPPER = initializeDeserializers();

    public static <T> Set<T> readSet(byte[] setAsBytes, Class<T> elementType) {
	try {
	    return OBJECT_MAPPER.readValue(setAsBytes,
		    TypeFactory.defaultInstance().constructCollectionType(Set.class, elementType));
	} catch (Exception e) {
	    _logger.error(String.format(Messages.getString("JsonUtils.4"), elementType));
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public static <T> T readValue(byte[] jsonAsBytes, Class<T> expectedType) {
	try {
	    return OBJECT_MAPPER.readValue(jsonAsBytes, expectedType);
	} catch (Exception e) {
	    _logger.error(Messages.getString("JsonUtils.0")); //$NON-NLS-1$
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public static <T> T readValue(JsonNode jsonNode, Class<T> expectedType) {
	try {
	    return OBJECT_MAPPER.readValue(jsonNode.traverse(), expectedType);
	} catch (Exception e) {
	    _logger.error(Messages.getString("JsonUtils.0")); //$NON-NLS-1$
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public static <T> T readValue(String jsonString, Class<T> expectedType) {
	try {
	    return OBJECT_MAPPER.readValue(jsonString, expectedType);
	} catch (Exception e) {
	    _logger.error(String.format(Messages.getString("JsonUtils.1"), //$NON-NLS-1$
		    jsonString));
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public static void updateExistingCommand(JsonCommand command, String jsonData) {
	try {
	    OBJECT_MAPPER.readerForUpdating(command).readValue(jsonData);
	} catch (Exception e) {
	    _logger.error(String.format(Messages.getString("JsonUtils.2"), jsonData)); //$NON-NLS-1$
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public static byte[] writeValueAsBytes(Object object) {
	try {
	    return OBJECT_MAPPER.writeValueAsBytes(object);
	} catch (Exception e) {
	    _logger.error(Messages.getString("JsonUtils.5")); //$NON-NLS-1$
	    throw new RuntimeException(e);
	}
    }

    public static String writeValueAsString(Object object) {
	try {
	    return OBJECT_MAPPER.writeValueAsString(object);
	} catch (Exception e) {
	    _logger.error(Messages.getString("JsonUtils.3")); //$NON-NLS-1$
	    throw new RuntimeException(e);
	}
    }

    private static ObjectMapper initializeDeserializers() {
	ObjectMapper objectMapper = new ObjectMapper();

	objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, true);
	objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@SuppressWarnings("deprecation")
	SimpleModule module = new SimpleModule("term module", new Version(1, 9, 7, null));

	module.addDeserializer(RoleTypeEnum.class, new RoleTypeEnumDeserializer());
	module.addDeserializer(EntityTypeEnum.class, new EntityTypeEnumDeserializer());

	objectMapper.registerModule(module);

	return objectMapper;
    }

}
