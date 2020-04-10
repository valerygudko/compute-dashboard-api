package com.techtest.computedashboardapi.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.techtest.computedashboardapi.exception.RequestParsingException;
import com.techtest.computedashboardapi.exception.ResponseParsingException;
import com.techtest.computedashboardapi.mapper.EC2InstanceResponseMapper;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RetrieveInstanceServiceImpl implements RetrieveInstanceService {

    private static Integer RUNNING_STATE_CODE = 16;

    private ObjectProvider<AmazonEC2> ec2ClientProvider;

    private EC2InstanceResponseMapper mapper;

    public RetrieveInstanceServiceImpl(EC2InstanceResponseMapper mapper, ObjectProvider<AmazonEC2> ec2ClientProvider){
        this.mapper = mapper;
        this.ec2ClientProvider = ec2ClientProvider;
    }

    public List<EC2InstanceResponse> getEc2Instances(String region) throws RequestParsingException, ResponseParsingException {

        Regions regionName = getRegionByName(region);

        AmazonEC2 ec2Client = ec2ClientProvider.getObject(regionName.getName());

        List<Instance> instancesResponse = getEC2Instances(ec2Client);

        log.info("Found {} running ec2 instances", instancesResponse.size());

        return mapper.mapEc2InstanceResponse(instancesResponse);
    }

    private Regions getRegionByName(String region) throws RequestParsingException {
        return Arrays.stream(Regions.values())
                    .filter(e -> e.getName().equals(region))
                    .findFirst()
                    .orElseThrow(() -> new RequestParsingException(new IllegalStateException(String.format("Unsupported parameter %s.", region))));
    }

    private List<Instance> getEC2Instances(AmazonEC2 ec2Client) {
        return ec2Client.describeInstances(new DescribeInstancesRequest())
                    .getReservations()
                    .stream()
                    .map(Reservation::getInstances)
                    .flatMap(List::stream)
                    .filter(instance -> instance.getState().getCode().equals(RUNNING_STATE_CODE))
                    .collect(Collectors.toList());
    }

}
