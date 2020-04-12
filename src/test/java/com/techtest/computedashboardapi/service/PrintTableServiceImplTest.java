package com.techtest.computedashboardapi.service;

import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static com.techtest.computedashboardapi.utils.TestConstants.*;
import static com.techtest.computedashboardapi.utils.TestConstants.LAUNCH_TIME;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PrintTableServiceImplTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private static String[] HEADER_WITH_DELIMITERS = { "NAME", "ID", "TYPE", "STATE", "MONITORING", "AZ", "PUBLIC IP", "PRIVATE IP", "SUBNET ID", "LAUNCH TIME", "|", "="};

    private PrintTableServiceImpl testObj = new PrintTableServiceImpl();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Table with header is printed if empty list is passed")
    void print_emptyList_emptyTableWithHeaderPrinted() {
        //when
        testObj.print(emptyList());

        //then
        assertThat(outContent.toString()).contains(HEADER_WITH_DELIMITERS);
    }

    @Test
    @DisplayName("Table with header and data is printed if non-empty list is passed")
    void print_instancesList_tableWithInstancesPrinted() {
        //given
        List<EC2InstanceResponse> responseList = singletonList(EC2InstanceResponse.builder()
                .id(ID)
                .name(NAME)
                .type(INSTANCE_TYPE)
                .state(STATE)
                .monitoring(MONITORING)
                .az(AZ)
                .publicIP(PUBLIC_IP)
                .privateIP(PRIVATE_IP)
                .subnetId(SUBNET_ID)
                .launchTime(LAUNCH_TIME)
                .build());
        String[] instanceAttributes = {ID, NAME, INSTANCE_TYPE, STATE, MONITORING, AZ, PUBLIC_IP, PRIVATE_IP, SUBNET_ID, LAUNCH_TIME.toString()};

        //when
        testObj.print(responseList);

        //then
        assertThat(outContent.toString()).contains(HEADER_WITH_DELIMITERS);
        assertThat(outContent.toString()).contains(instanceAttributes);
    }
}