package com.techtest.computedashboardapi.controller;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.AmazonEC2Exception;
import com.techtest.computedashboardapi.exception.CommunicationFailedException;
import com.techtest.computedashboardapi.exception.RequestParsingException;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import com.techtest.computedashboardapi.service.PrintTableService;
import com.techtest.computedashboardapi.service.RetrieveInstanceService;
import com.techtest.computedashboardapi.utils.TestLogging;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EC2InstanceController.class)
@AutoConfigureMockMvc
@WebAppConfiguration()
class EC2InstanceControllerTest extends TestLogging {

    private static final String PATH = "/ec2-instances";
    private static final String DEFAULT_REGION_NAME = Regions.DEFAULT_REGION.getName();
    private static final String REGION = "region";
    private static final String LOG_MESSAGE = "Calling the service to retrieve info from AWS for region %s";

    @MockBean
    private RetrieveInstanceService retrieveInstanceService;

    @MockBean
    private PrintTableService printTableService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Request with valid region must be accepted")
    void getEc2Instances_validRegion_returns200() throws Exception {
        // given
        List<EC2InstanceResponse> ec2InstanceResponseList = new ArrayList<EC2InstanceResponse>();
        given(retrieveInstanceService.getEc2Instances(DEFAULT_REGION_NAME)).willReturn(ec2InstanceResponseList);

        // when then
        mockMvc.perform(get(PATH).param(REGION, DEFAULT_REGION_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(retrieveInstanceService).getEc2Instances(DEFAULT_REGION_NAME);
        verify(printTableService).print(ec2InstanceResponseList);

        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, DEFAULT_REGION_NAME))).isTrue();
    }

    @Test
    @DisplayName("Request without region specified is a bad request")
    void getEc2Instances_noRegion_returns400() throws Exception {
        // when then
        mockMvc.perform(get(PATH))
                .andExpect(status().isBadRequest());

        verify(retrieveInstanceService, never()).getEc2Instances(anyString());
        verify(printTableService, never()).print(anyList());
    }

    @DisplayName("Request with invalid or empty value of region is a bad request")
    @ParameterizedTest(name = "Get 400 for specified region value of: {0}")
    @ValueSource(strings = {"", "whatever"})
    void getEc2Instances_invalidOrEmptyRegion_returns400(String region) throws Exception {
        // given
        given(retrieveInstanceService.getEc2Instances(region)).willThrow(new RequestParsingException(new IllegalStateException(format("Unsupported parameter %s.", region))));

        // when then
        mockMvc.perform(get(PATH).param(REGION, region))
                .andExpect(status().isBadRequest());

        verify(retrieveInstanceService).getEc2Instances(region);
        verify(printTableService, never()).print(anyList());

        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, region))).isTrue();
    }

    @Test
    @DisplayName("500 exception returned when AmazonEC2Exception thrown")
    void getEc2Instances_AmazonEC2ExceptionThrown_returns500() throws Exception {
        // given
        given(retrieveInstanceService.getEc2Instances(DEFAULT_REGION_NAME)).willThrow(new CommunicationFailedException(new AmazonEC2Exception("Invalid credentials")));

        // when then
        mockMvc.perform(get(PATH).param(REGION, DEFAULT_REGION_NAME))
                .andExpect(status().is5xxServerError());

        verify(retrieveInstanceService).getEc2Instances(DEFAULT_REGION_NAME);
        verify(printTableService, never()).print(anyList());

        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, DEFAULT_REGION_NAME))).isTrue();
    }

}