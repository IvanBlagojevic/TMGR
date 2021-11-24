package org.gs4tr.termmanager.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.gs4tr.foundation.modules.entities.model.Identifiable;
import org.gs4tr.foundation.modules.entities.model.ResourceTrack;

@NamedQueries({ @NamedQuery(name = "TermEntryResourceTrack.findByTermEntryId", query = "select resourceTrack "
	+ "from TermEntryResourceTrack resourceTrack " + "where resourceTrack.termEntryId = :termEntryId") })
@Entity
@Table(name = "TERMENTRY_RESOURCE_TRACK")
public class TermEntryResourceTrack extends ResourceTrack implements Serializable, Identifiable<Long> {

    private static final long serialVersionUID = -7494708733814300110L;

    private String _resourceName;

    private Long _resourceTrackId;

    private String _termEntryId;

    public TermEntryResourceTrack() {
    }

    public TermEntryResourceTrack(ResourceTrack resourceTrack) {
	setResourceId(resourceTrack.getResourceId());
	setTrackType(resourceTrack.getTrackType());
	setClassifier(resourceTrack.getClassifier());
	setTaskName(resourceTrack.getTaskName());
    }

    public TermEntryResourceTrack(String resourceId, String resourceTrackType, String classifier) {
	setResourceId(resourceId);
	setTrackType(resourceTrackType);
	setClassifier(classifier);
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getResourceTrackId();
    }

    @Column(name = "RESOURCE_NAME", length = 64)
    public String getResourceName() {
	return _resourceName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESOURCE_TRACK_ID")
    public Long getResourceTrackId() {
	return _resourceTrackId;
    }

    @Column(name = "TERMENTRY_ID", length = 64)
    public String getTermEntryId() {
	return _termEntryId;
    }

    @Override
    public void setIdentifier(Long identifier) {
	setResourceTrackId(identifier);
    }

    public void setResourceName(String resourceName) {
	_resourceName = resourceName;
    }

    public void setResourceTrackId(Long trackResourceId) {
	_resourceTrackId = trackResourceId;
    }

    public void setTermEntryId(String termEntryId) {
	_termEntryId = termEntryId;
    }

}