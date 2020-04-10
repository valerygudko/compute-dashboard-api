package com.techtest.computedashboardapi.mapper;

import com.amazonaws.services.ec2.model.*;
import com.techtest.computedashboardapi.exception.ResponseParsingException;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.techtest.computedashboardapi.utils.TestConstants.*;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EC2InstanceResponseMapperTest {

    private EC2InstanceResponseMapper testObj = new EC2InstanceResponseMapper();

    @Test
    @DisplayName("Valid AWS instance object should be mapped to EC2InstanceResponse")
    void mapEc2InstanceResponse_mapsResponse() throws ResponseParsingException {
        // given
        Instance instance = givenAWSInstance();

        List<EC2InstanceResponse> expectedInstanceResponse = singletonList(EC2InstanceResponse.builder()
                .id(ID)
                .name(NAME)
                .type(TYPE)
                .state(STATE)
                .monitoring(MONITORING)
                .az(AZ)
                .publicIP(PUBLIC_IP)
                .privateIP(PRIVATE_IP)
                .subnetId(SUBNET_ID)
                .launchTime(LAUNCH_TIME)
                .build());

        // when
        List<EC2InstanceResponse> actualInstanceResponse = testObj.mapEc2InstanceResponse(singletonList(instance));

        // then
        assertThat(actualInstanceResponse.size()).isEqualTo(1);
        assertThat(actualInstanceResponse.get(0)).isEqualToComparingFieldByField(expectedInstanceResponse.get(0));
    }

    @Test
    @DisplayName("When AWS instance object has some data missing then ResponseParsingException thrown")
    void mapEc2InstanceResponse_noDataToMapResponse_ResponseParsingExceptionThrown() {
        // given
        Instance instance = new Instance();

        //when then
        assertThrows(ResponseParsingException.class, () -> testObj.mapEc2InstanceResponse(singletonList(instance)));
    }

    private Instance givenAWSInstance(){
        Tag tag = new Tag();
        tag.setKey(NAME_KEY);
        tag.setValue(NAME);

        InstanceState instanceState = new InstanceState();
        instanceState.setName(STATE);

        Monitoring monitoring = new Monitoring();
        monitoring.setState(MONITORING);

        Placement placement = new Placement();
        placement.setAvailabilityZone(AZ);

        Instance instance = new Instance();
        instance.setInstanceId(ID);
        instance.setTags(singletonList(tag));
        instance.setInstanceType(TYPE);
        instance.setState(instanceState);
        instance.setMonitoring(monitoring);
        instance.setPlacement(placement);
        instance.setPublicIpAddress(PUBLIC_IP);
        instance.setPrivateIpAddress(PRIVATE_IP);
        instance.setSubnetId(SUBNET_ID);
        instance.setLaunchTime(LAUNCH_TIME);

        return instance;
    }

}