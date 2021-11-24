package org.gs4tr.termmanager.service.xls;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation3.io.CsvReader.ReturnCode;
import org.junit.Ignore;
import org.junit.Test;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.Font;
import com.aspose.cells.Style;

@Ignore
public class FindDupsTest {

    private static final String CTD = "/home/emisia/Downloads/CTD.xlsx";

    private static final List<String> STATUSES = Arrays.asList("approved", "on hold", "pending approval");

    @Test
    public void testColorDups() throws Exception {
	InputStream inputStream = new FileInputStream(CTD);

	XlsReader reader = new XlsReader(inputStream, false, false);
	Cells cells = reader.getWorksheet().getCells();

	List<String> input = new ArrayList<>();

	ReturnCode returnCode = null;

	while (ReturnCode.FILE_END != returnCode) {

	    returnCode = reader.readNextRecord(input);

	    Map<String, List<Integer>> duplicateIndexesMap = getDuplicateIndexesMap(input);

	    duplicateIndexesMap.values().forEach(columnIndexes -> {
		columnIndexes.forEach(columnIndex -> {
		    Cell cell = cells.get(reader.getConfiguration().getRowIndex(), columnIndex);
		    colorRedCell(cell);
		});
	    });

	    input.clear();
	}

	reader.getWorkbook().save(CTD);
    }

    @Test
    public void testRemoveDups() throws Exception {
	InputStream inputStream = new FileInputStream(CTD);

	XlsReader reader = new XlsReader(inputStream, false, false);
	Cells cells = reader.getWorksheet().getCells();

	List<String> input = new ArrayList<>();

	ReturnCode returnCode = null;

	while (ReturnCode.FILE_END != returnCode) {

	    returnCode = reader.readNextRecord(input);

	    int rowIndex = reader.getConfiguration().getRowIndex();

	    Map<String, List<Integer>> duplicateIndexesMap = getDuplicateIndexesMap(input);

	    duplicateIndexesMap.values().forEach(columnIndexes -> {
		columnIndexes.forEach(columnIndex -> {
		    cells.clearContents(rowIndex, columnIndex, rowIndex, columnIndex);
		});
	    });

	    input.clear();
	}

	reader.getWorkbook().save(CTD);
    }

    private void colorRedCell(Cell cell) {
	Style style = cell.getStyle();

	Font font = style.getFont();
	font.setBold(true);
	font.setColor(Color.getRed());

	cell.setStyle(style);
    }

    private Map<String, List<Integer>> getDuplicateIndexesMap(List<String> input) {
	Map<String, List<Integer>> duplicateIndexesMap = new HashMap<>();

	for (int i = 0; i < input.size(); i++) {
	    if (!isEmptyOrTermStatus(input.get(i))) {
		duplicateIndexesMap.computeIfAbsent(input.get(i), c -> new ArrayList<>()).add(i);
	    }
	}
	// remove first index of duplicate occurrence
	removeFirstDuplicateIndex(input, duplicateIndexesMap);

	return duplicateIndexesMap;
    }

    private boolean isEmptyOrTermStatus(String item) {
	return item.isEmpty() || STATUSES.contains(item.toLowerCase());
    }

    private void removeFirstDuplicateIndex(List<String> input, Map<String, List<Integer>> duplicateIndexesMap) {
	Set<String> distinct = new HashSet<>(input);

	distinct.removeIf(s -> isEmptyOrTermStatus(s));

	for (String item : distinct) {
	    List<Integer> duplicateIndexes = duplicateIndexesMap.get(item);

	    if (CollectionUtils.isNotEmpty(duplicateIndexes)) {
		duplicateIndexes.remove(0);
	    }
	}
    }
}