package com.techtest.computedashboardapi.mapper;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;
import com.techtest.computedashboardapi.exception.ResponseParsingException;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class EC2InstanceResponseMapper {

    public List<EC2InstanceResponse> mapEc2InstanceResponse(List<Instance> ec2InstancesResponse) throws ResponseParsingException {
        try {
            return ec2InstancesResponse.stream().map(instance -> EC2InstanceResponse.builder()
                    .name(instance.getTags().stream().findFirst().map(Tag::getValue).orElse(""))
                    .id(instance.getInstanceId())
                    .az(instance.getPlacement().getAvailabilityZone())
                    .type(instance.getInstanceType())
                    .state(instance.getState().getName())
                    .monitoring(instance.getMonitoring().getState())
                    .publicIP(instance.getPublicIpAddress())
                    .privateIP(instance.getPrivateIpAddress())
                    .subnetId(instance.getSubnetId())
                    .launchTime(instance.getLaunchTime())
                    .build())
                    .collect(toList());
        } catch (NullPointerException e) {
            throw new ResponseParsingException(e);
        }
    }

}
