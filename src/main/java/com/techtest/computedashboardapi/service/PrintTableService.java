package com.techtest.computedashboardapi.service;

import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;

import java.util.List;

public interface PrintTableService {

    void print(List<EC2InstanceResponse> responseList);
}
