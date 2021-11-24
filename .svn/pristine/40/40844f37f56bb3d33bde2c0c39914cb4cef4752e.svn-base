package org.gs4tr.termmanager.model.glossary.backup.regular;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.gs4tr.termmanager.model.glossary.backup.DbBaseDescription;

@Entity
@Table(name = "TERM_DESCRIPTION")
public class DbTermDescription extends DbBaseDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6521340852224679888L;

    private String _termUuid;

    public DbTermDescription() {
	super();
    }

    public DbTermDescription(String type, byte[] value) {
	super(type, value);
    }

    public DbTermDescription(String baseType, String type, byte[] value) {
	super(baseType, type, value);
    }

    @Column(name = "TERM_UUID", nullable = false, updatable = false, length = 128)
    public String getTermUuid() {
	return _termUuid;
    }

    public void setTermUuid(String termUuid) {
	_termUuid = termUuid;
    }

    @Override
    public String toString() {
	return "DbTermDescription [_termUuid=" + _termUuid + ", baseType=" + getBaseType() + ", type=" + getType()
		+ ", uuid=" + getUuid() + ", value=" + Arrays.toString(getValue()) + "]";
    }
}
