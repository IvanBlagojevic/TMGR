package org.gs4tr.termmanager.service.utils;

import java.beans.PropertyEditorSupport;
import java.util.Collection;

import org.gs4tr.foundation.modules.entities.model.types.EntityType;
import org.gs4tr.termmanager.model.EntityTypeHolder;

public class EntityTypePropertyEditor<T extends EntityType> extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
	Collection<EntityType> entityTypes = EntityTypeHolder.values();
	for (EntityType entityType : entityTypes) {
	    if (entityType.toString().equals(text)) {
		setValue(entityType);
		return;
	    }
	}

	throw new IllegalArgumentException(
		"Invalid text for entityTypeClass of type '" + EntityType.class.getName() + ": '" + text + "'.");
    }

}