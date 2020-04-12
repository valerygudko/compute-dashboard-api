package com.techtest.computedashboardapi.service;

import com.techtest.computedashboardapi.exception.CommunicationFailedException;
import com.techtest.computedashboardapi.exception.RequestParsingException;
import com.techtest.computedashboardapi.exception.ResponseParsingException;
import com.techtest.computedashboardapi.mapper.EC2InstanceResponseMapper;
import com.techtest.computedashboardapi.model.request.PageRequest;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import com.techtest.computedashboardapi.utils.TestLogging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RetrieveInstanceServiceImplTest extends TestLogging {

    private static final String LOG_MESSAGE = "Found %d running ec2 instances";
    private static final String DEFAULT_REGION_NAME = Region.US_WEST_2.toString();

    @Mock
    private EC2InstanceResponseMapper ec2InstanceResponseMapper;

    @Mock
    private ObjectProvider<Ec2Client> amazonEC2ObjectProvider;

    @Mock
    private Ec2Client amazonEC2;

    @Mock
    private DescribeInstancesResponse describeInstancesResponse;

    @Mock
    private Reservation reservation;

    @Mock
    private Instance instance;

    @Mock
    private InstanceState instanceState;

    @Mock
    private PageRequest pageRequest;

    private List<EC2InstanceResponse> ec2InstanceResponseList = singletonList(EC2InstanceResponse.builder().build());

    @InjectMocks
    private RetrieveInstanceServiceImpl testObj;

    @BeforeEach
    void setup() {
        testObj = new RetrieveInstanceServiceImpl(ec2InstanceResponseMapper, amazonEC2ObjectProvider);
    }

    @Test
    @DisplayName("List of EC2 instances must be returned for valid region and no page request")
    void getEc2Instances_validRegion_listOfInstancesReturned() throws RequestParsingException, CommunicationFailedException, ResponseParsingException {
        //given
        Integer RUNNING_STATE_CODE = 16;
        given(amazonEC2ObjectProvider.getObject(Region.of(DEFAULT_REGION_NAME))).willReturn(amazonEC2);
        given(ec2InstanceResponseMapper.mapEc2InstanceResponse(anyList())).willReturn(ec2InstanceResponseList);
        given(amazonEC2.describeInstances(any(DescribeInstancesRequest.class))).willReturn(describeInstancesResponse);
        given(describeInstancesResponse.reservations()).willReturn(singletonList(reservation));
        given(describeInstancesResponse.nextToken()).willReturn(null);
        List<Instance> instanceList = singletonList(instance);
        given(reservation.instances()).willReturn(instanceList);
        given(instance.state()).willReturn(instanceState);
        given(instanceState.code()).willReturn(RUNNING_STATE_CODE);

        //when
        List<EC2InstanceResponse> result = testObj.getEc2Instances(DEFAULT_REGION_NAME, new PageRequest());

        //then
        int numberOfRunningInstances = instanceList.size();
        assertThat(result).isEqualTo(ec2InstanceResponseList);
        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, numberOfRunningInstances))).isTrue();
        verify(ec2InstanceResponseMapper).mapEc2InstanceResponse(instanceList);
    }

    @Test
    @DisplayName("When requested page number with requested size has no instances to display an empty list should be returned")
    void getEc2Instances_validRegion_pageWithNoInstances_emptyListReturned() throws RequestParsingException, CommunicationFailedException, ResponseParsingException {
        //given
        given(pageRequest.getSize()).willReturn(6);
        given(pageRequest.getPage()).willReturn(2);
        given(amazonEC2ObjectProvider.getObject(Region.of(DEFAULT_REGION_NAME))).willReturn(amazonEC2);
        given(ec2InstanceResponseMapper.mapEc2InstanceResponse(anyList())).willReturn(ec2InstanceResponseList);
        given(amazonEC2.describeInstances(any(DescribeInstancesRequest.class))).willReturn(describeInstancesResponse);
        given(describeInstancesResponse.nextToken()).willReturn(null);

        //when
        List<EC2InstanceResponse> result = testObj.getEc2Instances(DEFAULT_REGION_NAME, pageRequest);

        //then
        int numberOfRunningInstances = 0;
        assertThat(result).isEqualTo(ec2InstanceResponseList);
        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, numberOfRunningInstances))).isTrue();
        verify(ec2InstanceResponseMapper).mapEc2InstanceResponse(emptyList());
    }

    @Test
    @DisplayName("When list of ec2 instances is greater than a page size than a page should be displayed accordingly")
    void getEc2Instances_validRegion_moreInstancesThanPageSize_firstPage_pageSizeNumberOfInstancesReturned() throws RequestParsingException, CommunicationFailedException, ResponseParsingException {
        //given
        Integer RUNNING_STATE_CODE = 16;
        given(pageRequest.getSize()).willReturn(5);
        given(pageRequest.getPage()).willReturn(1);
        given(amazonEC2ObjectProvider.getObject(Region.of(DEFAULT_REGION_NAME))).willReturn(amazonEC2);
        given(ec2InstanceResponseMapper.mapEc2InstanceResponse(anyList())).willReturn(ec2InstanceResponseList);
        given(amazonEC2.describeInstances(any(DescribeInstancesRequest.class))).willReturn(describeInstancesResponse);
        given(describeInstancesResponse.nextToken()).willReturn("Go_to_next_page").willReturn(null);
        given(describeInstancesResponse.reservations()).willReturn(singletonList(reservation));
        List<Instance> instanceList = Arrays.asList(instance, instance, instance, instance, instance);
        given(reservation.instances()).willReturn(instanceList);
        given(instance.state()).willReturn(instanceState);
        given(instanceState.code()).willReturn(RUNNING_STATE_CODE);

        //when
        List<EC2InstanceResponse> result = testObj.getEc2Instances(DEFAULT_REGION_NAME, pageRequest);

        //then
        int numberOfRunningInstancesOnThePage = 5;
        assertThat(result).isEqualTo(ec2InstanceResponseList);
        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, numberOfRunningInstancesOnThePage))).isTrue();
        verify(ec2InstanceResponseMapper).mapEc2InstanceResponse(instanceList);
    }

    @Test
    @DisplayName("When list of ec2 instances is greater than a page size than appropriate offset should be returned")
    void getEc2Instances_validRegion_moreInstancesThanPageSize_secondPage_offsetIsReturned() throws RequestParsingException, CommunicationFailedException, ResponseParsingException {
        //given
        Integer RUNNING_STATE_CODE = 16;
        given(pageRequest.getSize()).willReturn(5);
        given(pageRequest.getPage()).willReturn(2);
        given(amazonEC2ObjectProvider.getObject(Region.of(DEFAULT_REGION_NAME))).willReturn(amazonEC2);
        given(ec2InstanceResponseMapper.mapEc2InstanceResponse(anyList())).willReturn(ec2InstanceResponseList);
        given(amazonEC2.describeInstances(any(DescribeInstancesRequest.class))).willReturn(describeInstancesResponse);
        given(describeInstancesResponse.nextToken()).willReturn("Go_to_next_page").willReturn(null);
        given(describeInstancesResponse.reservations()).willReturn(singletonList(reservation));
        given(reservation.instances()).willReturn(singletonList(instance));
        given(instance.state()).willReturn(instanceState);
        given(instanceState.code()).willReturn(RUNNING_STATE_CODE);

        //when
        List<EC2InstanceResponse> result = testObj.getEc2Instances(DEFAULT_REGION_NAME, pageRequest);

        //then
        int numberOfRunningInstancesOnThePage = 1;
        assertThat(result).isEqualTo(ec2InstanceResponseList);
        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, numberOfRunningInstancesOnThePage))).isTrue();
        verify(ec2InstanceResponseMapper).mapEc2InstanceResponse(singletonList(instance));
    }

    @Test
    @DisplayName("When there is no EC2 instances found for valid region empty list should be returned")
    void getEc2Instances_validRegion_noInstancesFound_emptyListOfInstancesReturned() throws RequestParsingException, CommunicationFailedException, ResponseParsingException {
        //given
        given(amazonEC2ObjectProvider.getObject(Region.of(DEFAULT_REGION_NAME))).willReturn(amazonEC2);
        given(ec2InstanceResponseMapper.mapEc2InstanceResponse(anyList())).willReturn(ec2InstanceResponseList);
        given(amazonEC2.describeInstances(any(DescribeInstancesRequest.class))).willReturn(describeInstancesResponse);
        given(describeInstancesResponse.reservations()).willReturn(singletonList(reservation));
        given(describeInstancesResponse.nextToken()).willReturn(null);
        List<Instance> instanceList = emptyList();
        given(reservation.instances()).willReturn(instanceList);

        //when
        List<EC2InstanceResponse> result = testObj.getEc2Instances(DEFAULT_REGION_NAME, new PageRequest());

        //then
        int numberOfRunningInstances = 0;
        assertThat(result).isEqualTo(ec2InstanceResponseList);
        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, numberOfRunningInstances))).isTrue();
        verify(ec2InstanceResponseMapper).mapEc2InstanceResponse(emptyList());
    }

    @Test
    @DisplayName("When there is no running EC2 instances for valid region empty list should be returned")
    void getEc2Instances_validRegion_noRunningInstances_emptyListOfInstancesReturned() throws RequestParsingException, CommunicationFailedException, ResponseParsingException {
        //given
        Integer PENDING_STATE_CODE = 0;

        given(amazonEC2ObjectProvider.getObject(Region.of(DEFAULT_REGION_NAME))).willReturn(amazonEC2);
        given(ec2InstanceResponseMapper.mapEc2InstanceResponse(anyList())).willReturn(ec2InstanceResponseList);
        given(amazonEC2.describeInstances(any(DescribeInstancesRequest.class))).willReturn(describeInstancesResponse);
        given(describeInstancesResponse.reservations()).willReturn(singletonList(reservation));
        given(describeInstancesResponse.nextToken()).willReturn(null);
        List<Instance> instanceList = singletonList(instance);
        given(reservation.instances()).willReturn(instanceList);
        given(instance.state()).willReturn(instanceState);
        given(instanceState.code()).willReturn(PENDING_STATE_CODE);

        //when
        List<EC2InstanceResponse> result = testObj.getEc2Instances(DEFAULT_REGION_NAME, new PageRequest());

        //then
        int numberOfRunningInstances = 0;
        assertThat(result).isEqualTo(ec2InstanceResponseList);
        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertThat(messageHasBeenLogged(capture(), format(LOG_MESSAGE, numberOfRunningInstances))).isTrue();
        verify(ec2InstanceResponseMapper).mapEc2InstanceResponse(emptyList());
    }

    @Test
    @DisplayName("RequestParsingException thrown when invalid region specified")
    void getEc2Instances_invalidRegion_RequestParsingExceptionThrown() {

        //when then
        assertThrows(RequestParsingException.class, () -> testObj.getEc2Instances("whatever", new PageRequest()));
    }
}