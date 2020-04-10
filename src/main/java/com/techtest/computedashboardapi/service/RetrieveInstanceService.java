package com.techtest.computedashboardapi.service;

import com.techtest.computedashboardapi.exception.CommunicationFailedException;
import com.techtest.computedashboardapi.exception.RequestParsingException;
import com.techtest.computedashboardapi.exception.ResponseParsingException;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;

import java.util.List;

public interface RetrieveInstanceService {

    List<EC2InstanceResponse> getEc2Instances(String region) throws RequestParsingException, ResponseParsingException, CommunicationFailedException;
}
