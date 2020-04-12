package com.techtest.computedashboardapi.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.techtest.computedashboardapi.utils.TestConstants.*;
import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class WebControllerFunctionalTest {

    private static final String REDIRECT_URL_TO_OKTA = "http://localhost/oauth2/authorization/okta";
    private static final String HOME_PATH = "/";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("All requests must be redirected to Okta for oauth")
    void home_unauthorizedRequestsNotPermitted() throws Exception {
        mockMvc.perform(get(HOME_PATH))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECT_URL_TO_OKTA));
    }

    @Test
    @WithMockUser(username = USER_NAME, password = PASSWORD, roles = USER_ROLE)
    @DisplayName("Authorized user can access home api")
    void home_authorizedRequestToHomeApi_returnsPersonalGreeting() throws Exception {
        mockMvc.perform(get(HOME_PATH))
                .andExpect(status().isOk())
        .andExpect(content().string(format("Welcome, %s", USER_NAME)));
    }

}
