package org.gs4tr.termmanager.service.counter;

import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;

public interface TermCounter {

    void updateTermCount(ProjectDetailInfo detailInfo, Term term);

}
