package org.gs4tr.termmanager.service.deserializers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.gs4tr.termmanager.model.dto.EntityTypeEnum;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class EntityTypeEnumDeserializer extends JsonDeserializer<EntityTypeEnum> {

    private static Map<String, EntityTypeEnum> _entityTypeMapConverter = new HashMap<String, EntityTypeEnum>();
    private static final String ORGANIZATION_NAME = "ORGANIZATION";
    private static final String PROJECT_NAME = "PROJECT";
    private static final String ROLE_NAME = "ROLE";
    private static final String TERM_NAME = "TERM";
    private static final String TERMENTRY_NAME = "TERMENTRY";

    private static final String USER_NAME = "USER";

    static {
	_entityTypeMapConverter.put(ORGANIZATION_NAME, new org.gs4tr.termmanager.model.dto.EntityTypeEnum(0));
	_entityTypeMapConverter.put(PROJECT_NAME, new org.gs4tr.termmanager.model.dto.EntityTypeEnum(1));
	_entityTypeMapConverter.put(ROLE_NAME, new org.gs4tr.termmanager.model.dto.EntityTypeEnum(2));
	_entityTypeMapConverter.put(TERM_NAME, new org.gs4tr.termmanager.model.dto.EntityTypeEnum(3));
	_entityTypeMapConverter.put(TERMENTRY_NAME, new org.gs4tr.termmanager.model.dto.EntityTypeEnum(4));
	_entityTypeMapConverter.put(USER_NAME, new org.gs4tr.termmanager.model.dto.EntityTypeEnum(5));
    }

    @Override
    public EntityTypeEnum deserialize(JsonParser jp, DeserializationContext context)
	    throws IOException {
	ObjectCodec codec = jp.getCodec();
	JsonNode node = codec.readTree(jp);

	EntityTypeEnum result = _entityTypeMapConverter.get(node.asText());
	return result;
    }
}
