package org.gs4tr.termmanager.webmvc.json.validator;

import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This class provide options to validate JSON response content.
 *
 */
public class JSONValidator {

    private ArrayNode _arrayNode;

    private JsonNode _node;

    private JSONValidator _parentValidator;

    public JSONValidator(String json) {
	ObjectMapper mapper = new ObjectMapper();

	try {
	    JsonNode node = mapper.readTree(json);
	    if (node instanceof ArrayNode) {
		_arrayNode = (ArrayNode) node;
	    } else {
		_node = node;
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private JSONValidator(JSONValidator parentValidator, JsonNode jsonNode) {
	_parentValidator = parentValidator;

	if (jsonNode instanceof ArrayNode) {
	    _arrayNode = (ArrayNode) jsonNode;
	} else {
	    _node = jsonNode;
	}
    }

    public JSONValidator assertArrayFinish() {
	if (_arrayNode.size() > 0) {
	    throw new AssertionError(String.format("Json hasn't been validated fully: %s", _arrayNode.toString()));
	}

	if (_parentValidator != null) {
	    _parentValidator.removeNode(_arrayNode);
	}

	return _parentValidator;
    }

    public JSONValidator assertObjectFinish() {
	if (_node != null && _node.size() > 0) {
	    throw new AssertionError(String.format("Json hasn't been validated fully: %s", _node.toString())); //$NON-NLS-1$
	}
	if (_arrayNode != null && _arrayNode.size() > 0) {
	    throw new AssertionError(String.format("Json hasn't been validated fully: %s", //$NON-NLS-1$
		    _arrayNode.toString()));
	}

	if (_parentValidator != null) {
	    _parentValidator.removeNode(_node);
	}

	return _parentValidator;
    }

    public JSONValidator assertProperty(String name, String value) {
	JsonNode node = _node.get(name);
	if (node == null) {
	    throw new AssertionError(String.format("Couldn't find key with value: '%s' within json: %s", name, //$NON-NLS-1$
		    _node.toString()));
	}

	if (!value.equals(node.asText())) {
	    throw new AssertionError(String.format("Expected '%s', but found '%s'", value, node));
	}

	removeProperty(name);

	return this;
    }

    public JSONValidator assertPropertyNull(String name) {
	JsonNode node = _node.get(name);
	if (node == null) {
	    throw new AssertionError(String.format("Couldn't find key with value: '%s' within json: %s", name, //$NON-NLS-1$
		    _node.toString()));
	}

	if (!node.isNull()) {
	    throw new AssertionError();
	}

	removeProperty(name);

	return this;
    }

    public JSONValidator assertSimpleArrayElement(String value) {
	Iterator<JsonNode> iterator = _arrayNode.iterator();
	boolean elementFound = false;
	while (iterator.hasNext() && !elementFound) {
	    JsonNode jsonNode = iterator.next();
	    if (jsonNode.asText().equals(value)) {
		iterator.remove();
		elementFound = true;
	    }
	}

	if (!elementFound) {
	    throw new AssertionError(String.format("Couldn't find element: '%s' within json: %s", value, //$NON-NLS-1$
		    _arrayNode));
	}

	return this;
    }

    public JSONValidator getObject(String name) {
	JSONValidator result = null;

	JsonNode jsonNode = _node.get(name);
	if (jsonNode != null) {
	    result = new JSONValidator(this, jsonNode);
	}

	if (result == null) {
	    throw new AssertionError(String.format("Couldn't find key with value: '%s' within json: %s", name, //$NON-NLS-1$
		    _node.toString()));
	}

	return result;
    }

    public JSONValidator getObjectFromArray(String keyPath, String value) {
	JSONValidator result = null;
	if (_arrayNode != null) {
	    Iterator<JsonNode> iterator = _arrayNode.elements();

	    while (iterator.hasNext() && result == null) {
		JsonNode jsonNode = iterator.next();
		String[] keys = keyPath.split("\\.");

		JsonNode node = jsonNode;
		for (String key : keys) {
		    node = node.path(key);
		}
		if (node != null && value == null) {
		    result = new JSONValidator(this, node);
		} else if (node != null && node.asText().equals(value)) {
		    result = new JSONValidator(this, jsonNode);
		    // remove already found property from object
		    ObjectNode objectNode = (ObjectNode) jsonNode;
		    for (int i = 0; i < keys.length; i++) {
			if (i == keys.length - 1) {
			    objectNode.remove(keys[i]);
			} else {
			    objectNode = (ObjectNode) objectNode.get(keys[i]);
			}
		    }
		}

	    }
	}

	if (result == null) {
	    throw new AssertionError(String.format("Couldn't find key with value: '%s' within json: %s", keyPath, //$NON-NLS-1$
		    _arrayNode));
	}

	return result;
    }

    @Override
    public String toString() {
	String result = null;
	if (_node != null) {
	    result = _node.toString();
	} else if (_arrayNode != null) {
	    result = _arrayNode.toString();
	}
	return result;
    }

    private void removeNode(JsonNode node) {
	Iterator<JsonNode> iterator = _node != null ? _node.iterator() : _arrayNode.iterator();
	boolean deleted = false;
	while (iterator.hasNext() && !deleted) {
	    JsonNode jsonNode = iterator.next();
	    if (jsonNode == node) {
		iterator.remove();
		deleted = true;
	    }
	}
    }

    private void removeProperty(String propertyName) {
	Iterator<Entry<String, JsonNode>> iterator = _node != null ? _node.fields() : _arrayNode.fields();
	boolean deleted = false;
	while (iterator.hasNext() && !deleted) {
	    Entry<String, JsonNode> object = iterator.next();
	    if (object.getKey().equals(propertyName)) {
		iterator.remove();
		deleted = true;
	    }
	}
    }
}
