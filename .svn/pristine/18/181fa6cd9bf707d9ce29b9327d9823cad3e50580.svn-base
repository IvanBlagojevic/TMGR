package org.gs4tr.termmanager.webmvc.controllers;

import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.service.impl.ExportAdapter;
import org.gs4tr.termmanager.webmvc.model.commands.CancelDownloadExportCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CancelDownloadExportController extends AbstractController {

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, ExportAdapter> _cacheGateway;

    @RequestMapping(value = "cancelDownloadExport.ter")
    @ResponseBody
    public ModelMapResponse handle(@ModelAttribute CancelDownloadExportCommand command) {

	String threadName = command.getThreadName();

	getCacheGateway().remove(CacheName.EXPORT_PROGRESS_STATUS, threadName);

	return new ModelMapResponse();
    }

    private CacheGateway<String, ExportAdapter> getCacheGateway() {
	return _cacheGateway;
    }

}
