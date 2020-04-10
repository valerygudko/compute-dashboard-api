package com.techtest.computedashboardapi.service;

import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import dnl.utils.text.table.TextTable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PrintTableServiceImpl implements PrintTableService {

    private static final String NAME = "NAME";
    private static final String ID = "ID";
    private static final String TYPE = "TYPE";
    private static final String STATE = "STATE";
    private static final String MONITORING = "MONITORING";
    private static final String AZ = "AZ";
    private static final String PUBLIC_IP = "PUBLIC IP";
    private static final String PRIVATE_IP = "PRIVATE IP";
    private static final String SUBNET_ID = "SUBNET ID";
    private static final String LAUNCH_TIME = "LAUNCH TIME";

    public PrintTableServiceImpl(){}

    public void print(List<EC2InstanceResponse> responseList){
        String[] columnNames = {NAME, ID, TYPE, STATE, MONITORING, AZ, PUBLIC_IP, PRIVATE_IP, SUBNET_ID, LAUNCH_TIME};
        String[][] values = new String[columnNames.length][responseList.size()];

        fillArrayFromResponse(responseList, values);

        TextTable tt = new TextTable(columnNames, values);
        tt.printTable();
    }

    private void fillArrayFromResponse(List<EC2InstanceResponse> responseList, String[][] values) {
        IntStream.range(0, responseList.size()).forEach(i -> {
            EC2InstanceResponse response = responseList.get(i);
            values[i] = new String[] {response.getName(), response.getId(), response.getType(), response.getState(), response.getMonitoring(),
                    response.getAz(), response.getPublicIP(), response.getPrivateIP(), response.getSubnetId(), response.getLaunchTime().toString()};
        });
    }
}
