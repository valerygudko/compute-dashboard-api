package com.techtest.computedashboardapi.controller;

import com.techtest.computedashboardapi.exception.CommunicationFailedException;
import com.techtest.computedashboardapi.exception.RequestParsingException;
import com.techtest.computedashboardapi.exception.ResponseParsingException;
import com.techtest.computedashboardapi.exception.RestExceptionHandler;
import com.techtest.computedashboardapi.model.request.PageRequest;
import com.techtest.computedashboardapi.model.request.SortRequest;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import com.techtest.computedashboardapi.service.PrintTableService;
import com.techtest.computedashboardapi.service.RetrieveInstanceService;
import com.techtest.computedashboardapi.service.SortService;
import io.swagger.annotations.Api;
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
@Api("Compute dashboard api for EC2 instances")
public class EC2InstanceController {

    static final String PATH = "/ec2-instances";

    private final RetrieveInstanceService retrieveInstanceService;
    private final PrintTableService printTableService;
    private final SortService sortService;

    public EC2InstanceController(RetrieveInstanceService retrieveInstanceService, SortService sortService, PrintTableService printTableService) {
        this.retrieveInstanceService = retrieveInstanceService;
        this.sortService = sortService;
        this.printTableService = printTableService;
    }

    @ApiOperation(httpMethod = "GET", value = "List of ec2 instances in a specified region", notes =  "Sample request: <BASE_URL>", response = EC2InstanceResponse[].class)
    @ApiResponses({@ApiResponse(code = 200, message = "OK - List of running ec2 instances."),
            @ApiResponse(code = 400, message = RestExceptionHandler.ERROR_CODE_REQUEST_PARSING),
            @ApiResponse(code = 422, message = RestExceptionHandler.ERROR_CODE_RESPONSE_PARSING),
            @ApiResponse(code = 500, message = RestExceptionHandler.ERROR_CODE_COMMUNICATION_FAILED)})

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EC2InstanceResponse> get(@NotNull @RequestParam final String region, @Valid final PageRequest pageRequest, DirectFieldBindingResult bindingResult,
                                         @Valid final SortRequest sortRequest)
            throws CommunicationFailedException, ResponseParsingException, RequestParsingException {
        log.info("Calling the service to retrieve info from AWS for region {}", region);
        if (bindingResult.hasErrors()) {
            throw new RequestParsingException(bindingResult.getAllErrors().toString());
        }
        List<EC2InstanceResponse> result = retrieveInstanceService.getEc2Instances(region, pageRequest);
        printTableService.print(sortService.sort(result, sortRequest));
        return result;
    }
}
