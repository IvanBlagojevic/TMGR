package org.gs4tr.termmanager.service.tbx;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.List;

import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.service.AbstractSpringTermEntryReaderTest;
import org.gs4tr.termmanager.service.file.analysis.model.Alert;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;
import org.gs4tr.termmanager.service.impl.ImportValidationCallback;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderConfig;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderFactory;
import org.junit.Test;

public class DefaultTbxTermEntryReaderTest extends AbstractSpringTermEntryReaderTest {

    private static final String TEST_FILE_NAME = "terms_skype_utf8_TMgr4-fixed-by-Marko.tbx";
    private static final String TEST_FILES_PATH = "src/test/resources/tbx/analysisTestFiles/";

    @Test
    public void whneTbxFileFormatIsInvalidReturnFileParseErrorTest() throws FileNotFoundException {
	TermEntryReaderConfig.Builder builder = new TermEntryReaderConfig.Builder();
	builder.importType(ImportTypeEnum.TBX);
	builder.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, TEST_FILE_NAME).toFile()));
	TermEntryReaderConfig termEntryReaderConfig = builder.build();

	TermEntryReader reader = TermEntryReaderFactory.INSTANCE.createReader(termEntryReaderConfig);
	ImportValidationCallback validationCallback = new ImportValidationCallback(1);
	reader.validate(validationCallback);

	List<Alert> alerts = validationCallback.getAlerts();
	assertEquals(1, alerts.size());

	Alert saxParseExceptionAlert = alerts.get(0);

	assertEquals(AlertSubject.FILE_PARSE_ERROR, saxParseExceptionAlert.getSubject());
	assertEquals(AlertType.ERROR, saxParseExceptionAlert.getType());
	String cause = "Element or attribute do not match QName production: QName::=(NCName':')?NCName. ";
	assertEquals(String.format(MessageResolver.getMessage("ImportValidationCallback.0"), 62, 18, cause),
		saxParseExceptionAlert.getMessage());
    }
}
