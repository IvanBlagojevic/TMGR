package org.gs4tr.termmanager.model;

import java.io.Serializable;
import java.util.Comparator;

import org.gs4tr.termmanager.model.dto.Language;

public class LanguageComparator implements Comparator<Language>, Serializable {

    @Override
    public int compare(Language language1, Language language2) {
	return language1.getValue().compareTo(language2.getValue());
    }

}
