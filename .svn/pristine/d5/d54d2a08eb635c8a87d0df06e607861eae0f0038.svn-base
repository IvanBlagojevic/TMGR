package org.gs4tr.termmanager.service.comparator;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation3.core.diff.DiffMatchPatch.Diff;
import org.gs4tr.foundation3.core.diff.DiffMatchPatch.Operation;
import org.gs4tr.termmanager.model.dto.Difference;

/**
 * Package private supporting class which converts list of <code>Diff</code>
 * objects to list of <code>Difference</code>.
 * 
 * @author TMGR_Backend
 */
class DiffsConverter {

    public static List<Difference> convertToDifferences(final List<Diff> diffs) {
	int startIndex = 0, size = diffs.size();

	List<Difference> differences = new ArrayList<>(size);
	for (final Diff diff : diffs) {
	    final int length = diff.text.length();
	    if (Operation.INSERT == diff.operation) {
		String operation = Operation.INSERT.name();
		differences.add(new Difference(startIndex, length, operation));
	    } else if (Operation.DELETE == diff.operation) {
		String operation = Operation.DELETE.name();
		differences.add(new Difference(startIndex, length, operation));
	    }
	    startIndex += length;
	}
	return differences;
    }

    private DiffsConverter() {
    }
}
