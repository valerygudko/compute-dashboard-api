package com.techtest.computedashboardapi.utils;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.InstanceType;

import java.time.LocalDateTime;

public class TestConstants {

    public static final String DEFAULT_REGION_NAME = Region.US_WEST_2.toString();
    public static final String NAME_KEY = "Name";
    public static final String NAME = "Test";
    public static final String ID = "1A";
    public static final String INSTANCE_TYPE = InstanceType.C1_MEDIUM.toString();
    public static final String STATE = "Some_state";
    public static final String MONITORING = "Enabled?";
    public static final String AZ = "Availability Zone";
    public static final String PUBLIC_IP = "0.0.0.0";
    public static final String PRIVATE_IP = "0.0.0.1";
    public static final String SUBNET_ID = "Subnet_id";
    public static final LocalDateTime LAUNCH_TIME = LocalDateTime.now();
    public static final String UTC = "UTC";
    public static final String USER_NAME = "user1";
    public static final String PASSWORD = "pwd";
    public static final String USER_ROLE = "USER";
}
