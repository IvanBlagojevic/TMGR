package org.gs4tr.termmanager.service.deserializers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.gs4tr.termmanager.model.dto.RoleTypeEnum;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class RoleTypeEnumDeserializer extends JsonDeserializer<RoleTypeEnum> {

    private static Map<String, RoleTypeEnum> _roleTypeMapConverter = new HashMap<>();
    private static final String CONTEXT_ROLE = "PROJECT";

    private static final String SYSTEM_ROLE = "SYSTEM";

    static {
	_roleTypeMapConverter.put(CONTEXT_ROLE, new org.gs4tr.termmanager.model.dto.RoleTypeEnum(0));
	_roleTypeMapConverter.put(SYSTEM_ROLE, new org.gs4tr.termmanager.model.dto.RoleTypeEnum(1));
    }

    @Override
    public RoleTypeEnum deserialize(JsonParser jp, DeserializationContext context)
	    throws IOException {
	ObjectCodec codec = jp.getCodec();
	JsonNode node = codec.readTree(jp);

	return _roleTypeMapConverter.get(node.asText());
    }
}
