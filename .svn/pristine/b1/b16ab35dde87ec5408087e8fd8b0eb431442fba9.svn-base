package org.gs4tr.termmanager.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.types.BaseEntityTypeHolder;
import org.gs4tr.foundation.modules.entities.model.types.EntityType;

public class EntityTypeHolder extends BaseEntityTypeHolder {
    public static final EntityType ADMIN = createTypeInstance("admin");
    public static final EntityType LANGUAGESET = createTypeInstance("languageSet");
    public static final EntityType PROJECTDETAIL = createTypeInstance("projectDetail");
    public static final EntityType PROJECTLANGUAGEDETAIL = createTypeInstance("projectLanguageDetail");
    public static final EntityType SUBMISSIONDETAIL = createTypeInstance("submissionDetail");
    public static final EntityType SUBMISSIONLANGUAGEDETAIL = createTypeInstance("submissionLanguageDetail");
    public static final EntityType SUBMISSIONTERM = createTypeInstance("submissionTerm");
    public static final EntityType SUBMISSIONTERMDESCRIPTION = createTypeInstance("submissionTermDescription");
    public static final EntityType TERM = createTypeInstance("term");
    public static final EntityType TERM_DESCRIPTION = createTypeInstance("termDescription");
    public static final EntityType TERMENTRY = createTypeInstance("termentry");
    public static final EntityType TERMENTRY_DESCRIPTION = createTypeInstance("termentryDescription");
    public static final EntityType TRANSLATION = createTypeInstance("translation");

    static {
	initialize();
    }

    public static List<EntityType> getInternalValues() {
	List<EntityType> entityTypes = new ArrayList<EntityType>();
	entityTypes.add(TERM);
	entityTypes.add(TERM_DESCRIPTION);
	entityTypes.add(TERMENTRY_DESCRIPTION);
	return entityTypes;
    }

    public static EntityType valueOf(String name) {
	return BaseEntityTypeHolder.valueOf(name);
    }

    public static Collection<EntityType> values() {
	return BaseEntityTypeHolder.values();
    }

    private static void initialize() {
	add(TERMENTRY);
	add(TRANSLATION);
	add(TERM);
	add(TERMENTRY_DESCRIPTION);
	add(TERM_DESCRIPTION);
	add(ADMIN);
	add(LANGUAGESET);
	add(PROJECTDETAIL);
	add(PROJECTLANGUAGEDETAIL);
	add(SUBMISSIONTERM);
	add(SUBMISSIONDETAIL);
	add(SUBMISSIONLANGUAGEDETAIL);
	add(SUBMISSIONTERMDESCRIPTION);
    }
}
