package org.gs4tr.termmanager.model.glossary.backup.regular;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.gs4tr.termmanager.model.glossary.backup.DbBaseTermHistory;

@Entity
@Table(name = "TERM_HISTORY")
public class DbTermHistory extends DbBaseTermHistory implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 231277229271406302L;

    public DbTermHistory() {

    }
}
