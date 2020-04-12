package com.techtest.computedashboardapi.service;

import com.techtest.computedashboardapi.exception.RequestParsingException;
import com.techtest.computedashboardapi.model.request.SortRequest;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;

import java.util.List;

public interface SortService {
    List<EC2InstanceResponse> sort(List<EC2InstanceResponse> responseList, SortRequest sortRequest) throws RequestParsingException;
}
