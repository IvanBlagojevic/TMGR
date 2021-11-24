package org.gs4tr.termmanager.solr.plugin.utils;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.digest.DigestUtils;

public class ChecksumHelper {

    public static String makeChecksum(String text) {
	try {
	    return DigestUtils.md5Hex(text.getBytes(StandardCharsets.UTF_8));
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }
}
