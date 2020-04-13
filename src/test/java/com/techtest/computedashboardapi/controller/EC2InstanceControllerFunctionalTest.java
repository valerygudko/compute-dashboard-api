package com.techtest.computedashboardapi.controller;

import com.techtest.computedashboardapi.model.request.SortAttributes;
import com.techtest.computedashboardapi.model.request.SortDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.techtest.computedashboardapi.utils.TestConstants.*;
import static com.techtest.computedashboardapi.utils.TestConstants.USER_ROLE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
class EC2InstanceControllerFunctionalTest {

    private static final String PATH = "/ec2-instances";
    private static final String REGION = "region";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("All requests are redirected to Okta for oauth")
    void home_requestsRedirectedToOkta() throws Exception {
        // given when then
        mockMvc.perform(get(PATH)
                .secure(true)
                .param(REGION, DEFAULT_REGION_NAME)
                .param("page", String.valueOf(1))
                .param("size", String.valueOf(5))
                .param("sort.attr", SortAttributes.state.name())
                .param("sort.order", SortDirection.desc.name()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Valid request must be accepted")
    @WithMockUser(username = USER_NAME, password = PASSWORD, roles = USER_ROLE)
    void getEc2Instances_validRegionRequest_returns200() throws Exception {
        // given when then
        mockMvc.perform(get(PATH)
                .secure(true)
                .param(REGION, DEFAULT_REGION_NAME)
                .param("page", String.valueOf(1))
                .param("size", String.valueOf(5))
                .param("sort.attr", SortAttributes.state.name())
                .param("sort.order", SortDirection.desc.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
    }

    @DisplayName("Request with invalid sorting parameters is a bad request")
    @ParameterizedTest(name = "Get 400 for sorting parameters with attribute: {0} and order: {1}")
    @CsvSource({",acs", "id,5", "name1,DESC", "name,"})
    @WithMockUser(username = USER_NAME, password = PASSWORD, roles = USER_ROLE)
    void getEc2Instances_validRegion_invalidSortRequest_returns400(String sortAttr, String sortOrder) throws Exception {

        // when then
        mockMvc.perform(get(PATH).param(REGION, DEFAULT_REGION_NAME)
                .secure(true)
                .param("sort.attr", sortAttr)
                .param("sort.order", sortOrder))
                .andExpect(status().isBadRequest());

    }

}