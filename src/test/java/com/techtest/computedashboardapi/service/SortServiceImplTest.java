package com.techtest.computedashboardapi.service;

import com.techtest.computedashboardapi.exception.RequestParsingException;
import com.techtest.computedashboardapi.model.request.Sort;
import com.techtest.computedashboardapi.model.request.SortAttributes;
import com.techtest.computedashboardapi.model.request.SortDirection;
import com.techtest.computedashboardapi.model.request.SortRequest;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SortServiceImplTest {

    private static final String DELIMITER = ",";

    @Mock
    private SortRequest sortRequest;

    @Mock
    private Sort sort;

    private SortServiceImpl testObj = new SortServiceImpl();

    private EC2InstanceResponse one = EC2InstanceResponse.builder().id("5").name("Old").build();
    private EC2InstanceResponse two =  EC2InstanceResponse.builder().id("1").name("VeryOld").build();
    private EC2InstanceResponse three = EC2InstanceResponse.builder().id("4").name("Old").build();

    private List<EC2InstanceResponse> ec2InstanceResponseList = Arrays.asList(one, two, three);

    @ParameterizedTest(name = "Get RequestParsingException for sorting parameters with attribute: {0} and order: {1}")
    @DisplayName("Empty sorting parameters must not be accepted")
    @CsvSource({",acs", "name,", ","})
    void sort_emptySortingParams_RequestParsingExceptionThrown(String attr, String order) {
        //given
        given(sortRequest.getSort()).willReturn(sort);
        lenient().when(sort.getOrder()).thenReturn(order);
        lenient().when(sort.getAttr()).thenReturn(attr);

        //when then
        assertThrows(RequestParsingException.class, () -> testObj.sort(ec2InstanceResponseList, sortRequest), "Empty sorting attribute or order are not allowed");
    }

    @Test
    @DisplayName("Invalid length of sorting parameters must not be accepted")
    void sort_invalidLengthSortingParams_RequestParsingExceptionThrown() {
        //given
        given(sortRequest.getSort()).willReturn(sort);
        lenient().when(sort.getOrder()).thenReturn(SortDirection.asc.name());
        lenient().when(sort.getAttr()).thenReturn("name,type");

        //when then
        assertThrows(RequestParsingException.class, () -> testObj.sort(ec2InstanceResponseList, sortRequest), "Invalid length of sorting parameters passed");
    }

    @DisplayName("Invalid sorting attribute value passed must not be accepted")
    @ParameterizedTest(name = "Get RequestParsingExceptionThrown for sorting attribute value of: {0}")
    @ValueSource(strings = {"typO", "whatever"})
    void sort_invalidSortingAttributeValue_RequestParsingExceptionThrown(String attr) {
        //given
        given(sortRequest.getSort()).willReturn(sort);
        lenient().when(sort.getOrder()).thenReturn(SortDirection.asc.name());
        lenient().when(sort.getAttr()).thenReturn(attr);

        //when then
        assertThrows(RequestParsingException.class, () -> testObj.sort(ec2InstanceResponseList, sortRequest), "Invalid sorting attribute value passed");
    }

    @Test
    @DisplayName("Invalid sorting order value must not be accepted")
    void sort_invalidSortingOrderValue_RequestParsingExceptionThrown() {
        //given
        given(sortRequest.getSort()).willReturn(sort);
        lenient().when(sort.getOrder()).thenReturn("WRONG");
        lenient().when(sort.getAttr()).thenReturn(SortAttributes.monitoring.name());

        //when then
        assertThrows(RequestParsingException.class, () -> testObj.sort(ec2InstanceResponseList, sortRequest), "Invalid sorting order value passed");
    }

    @Test
    @DisplayName("Valid sorting request with single sorting param must be processed correctly")
    void sort_validSortingRequest_returnSortedList() throws RequestParsingException {
        //given
        given(sortRequest.getSort()).willReturn(sort);
        lenient().when(sort.getOrder()).thenReturn(SortDirection.desc.name());
        lenient().when(sort.getAttr()).thenReturn(SortAttributes.name.name());

        //when
        List<EC2InstanceResponse> result = testObj.sort(ec2InstanceResponseList, sortRequest);

        // then
        assertThat(result.size()).isEqualTo(ec2InstanceResponseList.size());
        assertThat(result.get(0)).isEqualToComparingFieldByField(two);
    }

    @Test
    @DisplayName("Valid sorting request with multiple sorting attributes must be processed correctly")
    void sort_validSortingRequest_multipleAttributes_returnSortedList() throws RequestParsingException {
        //given
        given(sortRequest.getSort()).willReturn(sort);
        lenient().when(sort.getOrder()).thenReturn(Strings.concat(SortDirection.desc.name(), DELIMITER, SortDirection.asc.name()));
        lenient().when(sort.getAttr()).thenReturn(Strings.concat(SortAttributes.name.name(), DELIMITER,  SortAttributes.id.name()));

        //when
        List<EC2InstanceResponse> result = testObj.sort(ec2InstanceResponseList, sortRequest);

        // then
        assertThat(result.size()).isEqualTo(ec2InstanceResponseList.size());
        assertThat(result.get(0)).isEqualToComparingFieldByField(two);
        assertThat(result.get(1)).isEqualToComparingFieldByField(three);
        assertThat(result.get(2)).isEqualToComparingFieldByField(one);
    }

}