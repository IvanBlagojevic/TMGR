package org.gs4tr.termmanager.service.comparator;

/**
 * This interface represents a comparison function which compares two arguments
 * of type <T>.
 * 
 * @author TMGR_Backend
 *
 * @param <T> the type of objects that may be compared by this comparator
 * @param <R> the type of result that will be returned by this comparator
 */
public interface Comparator<T, R> {

    R compare(T previous, T current);
}
