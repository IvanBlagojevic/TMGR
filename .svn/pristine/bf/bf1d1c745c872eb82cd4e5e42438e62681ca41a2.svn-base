package org.gs4tr.termmanager.webservice.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation3.solr.IMatchPostProcessor;
import org.gs4tr.foundation3.solr.analyzer.LuceneAnalyzerBuilder;
import org.gs4tr.foundation3.solr.highlight.MatchPostProcessor;
import org.gs4tr.foundation3.solr.model.highlight.MatchPostProcessorConfiguration;
import org.gs4tr.foundation3.solr.model.highlight.MatchPostProcessorContext;
import org.gs4tr.foundation3.solr.model.highlight.MatchPostProcessorType;
import org.gs4tr.foundation3.solr.model.highlight.MatchRange;
import org.gs4tr.foundation3.solr.model.highlight.TagType;
import org.gs4tr.termmanager.webservice.model.response.SegmentHitRange;
import org.gs4tr.termmanager.webservice.model.response.SegmentTermHit;

public class TermRecognitionUtils {

    public static void highlightMatchedSegments(List<SegmentTermHit> hits, String segment, boolean fuzzy) {

	if (CollectionUtils.isEmpty(hits)) {
	    return;
	}

	char[] segmentChars = segment.toLowerCase().toCharArray();

	for (SegmentTermHit hit : hits) {
	    String matchText = hit.getTermHit();
	    List<MatchRange> ranges = getHighlightRange(matchText, Locale.makeLocale(hit.getLocale()), segment, fuzzy);

	    if (CollectionUtils.isEmpty(ranges)) {
		continue;
	    }

	    List<SegmentHitRange> hitRanges = new ArrayList<>();

	    for (MatchRange matchRange : ranges) {
		boolean exactMatch = checkIsExact(matchRange, segmentChars, matchText);

		SegmentHitRange hitRange = new SegmentHitRange(matchRange.getStart(),
			matchRange.getEnd() - matchRange.getStart());
		hitRange.setFuzzy(!exactMatch);
		hitRanges.add(hitRange);
	    }

	    hit.setRanges(hitRanges);
	}
    }

    private static boolean checkIsExact(MatchRange range, char[] segmentChars, String matchText) {

	char[] matchChars = matchText.toLowerCase().toCharArray();

	if (matchChars.length == (range.getEnd() - range.getStart())) {
	    for (int i = range.getStart(); i < range.getEnd(); i++) {

		if (segmentChars[i] != matchChars[i - range.getStart()]) {
		    return false;
		}
	    }
	    return true;
	}
	return false;
    }

    private static List<MatchRange> getHighlightRange(String matchText, Locale locale, String segment, boolean fuzzy) {

	List<MatchRange> ranges = new ArrayList<>();

	IMatchPostProcessor processor = initSubdocumentPostProcessor(locale, fuzzy);
	MatchPostProcessorContext context = new MatchPostProcessorContext();
	processor.match(segment);
	processor.query(matchText).process(context);

	if (!context.getInfos().isEmpty()) {
	    Map<MatchRange, MutableInt> infos = context.getInfos();
	    for (Map.Entry<MatchRange, MutableInt> entry : infos.entrySet()) {
		MatchRange range = entry.getKey();
		ranges.add(range);
	    }
	}
	return ranges;

    }

    private static final IMatchPostProcessor initSubdocumentPostProcessor(Locale locale, boolean fuzzy) {

	MatchPostProcessorConfiguration conf = new MatchPostProcessorConfiguration();
	conf.setLocale(locale);
	conf.setScore(true);
	conf.setType(MatchPostProcessorType.SUBDOCUMENT);
	conf.enable(TagType.PHRASE);
	conf.enable(TagType.EXACT);
	conf.enable(TagType.FOLDEXACT);

	if (fuzzy) {
	    if (LuceneAnalyzerBuilder.isStemmingSupported(locale)) {
		conf.enable(TagType.STEM);
		conf.enable(TagType.FOLDSTEM);
	    } else {
		conf.enable(TagType.FUZZY);
		conf.enable(TagType.FOLDFUZZY);
	    }
	}

	return MatchPostProcessor.newInstance(conf);
    }
}
