package org.gs4tr.termmanager.model.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("task")
public class Task {
    @XStreamOmitField
    private int _selectStyle;

    @XStreamAlias("taskId")
    private Integer _taskId;

    @XStreamAlias("taskName")
    private String _taskName;

    private int _weight;

    public Task() {
    }

    public Task(String name, Integer taskId) {
	_taskName = name;
	_taskId = taskId;
    }

    public int getSelectStyle() {
	return _selectStyle;
    }

    public Integer getTaskId() {
	return _taskId;
    }

    public String getTaskName() {
	return _taskName;
    }

    public int getWeight() {
	return _weight;
    }

    public void setSelectStyle(int selectStyle) {
	_selectStyle = selectStyle;
    }

    public void setTaskId(Integer taskId) {
	_taskId = taskId;
    }

    public void setTaskName(String taskName) {
	_taskName = taskName;
    }

    public void setWeight(int weight) {
	_weight = weight;
    }

}
