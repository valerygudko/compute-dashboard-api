package com.techtest.computedashboardapi.mapper;

import com.techtest.computedashboardapi.exception.ResponseParsingException;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ec2.model.*;

import java.time.ZoneId;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class EC2InstanceResponseMapper {

    public List<EC2InstanceResponse> mapEc2InstanceResponse(List<Instance> ec2InstancesResponse) throws ResponseParsingException {
        try {
            return ec2InstancesResponse.stream().map(instance -> EC2InstanceResponse.builder()
                    .name(instance.tags().stream().findFirst().map(Tag::value).orElse(""))
                    .id(instance.instanceId())
                    .az(instance.placement().availabilityZone())
                    .type(instance.instanceType().name())
                    .state(instance.state().nameAsString())
                    .monitoring(instance.monitoring().stateAsString())
                    .publicIP(instance.publicIpAddress())
                    .privateIP(instance.privateIpAddress())
                    .subnetId(instance.subnetId())
                    .launchTime(instance.launchTime().atZone(ZoneId.of("UTC")).toLocalDateTime())
                    .build())
                    .collect(toList());
        } catch (NullPointerException e) {
            throw new ResponseParsingException(e);
        }
    }

}
