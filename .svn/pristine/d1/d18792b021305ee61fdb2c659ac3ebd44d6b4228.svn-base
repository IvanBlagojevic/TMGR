package org.gs4tr.termmanager.service.solr.restore;

/**
 * This is an start-up processor which mission is to clean (delete) unused or
 * hidden terminology from backup.
 *
 * @author emisia
 */
public interface ICleanUpProcessorV2 {

    /**
     * @return true if restore is finished
     */
    boolean isFinished();

    /**
     * Starts cleanup from backup for all or specified projects
     */
    void cleanup() throws Exception;
}
