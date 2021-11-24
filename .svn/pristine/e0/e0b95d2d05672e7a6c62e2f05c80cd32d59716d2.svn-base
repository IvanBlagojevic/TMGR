package org.gs4tr.termmanager.io.tests.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.gs4tr.termmanager.io.tests.TestHelper;
import org.gs4tr.termmanager.model.serializer.JsonIO;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.junit.Assert;
import org.junit.Test;

public class JsonIOTest {

    @Test
    public void testJsonIO() {
	List<TermEntry> toWrite = Arrays.asList(TestHelper.createTermEntry());
	byte[] bytes = JsonIO.writeValueAsBytes(toWrite);
	Assert.assertNotNull(bytes);

	InputStream in = new ByteArrayInputStream(bytes);
	TermEntry[] toRead = JsonIO.readValue(in, TermEntry[].class);
	Assert.assertNotNull(toRead);

	Assert.assertEquals(toWrite, Arrays.asList(toRead));
    }


    @Test
    public void testJsonIO2() {
        TermEntry toWrite = TestHelper.createTermEntry();
        byte[] bytes = JsonIO.writeValueAsBytes(toWrite);
        Assert.assertNotNull(bytes);

        TermEntry toRead = JsonIO.readValue(bytes, TermEntry.class);
        Assert.assertNotNull(toRead);

        Assert.assertEquals(toWrite, toRead);
    }
}
