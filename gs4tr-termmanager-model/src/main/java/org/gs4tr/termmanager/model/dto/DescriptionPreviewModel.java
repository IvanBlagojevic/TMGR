package org.gs4tr.termmanager.model.dto;

import java.util.ArrayList;
import java.util.List;

public class DescriptionPreviewModel {

    private String _type;

    private List<String> _values = new ArrayList<String>();

    public String getType() {
	return _type;
    }

    public List<String> getValues() {
	return _values;
    }

    public void setType(String type) {
	_type = type;
    }

    public void setValues(List<String> values) {
	_values = values;
    }

}
