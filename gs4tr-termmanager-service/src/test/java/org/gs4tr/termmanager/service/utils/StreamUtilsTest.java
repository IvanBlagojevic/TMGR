package org.gs4tr.termmanager.service.utils;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class StreamUtilsTest {

    @Test
    public void testSplitList() {
	int numberOfParts = 2;
	List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);

	Map<Integer, List<Integer>> splitList = StreamUtils.splitList(list, numberOfParts);
	Assert.assertEquals(numberOfParts, splitList.keySet().size());
    }
}
