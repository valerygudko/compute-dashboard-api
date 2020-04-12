package com.techtest.computedashboardapi.model.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;

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

    private final LocalDateTime launchTime;

    public static Comparator<EC2InstanceResponse> sortByIdAsc = Comparator.comparing(EC2InstanceResponse::getId);
    public static Comparator<EC2InstanceResponse> sortByIdDesc = sortByIdAsc.reversed();

    public static Comparator<EC2InstanceResponse> sortByNameAsc = Comparator.comparing(EC2InstanceResponse::getName);
    public static Comparator<EC2InstanceResponse> sortByNameDesc = sortByNameAsc.reversed();

    public static Comparator<EC2InstanceResponse> sortByTypeAsc = Comparator.comparing(EC2InstanceResponse::getType);
    public static Comparator<EC2InstanceResponse> sortByTypeDesc = sortByTypeAsc.reversed();

    public static Comparator<EC2InstanceResponse> sortByStateAsc = Comparator.comparing(EC2InstanceResponse::getState);
    public static Comparator<EC2InstanceResponse> sortByStateDesc = sortByStateAsc.reversed();

    public static Comparator<EC2InstanceResponse> sortByMonitoringAsc = Comparator.comparing(EC2InstanceResponse::getMonitoring);
    public static Comparator<EC2InstanceResponse> sortByMonitoringDesc = sortByMonitoringAsc.reversed();

    public static Comparator<EC2InstanceResponse> sortByAzAsc = Comparator.comparing(EC2InstanceResponse::getAz);
    public static Comparator<EC2InstanceResponse> sortByAzDesc = sortByAzAsc.reversed();

    public static Comparator<EC2InstanceResponse> sortByPublicIPAsc = Comparator.comparing(EC2InstanceResponse::getPublicIP);
    public static Comparator<EC2InstanceResponse> sortByPublicIPDesc = sortByPublicIPAsc.reversed();

    public static Comparator<EC2InstanceResponse> sortByPrivateIPAsc = Comparator.comparing(EC2InstanceResponse::getPrivateIP);
    public static Comparator<EC2InstanceResponse> sortByPrivateIPDesc = sortByPrivateIPAsc.reversed();

    public static Comparator<EC2InstanceResponse> sortBySubnetIdAsc = Comparator.comparing(EC2InstanceResponse::getSubnetId);
    public static Comparator<EC2InstanceResponse> sortBySubnetIdDesc = sortBySubnetIdAsc.reversed();

    public static Comparator<EC2InstanceResponse> sortByLaunchTimeAsc = Comparator.comparing(EC2InstanceResponse::getLaunchTime);
    public static Comparator<EC2InstanceResponse> sortByLaunchTimeDesc = sortByLaunchTimeAsc.reversed();
}
