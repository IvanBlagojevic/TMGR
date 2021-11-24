package org.gs4tr.termmanager.service.xls;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.gs4tr.foundation3.io.CsvReader.ReturnCode;
import org.junit.Assert;
import org.junit.Test;

public class BasicTest {

    @Test
    public void basicTest() throws Exception {

	String path = "src/test/resources/xls/import.xls";

	InputStream inputStream = new FileInputStream(path);

	XlsReader reader = new XlsReader(inputStream, false, false);

	List<String> header = reader.getHeaderColumns();

	Assert.assertEquals(4, header.size());

	// case sensitive is false
	Assert.assertEquals("id", header.get(0));

	Assert.assertEquals("name", header.get(1));

	List<String> fields = new LinkedList<String>();

	Assert.assertEquals(ReturnCode.LINE_END, reader.readNextRecord(fields));

	Assert.assertEquals("1", fields.get(0));

	Assert.assertEquals("filip", fields.get(1));

	Assert.assertEquals(ReturnCode.LINE_END, reader.readNextRecord(fields));

	Assert.assertEquals(ReturnCode.FILE_END, reader.readNextRecord(fields));

	Assert.assertEquals("3", fields.get(0));

	// null from xls is empty string
	Assert.assertEquals("", fields.get(2));

    }

    @Test
    public void testXlsx() throws Exception {

	// data are from the second worksheet

	String path = "src/test/resources/xls/import.xlsx";

	InputStream inputStream = new FileInputStream(path);

	XlsReader reader = new XlsReader(inputStream, true, true);

	List<String> header = reader.getHeaderColumns();

	Assert.assertEquals(4, header.size());

	// case sensitive is true
	Assert.assertEquals("Id", header.get(0));

	// null from xls is empty string
	Assert.assertEquals("", header.get(2));

	List<String> fields = new LinkedList<String>();

	Assert.assertEquals(ReturnCode.LINE_END, reader.readNextRecord(fields));

	Assert.assertEquals(ReturnCode.LINE_END, reader.readNextRecord(fields));

	Assert.assertEquals(ReturnCode.FILE_END, reader.readNextRecord(fields));

    }
}
