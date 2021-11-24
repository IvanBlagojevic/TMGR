package org.gs4tr.termmanager.service.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class StreamUtils {

    public static File createTempFile(InputStream inputStream) {
	try {
	    String fileName = "import_"; //$NON-NLS-1$
	    File tmpFile = File.createTempFile(fileName, null);
	    OutputStream outputStream = new FileOutputStream(tmpFile);
	    IOUtils.copyLarge(inputStream, outputStream);
	    inputStream.close();
	    outputStream.close();
	    return tmpFile;
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public static InputStream openTempInputStream(File tmpFile) {
	try {
	    return new FileInputStream(tmpFile);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public static <T> Map<Integer, List<T>> splitList(List<T> collection, int numberOfParts) {
	Map<Integer, List<T>> split = new HashMap<>();
	int size = (int) Math.round(collection.size() / numberOfParts + .5);
	int count = 1;
	for (int start = 0; start < collection.size(); start += size) {
	    int end = Math.min(start + size, collection.size());
	    List<T> sublist = collection.subList(start, end);
	    split.put(count, sublist);
	    count++;
	}
	return split;
    }
}