package org.gs4tr.termmanager.service.impl;

import org.gs4tr.termmanager.model.ExportInfo;

public interface ExportNotificationCallback {

    String getThreadName();

    void init(long totalNumberOfItems);

    void notifyItemProcessingFinished();

    void notifyProcessingFinished(ExportInfo exportInfo);
}