package org.gs4tr.termmanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@NamedQueries({
	@NamedQuery(name = "ProjectDescription.findByProjectId", query = "select description "
		+ "from ProjectDescription description " + "where description.tmProject.projectId = :projectId"),
	@NamedQuery(name = "ProjectDescription.updateProjectAvailableDescription", query = "update TmProject project set project.availableDescription = :availableDescription "
		+ " where project.projectId =:projectId"),
	@NamedQuery(name = "ProjectDescription.deleteByProjectId", query = "delete from ProjectDescription description "
		+ " where description.tmProject.projectId =:projectId") })

@Entity
@Table(name = "PROJECT_DESCRIPTION")
public class ProjectDescription {

    private Long _projectDescriptionId;

    private String _text;

    private TmProject _tmProject;

    public ProjectDescription(String text, TmProject project) {
	_text = text;
	_tmProject = project;
    }

    public ProjectDescription() {
    }

    @Id
    @Column(name = "PROJECT_DESCRIPTION_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getProjectDescriptionId() {
	return _projectDescriptionId;
    }

    @Lob
    @Column(name = "TEXT", nullable = false)
    public String getText() {
	return _text;
    }

    @OneToOne
    @JoinColumn(name = "PROJECT_ID", nullable = false, updatable = false)
    public TmProject getTmProject() {
	return _tmProject;
    }

    public void setProjectDescriptionId(Long projectDescriptionId) {
	_projectDescriptionId = projectDescriptionId;
    }

    public void setText(String text) {
	_text = text;
    }

    public void setTmProject(TmProject tmProject) {
	_tmProject = tmProject;
    }
}
