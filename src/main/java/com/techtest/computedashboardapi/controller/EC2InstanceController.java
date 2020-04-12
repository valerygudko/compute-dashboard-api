package com.techtest.computedashboardapi.controller;

import com.techtest.computedashboardapi.exception.CommunicationFailedException;
import com.techtest.computedashboardapi.exception.RequestParsingException;
import com.techtest.computedashboardapi.exception.ResponseParsingException;
import com.techtest.computedashboardapi.model.request.PageRequest;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import com.techtest.computedashboardapi.service.PrintTableService;
import com.techtest.computedashboardapi.service.RetrieveInstanceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(value = EC2InstanceController.PATH)
@Slf4j
public class EC2InstanceController {

    static final String PATH = "/ec2-instances";

    private final RetrieveInstanceService retrieveInstanceService;
    private final PrintTableService printTableService;

    public EC2InstanceController(RetrieveInstanceService retrieveInstanceService, PrintTableService printTableService) {
        this.retrieveInstanceService = retrieveInstanceService;
        this.printTableService = printTableService;
    }

    @ApiOperation(httpMethod = "GET", value = "List of ec2 instances in a specified region", response = EC2InstanceResponse[].class)
    @ApiResponses({@ApiResponse(code = 200, message = "OK - List of running ec2 instances."),
            @ApiResponse(code = 400, message = "Bad Request - Region must be specified and be of a valid value, page request must have valid parameters")})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EC2InstanceResponse> get(@NotNull @RequestParam final String region, @Valid final PageRequest pageRequest, DirectFieldBindingResult bindingResult,
                                         @RequestParam(required = false) final List<String> sort)
            throws CommunicationFailedException, ResponseParsingException, RequestParsingException {
        log.info("Calling the service to retrieve info from AWS for region {}", region);
        //TODO: implement sorting
        if (bindingResult.hasErrors()) {
            throw new RequestParsingException(bindingResult.getAllErrors().toString());
        }
        List<EC2InstanceResponse> result = retrieveInstanceService.getEc2Instances(region, pageRequest);
        printTableService.print(result);
        return result;
    }
}
