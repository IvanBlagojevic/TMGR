package org.gs4tr.termmanager.persistence.solr.query;

import java.util.Map;
import java.util.Set;

public interface ISearchFilter {

    void addFilterProperty(String key, String value);

    void addResultField(String... field);

    Map<String, String> getFilterProperties();

    IPageable getPageable();

    Set<String> getResultFields();

    void setPageable(IPageable pageable);
}
