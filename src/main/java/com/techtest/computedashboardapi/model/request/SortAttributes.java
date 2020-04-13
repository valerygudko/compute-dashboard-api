package com.techtest.computedashboardapi.model.request;

import io.swagger.annotations.ApiModel;

@ApiModel
public enum SortAttributes {
    name, id, type, state, monitoring, az, publicIP, privateIP, subnetId, launchTime
}