package org.gs4tr.termmanager.model;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.dto.Description;

public class CustomCollectionUtils {

    @SuppressWarnings("unchecked")
    public static <T> T[] getArray(Set<T> set, Class<?> itemClass) {
	if (isEmpty(set)) {
	    return null;
	}

	T[] array = (T[]) Array.newInstance(itemClass, set.size());

	int i = 0;

	for (T t : set) {
	    array[i] = t;
	    i++;
	}

	return array;
    }

    public static <T> List<T> getArrayList(T[] array) {
	if (array == null) {
	    return null;
	}

	List<T> list = new ArrayList<>();

	Collections.addAll(list, array);

	return list;
    }

    public static <T> Set<T> getSet(T[] array) {
	if (array == null) {
	    return null;
	}

	Set<T> list = new HashSet<>();

	Collections.addAll(list, array);

	return list;
    }

    public static <T> String listToString(List<T> list) {
	return listToString(list, StringUtils.EMPTY);
    }

    public static void sortDescriptions(Description[] termNotes) {
	Arrays.sort(termNotes, Comparator.comparing(Description::getType));
    }

    private static <T> String listToString(List<T> list, String delimiter) {
	if (list == null) {
	    return null;
	}

	StringBuilder buffer = new StringBuilder();
	for (T item : list) {
	    buffer.append(item).append(delimiter);
	}
	buffer.delete(buffer.length() - delimiter.length(), buffer.length());

	return buffer.toString();
    }

}
