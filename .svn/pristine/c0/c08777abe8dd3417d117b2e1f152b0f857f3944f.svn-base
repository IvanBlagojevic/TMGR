package org.gs4tr.termmanager.service.mocking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.model.ImportErrorAction;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.ImportTermService;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import junit.framework.Assert;

@TestSuite("service")
public class ImportTermServiceTest extends AbstractServiceTest {

    @Autowired
    private ImportTermService _importTermService;

    @Ignore
    @Test
    public void importTbxTest_01() throws Exception {
	File file = new File("src/test/resources/testfiles/medtronic.tbx");
	String fileEncoding = ServiceUtils.getFileEncoding(file);
	InputStream tbxStream = null;
	try {
	    tbxStream = new FileInputStream(file);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
	ImportProgressInfo importProgressInfo = new ImportProgressInfo(0);
	long startTime = System.currentTimeMillis();
	importProgressInfo.getImportSummary().setStartTime(startTime);

	List<String> importLocales = new ArrayList<>();
	importLocales.add("de");

	Long projectId = 1L;

	ImportOptionsModel importOpstions = new ImportOptionsModel();
	importOpstions.setProjectId(projectId);
	importOpstions.setSyncLanguageId("en");
	importOpstions.setImportErrorAction(ImportErrorAction.SKIP);
	importOpstions.setStatus(ItemStatusTypeHolder.PROCESSING.getName());
	importOpstions.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);

	ImportSummary importSummary = getImportTermService().importDocument(tbxStream, importProgressInfo,
		importOpstions, ImportTypeEnum.TBX, fileEncoding, SyncOption.MERGE);

	String time = importSummary.getTotalTimeForImport();
	Assert.assertNotNull(time);
    }

    private ImportTermService getImportTermService() {
	return _importTermService;
    }
}
