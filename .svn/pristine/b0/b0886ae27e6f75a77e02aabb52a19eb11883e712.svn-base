package org.gs4tr.termmanager.model.glossary.backup.regular;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.gs4tr.termmanager.model.glossary.backup.DbBaseDescription;

@Entity
@Table(name = "TERMENTRY_DESCRIPTION")
public class DbTermEntryDescription extends DbBaseDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6521340852224679888L;

    private String _termEntryUuid;

    public DbTermEntryDescription() {
	super();
    }

    public DbTermEntryDescription(String type, byte[] value) {
	super(type, value);
    }

    public DbTermEntryDescription(String baseType, String type, byte[] value) {
	super(baseType, type, value);
    }

    @Column(name = "TERMENTRY_UUID", nullable = false, length = 128, updatable = false)
    public String getTermEntryUuid() {
	return _termEntryUuid;
    }

    public void setTermEntryUuid(String termEntryUuid) {
	_termEntryUuid = termEntryUuid;
    }

    @Override
    public String toString() {
	return "DbTermEntryDescription [_termEntryUuid=" + _termEntryUuid + ", baseType=" + getBaseType() + ", type="
		+ getType() + ", uuid=" + getUuid() + ", value=" + Arrays.toString(getValue()) + "]";
    }
}
