package org.gs4tr.termmanager.service.solr.restore;

import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.service.solr.restore.model.RecodeOrCloneCommand;

public interface RecodeOrCloneTermsProcessor {

    void initAndValidateCommands(List<RecodeOrCloneCommand> recodeCommands, List<RecodeOrCloneCommand> cloneCommands,
	    String projectsToRecode, String projectsToClone);

    boolean isRebuildByProjectShortCodes();

    Set<String> recodeOrCloneTerms();

    Set<String> recodeOrCloneTerms(List<RecodeOrCloneCommand> recodeCommands, List<RecodeOrCloneCommand> cloneCommands);

}
