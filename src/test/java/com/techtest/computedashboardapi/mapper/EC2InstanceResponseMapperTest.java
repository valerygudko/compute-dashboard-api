package com.techtest.computedashboardapi.mapper;

import com.techtest.computedashboardapi.exception.ResponseParsingException;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ec2.model.*;

import java.time.ZoneId;
import java.util.List;

import static com.techtest.computedashboardapi.utils.TestConstants.*;
import static java.time.Instant.ofEpochSecond;
import static java.time.LocalDateTime.ofInstant;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EC2InstanceResponseMapperTest {

    private static final long SECONDS_SINCE_EPOCH = 1_484_063_246L;

    private EC2InstanceResponseMapper testObj = new EC2InstanceResponseMapper();

    @Test
    @DisplayName("Valid AWS instance object should be mapped to EC2InstanceResponse")
    void mapEc2InstanceResponse_mapsResponse() throws ResponseParsingException {
        // given
        Instance instance = givenAWSInstance();

        List<EC2InstanceResponse> expectedInstanceResponse = singletonList(EC2InstanceResponse.builder()
                .id(ID)
                .name(NAME)
                .type(INSTANCE_TYPE)
                .state(STATE)
                .monitoring(MONITORING)
                .az(AZ)
                .publicIP(PUBLIC_IP)
                .privateIP(PRIVATE_IP)
                .subnetId(SUBNET_ID)
                .launchTime(ofInstant(ofEpochSecond(SECONDS_SINCE_EPOCH), ZoneId.of(UTC)))
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
        Instance instance = Instance.builder().build();

        //when then
        assertThrows(ResponseParsingException.class, () -> testObj.mapEc2InstanceResponse(singletonList(instance)));
    }

    private Instance givenAWSInstance(){

        return Instance.builder()
                .instanceId(ID)
                .tags(Tag.builder()
                        .key(NAME_KEY)
                        .value(NAME)
                        .build())
                .instanceType(INSTANCE_TYPE)
                .state(InstanceState.builder()
                        .name(STATE)
                        .build())
                .monitoring(Monitoring.builder()
                        .state(MONITORING)
                        .build())
                .placement(Placement.builder()
                        .availabilityZone(AZ)
                        .build())
                .publicIpAddress(PUBLIC_IP)
                .privateIpAddress(PRIVATE_IP)
                .subnetId(SUBNET_ID)
                .launchTime(ofEpochSecond(SECONDS_SINCE_EPOCH))
                .build();

    }

}