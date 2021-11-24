package org.gs4tr.termmanager.service.impl;

import java.util.UUID;

import org.gs4tr.termmanager.service.UuidGeneratorService;
import org.springframework.stereotype.Service;

@Service("uuidGeneratorService")
public class UuidGeneratorServiceImpl implements UuidGeneratorService {

    @Override
    public String[] generateUUID(int no) {
	String[] uuids = new String[no];
	for (int i = 0; i < uuids.length; i++) {
	    uuids[i] = UUID.randomUUID().toString();
	}
	return uuids;
    }
}
