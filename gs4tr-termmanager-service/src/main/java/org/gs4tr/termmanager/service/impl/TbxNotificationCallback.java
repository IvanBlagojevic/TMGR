package org.gs4tr.termmanager.service.impl;

import org.gs4tr.termmanager.model.ImportSummary;

public interface TbxNotificationCallback {

    ImportSummary getImportSummary();

    int getTotalTermEntries();

    void notifyPercentage(int percentage);

}
