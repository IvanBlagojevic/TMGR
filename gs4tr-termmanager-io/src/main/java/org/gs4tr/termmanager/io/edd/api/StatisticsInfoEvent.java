package org.gs4tr.termmanager.io.edd.api;

import java.util.Set;

public interface StatisticsInfoEvent<T> extends Event {

    Set<T> getStatisticsInfo();
}
