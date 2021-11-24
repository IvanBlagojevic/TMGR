package org.gs4tr.termmanager.persistence;

import org.gs4tr.termmanager.persistence.solr.query.ISearchFilter;
import org.gs4tr.tm3.api.Page;

public interface IGlossarySearcher<T, F extends ISearchFilter, EX extends Exception> {

    Page<T> concordanceSearch(F filter) throws EX;

    Page<T> segmentSearch(F filter) throws EX;
}
