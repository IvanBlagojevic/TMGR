package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;

public interface SystemManualTaskHandler extends ManualTaskHandler {
    boolean getGlobal();

    List<String> getPolicies();

    SelectStyleEnum getSelectStyle();
}
