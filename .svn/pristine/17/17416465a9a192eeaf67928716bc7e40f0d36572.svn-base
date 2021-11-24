package org.gs4tr.termmanager.webservice.controllers;

import static org.gs4tr.termmanager.cache.model.CacheName.WS_V2_USERS_SESSIONS;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.OK;

import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.webservice.model.request.BaseCommand;
import org.gs4tr.termmanager.webservice.model.response.BaseResponse;
import org.gs4tr.termmanager.webservice.model.response.ErrorResponse;
import org.gs4tr.termmanager.webservice.model.response.LogoutResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class provide post logout method to logout from Term Manager.
 * 
 * @author TMGR_Backend
 */

@RequestMapping("/rest/v2/logout")
@RestController("WSLogoutController")
@Api(value = "Logout")
public class LogoutController {

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, Authentication> _cacheGateway;

    @Autowired
    private SessionService _sessionService;

    public SessionService getSessionService() {
	return _sessionService;
    }

    @ApiResponses(value = {
	    @ApiResponse(code = 200, message = "Successful operation.", response = LogoutResponse.class),
	    @ApiResponse(code = 400, message = "Missing required parameter/Invalid parameter.", response = ErrorResponse.class),
	    @ApiResponse(code = 500, message = "Internal server error.") })
    @ApiOperation(value = "Use this method to logout from Term Manager.", httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @LogEvent(action = TMGREventActionConstants.ACTION_LOGOUT, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST_V2)
    public BaseResponse logout(@RequestBody BaseCommand command) {
	getSessionService().logout();
	removeAuthentication(command);
	return new LogoutResponse(OK, true);
    }

    private CacheGateway<String, Authentication> getCacheGateway() {
	return _cacheGateway;
    }

    private void removeAuthentication(BaseCommand command) {
	String securityTicket = command.getSecurityTicket();
	getCacheGateway().remove(WS_V2_USERS_SESSIONS, securityTicket);
    }
}
