package org.gs4tr.termmanager.service.persistence.importer.termentry;

/**
 * <p>
 * A <i>buffer</i> is used as an intermediate temporary store for data between a
 * fast and a slow entity. As one party would have to wait for the other
 * affecting performance, the buffer alleviates this by allowing entire blocks
 * of data to move at once rather then in small chunks. The data is written and
 * read only once from the buffer. Furthermore, the buffers are visible to at
 * least one party which is aware of it.
 * </p>
 * 
 * @author TMGR_Backend
 *
 * @param <T>
 */
public interface GenericBuffer<T> {

    void clear();

    void add(T t);
}
