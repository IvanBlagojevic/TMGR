package org.gs4tr.termmanager.service.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.gs4tr.termmanager.service.xls.XlsReader;
import org.gs4tr.termmanager.service.xls.XlsType;
import org.gs4tr.termmanager.service.xls.XlsWriter;
import org.junit.Test;

import junit.framework.Assert;

public class XlsWriterTest {

    @Test
    public void testWrite() throws Exception {
	File xlsFile = new File("target/testWrite.xlsx");
	OutputStream out = new FileOutputStream(xlsFile);

	List<String> header = new LinkedList<String>();
	header.add("termEntryId");
	header.add("definition");
	header.add("en");
	header.add("de");
	header.add("en:context");
	header.add("de:context");

	List<String> row = new LinkedList<String>();
	row.add(UUID.randomUUID().toString());
	row.add("entry definition");
	row.add("dog");
	row.add("haund");
	row.add("en att");
	row.add("de att");

	XlsWriter writer = new XlsWriter();
	writer.write(header, true);
	writer.write(row, false);

	writer.save(out, XlsType.XLSX);

	out.flush();
	out.close();

	InputStream stream = new FileInputStream(xlsFile);

	XlsReader reader = new XlsReader(stream, true, true);
	Assert.assertEquals(header, reader.getHeaderColumns());

	List<String> readRow = new LinkedList<String>();
	reader.readNextRecord(readRow);
	Assert.assertEquals(row, readRow);
    }

    @Test
    public void testWriteAddToHeader() throws Exception {
	File xlsFile = new File("target/testWriteAddToHeader.xlsx");
	OutputStream out = new FileOutputStream(xlsFile);

	List<String> header = new LinkedList<String>();
	header.add("termEntryId");
	header.add("definition");
	header.add("en");
	header.add("sr");
	header.add("en:context");
	header.add("sr:context");

	List<String> row1 = new LinkedList<String>();
	row1.add(UUID.randomUUID().toString());
	row1.add("entry definition");
	row1.add("dog");
	row1.add("pas");
	row1.add("en att");
	row1.add("sr att");

	XlsWriter writer = new XlsWriter();
	writer.write(header, true);
	writer.write(row1, false);

	writer.addHeaderCell("fr");

	List<String> row2 = new LinkedList<String>();
	row2.add(UUID.randomUUID().toString());
	row2.add("entry definition");
	row2.add("cat");
	row2.add("macka");
	row2.add("en att1");
	row2.add("de att1");
	row2.add("la maca");

	writer.write(row2, false);

	writer.save(out, XlsType.XLSX);

	out.flush();
	out.close();

	InputStream stream = new FileInputStream(xlsFile);

	XlsReader reader = new XlsReader(stream, true, true);
	Assert.assertEquals(header, reader.getHeaderColumns());
    }
}
