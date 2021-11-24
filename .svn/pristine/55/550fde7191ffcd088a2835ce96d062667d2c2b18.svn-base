package org.gs4tr.termmanager.model;

import java.util.Iterator;

import com.google.common.base.Splitter;

public class TmgrStringUtils {

    public static Iterator<String> split(String separator, CharSequence sequence) {
	return Splitter.on(separator).split(sequence).iterator();
    }

    public static String trimNonBreakingSpace(String str) {
	return str.replaceFirst("[\\s\\u00A0]+$", "").replaceFirst("^[\\s\\u00A0]+", "");
    }
}
