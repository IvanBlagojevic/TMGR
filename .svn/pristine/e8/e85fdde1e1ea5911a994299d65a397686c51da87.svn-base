package org.gs4tr.termmanager.service.xls;

import java.io.InputStream;
import java.util.Objects;

import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;

import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.aspose.cells.WorkbookSettings;

public class AsposeFactory {

    public static final String ASPOSE_LICENSE = "Aspose.Total.Java.lic"; //$NON-NLS-1$

    public static AsposeFactory getInstance(Class<?> clazz) {
	AsposeFactory result = new AsposeFactory();

	InputStream licence = clazz.getClassLoader().getResourceAsStream(AsposeFactory.ASPOSE_LICENSE);
	result.setCellsLicenseFile(licence);

	return result;
    }

    private com.aspose.cells.License _cellsLicence;

    public Workbook createWorkbookInstance(InputStream stream) {
	try {
	    // Added 18-March-2017 for TERII-4679
	    LoadOptions opt = new LoadOptions();
	    opt.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);

	    Workbook workbook = Objects.nonNull(stream) ? new Workbook(stream, opt) : new Workbook();
	    // TERII-3353
	    WorkbookSettings settings = workbook.getSettings();
	    settings.setCheckExcelRestriction(false);

	    return workbook;
	} catch (Exception e) {
	    throw new UserException(MessageResolver.getMessage("AsposeFactory.0"), e); //$NON-NLS-1$
	}
    }

    private void setCellsLicenseFile(InputStream licenseStream) {
	try {
	    _cellsLicence = new com.aspose.cells.License();
	    _cellsLicence.setLicense(licenseStream);
	    licenseStream.close();
	} catch (Exception e) {
	    throw new RuntimeException(Messages.getString("AsposeFactory.1"), e); //$NON-NLS-1$
	}
    }
}
