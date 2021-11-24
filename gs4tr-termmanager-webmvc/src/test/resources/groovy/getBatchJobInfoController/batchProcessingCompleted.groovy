package groovy.getBatchJobInfoController

import org.gs4tr.termmanager.model.BatchJobName

processedItem = builder.batchJobInfo(batchJobName: BatchJobName.DELETE_RENAME_PROJECT_ATTRIBUTE, displayPopup: Boolean.TRUE,
        longSuccessMessage: "Project: Skype<br/>Language attribute: context", msgLongSubTitle: "Project attributes are deleted or renamed",
        msgSubTitle: "Project attributes deleted or renamed", projectName: "Skype", successMessage: "Maintenance complete.", title: "Information")