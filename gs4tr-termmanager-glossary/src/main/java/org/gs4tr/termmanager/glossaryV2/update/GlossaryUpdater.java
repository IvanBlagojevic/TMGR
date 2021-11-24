package org.gs4tr.termmanager.glossaryV2.update;

import org.gs4tr.termmanager.glossaryV2.GlossaryUpdateRequestExt;
import org.gs4tr.tm3.api.BatchProcessResult;

public interface GlossaryUpdater {
    BatchProcessResult update(GlossaryUpdateRequestExt request);
}
