package org.gs4tr.termmanager.service.counter;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;
import org.springframework.stereotype.Component;

@Component("strategyTermCounter")
public class StrategyTermCounter {

    public static final String DISABLED = "DISABLED";

    private static final Map<String, TermCounter> TERM_COUNTER_MAP = new HashMap<String, TermCounter>();

    @PostConstruct
    public void populateTermCounterMap() {
	TERM_COUNTER_MAP.put(ItemStatusTypeHolder.PROCESSED.getName(), new ProcessedCounterImpl());
	TERM_COUNTER_MAP.put(ItemStatusTypeHolder.BLACKLISTED.getName(), new BlacklistedCounterImpl());
	TERM_COUNTER_MAP.put(ItemStatusTypeHolder.WAITING.getName(), new DemoteCounterImpl());
	TERM_COUNTER_MAP.put(ItemStatusTypeHolder.ON_HOLD.getName(), new OnHoldCounterImpl());
	TERM_COUNTER_MAP.put(DISABLED, new DisableCounterImpl());
    }

    public void updateTermCount(ProjectDetailInfo detailInfo, String newStatus, Term term, String currentStatus) {
	if (StringUtils.isNotBlank(newStatus) && !newStatus.equals(currentStatus)) {
	    TermCounter termCounter = getTermCounterMap().get(newStatus);
	    termCounter.updateTermCount(detailInfo, term);
	}
    }

    private Map<String, TermCounter> getTermCounterMap() {
	return TERM_COUNTER_MAP;
    }
}
