package org.gs4tr.termmanager.webservice.streamers;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseStreamer {

    private static final String CLOSE_BRACKET = "}";

    private static final String COMA_CHARACTER = ",";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String _END_JSON_RESPONSE_PART = "],\"time\":";

    private OutputStream _outputStream;

    ResponseStreamer(OutputStream outputStream) {
	setOutputStream(new BufferedOutputStream(outputStream));
    }

    public void closeOutputStream() throws Exception {
	_outputStream.flush();
	_outputStream.close();
	_outputStream = null;
    }

    public OutputStream getOutputStream() {
	return _outputStream;
    }

    public void writeFooterAndCloseOutputStream(long startTime, long endTime) throws Exception {
	String footer = _END_JSON_RESPONSE_PART + getStreamingTime(startTime, endTime) + CLOSE_BRACKET;
	getOutputStream().write(footer.getBytes(StandardCharsets.UTF_8));
	closeOutputStream();
    }

    public void writeResponseBody(Object body, int currentSegmentNumber, int numberOfSegments) throws Exception {
	String jsonObject = OBJECT_MAPPER.writeValueAsString(body);
	getOutputStream().write(jsonObject.getBytes(StandardCharsets.UTF_8));
	writeSeparator(currentSegmentNumber, numberOfSegments);
    }

    private long getStreamingTime(long startTime, long endTime) {
	return endTime - startTime;
    }

    private void setOutputStream(OutputStream outputStream) {
	_outputStream = outputStream;
    }

    private void writeSeparator(int currentSegmentNumber, int numberOfSegments) throws Exception {
	if (currentSegmentNumber < numberOfSegments - 1) {
	    getOutputStream().write(COMA_CHARACTER.getBytes(StandardCharsets.UTF_8));
	}
    }
}
