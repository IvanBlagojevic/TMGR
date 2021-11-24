package org.gs4tr.termmanager.tests.controllers.rest;

import org.gs4tr.termmanager.tests.AbstractSolrGlossaryTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public abstract class BaseRestControllerTest extends AbstractSolrGlossaryTest {

    protected String loginGet(String username, String password) throws Exception {

        MockHttpServletRequestBuilder get = get("/rest/login");
        get.servletPath("/rest/login");
        get.param("username", username);
        get.param("password", password);

        ResultActions resultActions = _mockMvc.perform(get);

        String userId = resultActions.andReturn().getResponse().getContentAsString();

        return userId;
    }

    protected String loginPost(String username, String password) throws Exception {

        MockHttpServletRequestBuilder post = post("/rest/login");
        post.servletPath("/rest/login");
        post.content("username=" + username + "&password=" + password);
        post.contentType(MediaType.TEXT_PLAIN);

        ResultActions resultActions = _mockMvc.perform(post);

        String userId = resultActions.andReturn().getResponse().getContentAsString();

        return userId;
    }

}