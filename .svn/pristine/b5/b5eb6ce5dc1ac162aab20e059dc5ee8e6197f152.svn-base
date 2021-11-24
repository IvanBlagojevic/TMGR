package org.gs4tr.termmanager.service.comparator;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation3.core.diff.DiffMatchPatch;
import org.gs4tr.foundation3.core.diff.DiffMatchPatch.Diff;

class TextComparator implements Comparator<String, List<Diff>> {

    public static final Comparator<String, List<Diff>> INSTANCE = new TextComparator();

    /**
     * Speedup flag. If false, then don't run a line-level diff first to
     * identify the changed areas. If true, then run a faster slightly less
     * optimal diff.
     */
    private static final boolean CHECK_LINES = false;

    /**
     * Number of seconds to map a diff before giving up.
     */
    private static final float DIFF_TIMEOUT = 1.0f;

    /**
     * {@code DiffMatchPatch} takes two texts and finds the differences.
     * <p>
     * This implementation works on a character by character basis. The result
     * of any {@link DiffMatchPatch.Diff} may contain 'chaff', irrelevant small
     * commonalities which complicate the output. A post-diff cleanup algorithm
     * factors out these trivial commonalities.
     */
    private final DiffMatchPatch _diffMatchPatch;

    private TextComparator() {
	_diffMatchPatch = new DiffMatchPatch();
	_diffMatchPatch.Diff_Timeout = DIFF_TIMEOUT;
    }

    @Override
    public List<Diff> compare(String oldText, String newText) {
	String nullSafeOldText = nullSafeText(oldText);
	String nullSafeNewText = nullSafeText(newText);
	
	LinkedList<Diff> diffs = getDiffMatchPatch().diff_main(nullSafeOldText, nullSafeNewText, CHECK_LINES);
	/*
	 * Increase human readability by factoring out commonalities which are
	 * likely to be coincidental.
	 */
	getDiffMatchPatch().diff_cleanupSemantic(diffs);
	
	return diffs;
    }

    private DiffMatchPatch getDiffMatchPatch() {
	return _diffMatchPatch;
    }

    private String nullSafeText(final String text) {
	return Objects.isNull(text) ? StringUtils.EMPTY : text;
    }
}
