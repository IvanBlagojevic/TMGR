package org.gs4tr.termmanager.persistence.solr.query;

import java.io.Serializable;

import org.gs4tr.tm3.api.Page;

/**
 * Abstract implementation of {@code IPageable}.
 * 
 */
public abstract class AbstractPageRequest implements IPageable, Serializable {

    public static final int DEFAULT_PAGE = 0;

    public static final int DEFAULT_SIZE = 50;

    private static final long serialVersionUID = 9125405869976663265L;

    private final int _page;

    private final int _size;

    /**
     * Creates a new {@link AbstractPageRequest} with default page and size
     * values. Pages are zero indexed, thus providing 0 for {@code page} will
     * return the first page.
     */
    public AbstractPageRequest() {
	_page = DEFAULT_PAGE;
	_size = DEFAULT_SIZE;
    }

    /**
     * Creates a new {@link AbstractPageRequest}. Pages are zero indexed, thus
     * providing 0 for {@code page} will return the first page.
     * 
     * @param page
     *            must not be less than zero.
     * @param size
     *            must not be less than one.
     */
    public AbstractPageRequest(int page, int size) {

	if (page < 0) {
	    throw new IllegalArgumentException(Messages.getString("AbstractPageRequest.0")); //$NON-NLS-1$
	}

	if (size < 1) {
	    throw new IllegalArgumentException(Messages.getString("AbstractPageRequest.1")); //$NON-NLS-1$
	}

	_page = page;
	_size = size;
    }

    @Override
    public abstract IPageable first();

    @Override
    public int getOffset() {
	return _page * _size;
    }

    @Override
    public int getPageNumber() {
	return _page;
    }

    @Override
    public int getPageSize() {
	return _size;
    }

    @Override
    public boolean hasPrevious() {
	return _page > 0;
    }

    @Override
    public abstract IPageable next();

    /**
     * Returns the {@link IPageable} requesting the previous {@link Page}.
     * 
     * @return
     */
    public abstract IPageable previous();

    @Override
    public IPageable previousOrFirst() {
	return hasPrevious() ? previous() : first();
    }
}
