package org.gs4tr.termmanager.webservice.streamers;

import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.OK;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ProjectMetadataStreamer extends ResponseStreamer {

    private static final String PROJECT_METADATA_RESPONSE_HEADER = "{\"returnCode\":" + OK
	    + ",\"success\":true,\"projectMetadata\":[";

    public ProjectMetadataStreamer(OutputStream outputStream) {
	super(outputStream);
    }

    public void writeResponseHeader() throws Exception {
	getOutputStream().write(PROJECT_METADATA_RESPONSE_HEADER.getBytes(StandardCharsets.UTF_8));
    }
}
