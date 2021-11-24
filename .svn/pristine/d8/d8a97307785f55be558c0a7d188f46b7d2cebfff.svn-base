package org.gs4tr.termmanager.service.solr.restore;

import java.util.List;

import org.gs4tr.termmanager.service.solr.restore.model.RecodeOrCloneCommand;

/**
 * Improved processor that is used to restore back up. Backup is written like a
 * snapshot of solr index.
 * 
 * @author emisia
 * 
 */
public interface IRestoreProcessorV2 {

    /**
     * @return restore percentage
     */
    int getPercentage();

    /**
     * @return true if restore is finished
     */
    boolean isFinished();

    /**
     * Starts restore from backup for specified collection
     */
    void restore() throws Exception;

    /**
     * Perform Recode/Clone action and starts restore from backup for specified
     * collection while application is running
     */
    void restoreRecodeOrClone(List<RecodeOrCloneCommand> recodeCommands, List<RecodeOrCloneCommand> cloneCommands)
	    throws Exception;
}
