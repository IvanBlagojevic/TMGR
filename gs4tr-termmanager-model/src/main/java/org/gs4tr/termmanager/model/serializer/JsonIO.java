package org.gs4tr.termmanager.model.serializer;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class JsonIO {

    private static final Log LOG = LogFactory.getLog(JsonIO.class);

    private static final ObjectMapper OM = new ObjectMapper();

    public static <T> List<T> readList(byte[] listAsBytes, Class<T> elementType) {
	if (Objects.isNull(listAsBytes)) {
	    return null;
	}
	try {
	    LogHelper.trace(LOG, "Converting bytes to list...");
	    return OM.readValue(listAsBytes,
		    TypeFactory.defaultInstance().constructCollectionType(List.class, elementType));
	} catch (Exception e) {
	    throw new JsonException(e.getMessage(), e);
	}
    }

    public static <T> T readValue(byte[] jsonAsBytes, Class<T> expectedType) {
	if (Objects.isNull(jsonAsBytes)) {
	    return null;
	}
	try {
	    LogHelper.trace(LOG, "Converting bytes to object...");
	    return OM.readValue(jsonAsBytes, expectedType);
	} catch (Exception e) {
	    throw new JsonException(e.getMessage(), e);
	}
    }

    public static <T> T readValue(InputStream jsonAsStream, Class<T> expectedType) {
	if (Objects.isNull(jsonAsStream)) {
	    return null;
	}
	try {
	    LOG.trace("Converting stream to object...");
	    return OM.readValue(jsonAsStream, expectedType);
	} catch (Exception e) {
	    throw new JsonException(e.getMessage(), e);
	}
    }

    public static <T> T readValue(String jsonString, Class<T> expectedType) {
	if (Objects.isNull(jsonString)) {
	    return null;
	}
	try {
	    LOG.trace("Converting string to object...");
	    return OM.readValue(jsonString, expectedType);
	} catch (Exception e) {
	    throw new JsonException(e.getMessage(), e);
	}
    }

    public static byte[] writeValueAsBytes(Object object) {
	try {
	    LOG.trace("Converting object to string...");
	    return OM.writeValueAsBytes(object);
	} catch (Exception e) {
	    throw new JsonException(e.getMessage(), e);
	}
    }

    public static String writeValueAsString(Object object) {
	try {
	    LOG.trace("Converting object to string...");
	    return OM.writeValueAsString(object);
	} catch (Exception e) {
	    throw new JsonException(e.getMessage(), e);
	}
    }

    static {
	OM.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    }
}
