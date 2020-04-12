package com.techtest.computedashboardapi.service;

//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.ec2.AmazonEC2;
//import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
//import com.amazonaws.services.ec2.model.Reservation;

import com.techtest.computedashboardapi.exception.CommunicationFailedException;
import com.techtest.computedashboardapi.exception.RequestParsingException;
import com.techtest.computedashboardapi.exception.ResponseParsingException;
import com.techtest.computedashboardapi.mapper.EC2InstanceResponseMapper;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RetrieveInstanceServiceImpl implements RetrieveInstanceService {

    private static Integer RUNNING_STATE_CODE = 16;

    private ObjectProvider<Ec2Client> ec2ClientProvider;

    private EC2InstanceResponseMapper mapper;

    public RetrieveInstanceServiceImpl(EC2InstanceResponseMapper mapper, ObjectProvider<Ec2Client> ec2ClientProvider){
        this.mapper = mapper;
        this.ec2ClientProvider = ec2ClientProvider;
    }

    public List<EC2InstanceResponse> getEc2Instances(String region) throws RequestParsingException, ResponseParsingException, CommunicationFailedException {

        Ec2Client ec2Client = ec2ClientProvider.getObject(getRegionByName(region));
        List<Instance> instancesResponse = getEC2Instances(ec2Client);
        log.info("Found {} running ec2 instances", instancesResponse.size());

        return mapper.mapEc2InstanceResponse(instancesResponse);
    }

    private Region getRegionByName(String region) throws RequestParsingException {
        Region regionValue = Region.of(region);
        if (regionValue.metadata() != null){
            return regionValue;
        } else {
            throw new RequestParsingException(new IllegalStateException(String.format("Unsupported parameter %s.", region)));
        }
    }

    private List<Instance> getEC2Instances(Ec2Client ec2Client) throws CommunicationFailedException {

        String nextToken = null;
        List<Instance> result = new ArrayList<>();

        try {

            do {
                DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(6).nextToken(nextToken).build();
                DescribeInstancesResponse response = ec2Client.describeInstances(request);

                for (Reservation reservation : response.reservations()) {
                    for (Instance instance : reservation.instances()) {
                        if (instance.state().code().equals(RUNNING_STATE_CODE)){
                            result.add(instance);
                        }
                    }
                }
                nextToken = response.nextToken();
            } while (nextToken != null);

        } catch (Ec2Exception e) {
            throw new CommunicationFailedException(e.getCause());
        }
        return result;
    }

}
