package org.gs4tr.termmanager.model.pagedlist;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;

public class TmTaskPagedList<T> extends TaskPagedList<T> {

    private static final long serialVersionUID = -5801644923508244165L;

    private Long _totalCountPerSource = Long.valueOf(0);

    private Long _totalCountPerTarget = Long.valueOf(0);

    public TmTaskPagedList(PagedList<T> pagedList) {
	super(pagedList);
    }

    public Long getTotalCountPerSource() {
	return _totalCountPerSource;
    }

    public Long getTotalCountPerTarget() {
	return _totalCountPerTarget;
    }

    public void setTotalCountPerSource(Long totalCountPerSource) {
	_totalCountPerSource = totalCountPerSource;
    }

    public void setTotalCountPerTarget(Long totalCountPerTarget) {
	_totalCountPerTarget = totalCountPerTarget;
    }
}
