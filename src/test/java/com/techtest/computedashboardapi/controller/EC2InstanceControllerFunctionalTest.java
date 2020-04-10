package com.techtest.computedashboardapi.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.amazonaws.regions.Regions.DEFAULT_REGION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @DisplayName("Valid request must be accepted")
    void getEc2Instances_validRegionRequest_returns200() throws Exception {
        // given when then
        mockMvc.perform(get(PATH)
                .param(REGION, DEFAULT_REGION.getName())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

}