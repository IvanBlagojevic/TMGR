package org.gs4tr.termmanager.model.dto.converter;

import java.util.HashMap;
import java.util.Map;

import org.gs4tr.termmanager.model.dto.DateRange;

public class DateRangeConverter {

    public static Map<String, Long> fromInternalToDto(DateRange dateRange) {

	if (dateRange == null) {
	    return null;
	}

	Map<String, Long> dateRangeMap = new HashMap<String, Long>();
	Long fromDate = dateRange.getFromDate();
	Long toDate = dateRange.getToDate();
	dateRangeMap.put("fromDate", fromDate);
	dateRangeMap.put("toDate", toDate);
	return dateRangeMap;
    }
}
