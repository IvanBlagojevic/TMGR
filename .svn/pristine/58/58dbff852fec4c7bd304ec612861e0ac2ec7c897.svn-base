package org.gs4tr.termmanager.persistence.solr.query;

import org.gs4tr.termmanager.persistence.solr.query.Sort.Direction;

/**
 * Basic implementation of {@code IPageable}.
 */
public class PageRequest extends AbstractPageRequest {

    private static final long serialVersionUID = 5915171662219548415L;

    private Sort _sort;

    /**
     * Creates a new {@link PageRequest} with default page and size values.
     * Pages are zero indexed, thus providing 0 for {@code page} will return the
     * first page.
     */
    public PageRequest() {
	super();
    }

    /**
     * Creates a new {@link PageRequest}. Pages are zero indexed, thus providing
     * 0 for {@code page} will return the first page.
     * 
     * @param page
     *            zero-based page index.
     * @param size
     *            the size of the page to be returned.
     */
    public PageRequest(int page, int size) {
	super(page, size);
    }

    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     * 
     * @param page
     *            zero-based page index.
     * @param size
     *            the size of the page to be returned.
     * @param direction
     *            the direction of the {@link Sort} to be specified, can be
     *            {@literal null}.
     * @param properties
     *            the properties to sort by, must not be {@literal null} or
     *            empty.
     */
    public PageRequest(int page, int size, Direction direction, String... properties) {
	this(page, size, new Sort(direction, properties));
    }

    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     * 
     * @param page
     *            zero-based page index.
     * @param size
     *            the size of the page to be returned.
     * @param sort
     *            can be {@literal null}.
     */
    public PageRequest(int page, int size, Sort sort) {
	super(page, size);
	_sort = sort;
    }

    @Override
    public IPageable first() {
	return new PageRequest(0, getPageSize(), getSort());
    }

    @Override
    public Sort getSort() {
	return _sort;
    }

    @Override
    public IPageable next() {
	return new PageRequest(getPageNumber() + 1, getPageSize(), getSort());
    }

    @Override
    public IPageable previous() {
	return getPageNumber() == 0 ? this : new PageRequest(getPageNumber() - 1, getPageSize(), getSort());
    }

    public void setSort(Sort sort) {
	_sort = sort;
    }
}
