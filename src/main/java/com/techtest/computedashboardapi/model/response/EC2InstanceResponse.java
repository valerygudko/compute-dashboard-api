package com.techtest.computedashboardapi.model.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class EC2InstanceResponse {

    private final String name;

    private final String id;

    private final String type;

    private final String state;

    private final String monitoring;

    private final String az;

    private final String publicIP;

    private final String privateIP;

    private final String subnetId;

    private final Date launchTime;
}
