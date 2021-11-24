package org.gs4tr.termmanager.webservice.streamers;

import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.OK;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SegmentHitStreamer extends ResponseStreamer {

    private static final String _START_JSON_RESPONSE_PART = "{\"returnCode\":" + OK
	    + ",\"success\":true,\"batchSegmentTermHits\":[";

    public SegmentHitStreamer(OutputStream outputStream) {
	super(outputStream);
    }

    public void writeResponseHeader() throws Exception {
	getOutputStream().write(_START_JSON_RESPONSE_PART.getBytes(StandardCharsets.UTF_8));
    }

}
