package org.gs4tr.termmanager.persistence.solr.query;

import org.gs4tr.tm3.api.Page;

/**
 * This interface provides pagination information.
 * 
 */
public interface IPageable {

    /**
     * Returns the {@link IPageable} requesting the first page.
     * 
     * @return
     */
    IPageable first();

    /**
     * Returns the offset to be taken according to the underlying page and page
     * size.
     * 
     * @return the offset to be taken
     */
    int getOffset();

    /**
     * Returns the page to be returned.
     * 
     * @return the page to be returned.
     */
    int getPageNumber();

    /**
     * Returns the number of items to be returned.
     * 
     * @return the number of items of that page
     */
    int getPageSize();

    /**
     * Returns the sorting parameters.
     * 
     * @return
     */
    Sort getSort();

    /**
     * Returns whether there's a previous {@link IPageable} we can access from
     * the current one. Will return {@literal false} in case the current
     * {@link IPageable} already refers to the first page.
     * 
     * @return
     */
    boolean hasPrevious();

    /**
     * Returns the {@link IPageable} requesting the next {@link Page}.
     * 
     * @return
     */
    IPageable next();

    /**
     * Returns the previous {@link IPageable} or the first {@link IPageable} if
     * the current one already is the first one.
     * 
     * @return
     */
    IPageable previousOrFirst();
}
