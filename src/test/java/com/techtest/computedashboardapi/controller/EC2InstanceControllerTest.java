package com.techtest.computedashboardapi.controller;

import com.techtest.computedashboardapi.exception.CommunicationFailedException;
import com.techtest.computedashboardapi.exception.RequestParsingException;
import com.techtest.computedashboardapi.model.request.PageRequest;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import com.techtest.computedashboardapi.service.PrintTableService;
import com.techtest.computedashboardapi.service.RetrieveInstanceService;
import com.techtest.computedashboardapi.utils.TestLogging;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;

import java.util.ArrayList;
import java.util.List;

import static com.techtest.computedashboardapi.utils.TestConstants.*;
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
@WithMockUser(username = USER_NAME, password = PASSWORD, roles = USER_ROLE)
class EC2InstanceControllerTest extends TestLogging {

    private static final String PATH = "/ec2-instances";
    private static final String REGION = "region";
    private static final String LOG_MESSAGE = "Calling the service to retrieve info from AWS for region %s";

    @MockBean
    private RetrieveInstanceService retrieveInstanceService;

    @MockBean
    private PrintTableService printTableService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Request with valid region and no specific page request must be accepted")
    void getEc2Instances_validRegion_noPageRequest_returns200() throws Exception {
        // given
        List<EC2InstanceResponse> ec2InstanceResponseList = new ArrayList<EC2InstanceResponse>();
        given(retrieveInstanceService.getEc2Instances(eq(DEFAULT_REGION_NAME), any(PageRequest.class))).willReturn(ec2InstanceResponseList);

        // when then
        mockMvc.perform(get(PATH).param(REGION, DEFAULT_REGION_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(retrieveInstanceService).getEc2Instances(eq(DEFAULT_REGION_NAME), any(PageRequest.class));
        verify(printTableService).print(ec2InstanceResponseList);

        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, DEFAULT_REGION_NAME))).isTrue();
    }

    @Test
    @DisplayName("Request with valid region and valid page request must be accepted")
    void getEc2Instances_validRegion_validPageRequest_returns200() throws Exception {
        // given
        List<EC2InstanceResponse> ec2InstanceResponseList = new ArrayList<EC2InstanceResponse>();
        int page_number = 1;
        int page_size = 6;
        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        given(retrieveInstanceService.getEc2Instances(eq(DEFAULT_REGION_NAME), any(PageRequest.class))).willReturn(ec2InstanceResponseList);

        // when then
        mockMvc.perform(get(PATH).param(REGION, DEFAULT_REGION_NAME)
        .param("page", String.valueOf(page_number))
        .param("size", String.valueOf(page_size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

        verify(retrieveInstanceService).getEc2Instances(eq(DEFAULT_REGION_NAME), pageRequestArgumentCaptor.capture());
        assertThat(pageRequestArgumentCaptor.getValue().getPage()).isEqualTo(page_number);
        assertThat(pageRequestArgumentCaptor.getValue().getSize()).isEqualTo(page_size);
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

        verify(retrieveInstanceService, never()).getEc2Instances(anyString(), any(PageRequest.class));
        verify(printTableService, never()).print(anyList());
    }

    @DisplayName("Request with invalid or empty value of region is a bad request")
    @ParameterizedTest(name = "Get 400 for specified region value of: {0}")
    @ValueSource(strings = {"", "whatever"})
    void getEc2Instances_invalidOrEmptyRegion_returns400(String region) throws Exception {
        // given
        given(retrieveInstanceService.getEc2Instances(eq(region), any(PageRequest.class))).willThrow(new RequestParsingException(new IllegalStateException(format("Unsupported parameter %s.", region))));

        // when then
        mockMvc.perform(get(PATH).param(REGION, region))
                .andExpect(status().isBadRequest());

        verify(retrieveInstanceService).getEc2Instances(eq(region), any(PageRequest.class));
        verify(printTableService, never()).print(anyList());

        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, region))).isTrue();
    }

    @DisplayName("Request with invalid page request is a bad request")
    @ParameterizedTest(name = "Get 400 for specified page request with size of: {0} and page number of: {1}")
    @CsvSource({"null,5", "-1,5", "0,5", "1,3", "1,1001"})
    void getEc2Instances_validRegion_invalidPageRequest_returns400(String pageSize, String pageNumber) throws Exception {

        // when then

        mockMvc.perform(get(PATH).param(REGION, DEFAULT_REGION_NAME)
                .param("page", pageNumber)
                .param("size", pageSize))
                .andExpect(status().isBadRequest());

        verify(retrieveInstanceService, never()).getEc2Instances(eq(DEFAULT_REGION_NAME), any(PageRequest.class));
        verify(printTableService, never()).print(anyList());

        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, DEFAULT_REGION_NAME))).isTrue();
    }

    @Test
    @DisplayName("500 exception returned when AmazonEC2Exception thrown")
    void getEc2Instances_AmazonEC2ExceptionThrown_returns500() throws Exception {
        // given
        given(retrieveInstanceService.getEc2Instances(eq(DEFAULT_REGION_NAME), any(PageRequest.class))).willThrow(new CommunicationFailedException(Ec2Exception.builder().message("Error").build()));

        // when then
        mockMvc.perform(get(PATH).param(REGION, DEFAULT_REGION_NAME))
                .andExpect(status().is5xxServerError());

        verify(retrieveInstanceService).getEc2Instances(eq(DEFAULT_REGION_NAME), any(PageRequest.class));
        verify(printTableService, never()).print(anyList());

        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, DEFAULT_REGION_NAME))).isTrue();
    }

}