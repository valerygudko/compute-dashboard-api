package com.techtest.computedashboardapi.service;

import com.techtest.computedashboardapi.exception.RequestParsingException;
import com.techtest.computedashboardapi.model.request.Sort;
import com.techtest.computedashboardapi.model.request.SortAttributes;
import com.techtest.computedashboardapi.model.request.SortDirection;
import com.techtest.computedashboardapi.model.request.SortRequest;
import com.techtest.computedashboardapi.model.response.EC2InstanceResponse;
import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.techtest.computedashboardapi.model.request.SortDirection.asc;
import static com.techtest.computedashboardapi.model.request.SortDirection.desc;
import static com.techtest.computedashboardapi.model.response.EC2InstanceResponse.*;

@Service
public class SortServiceImpl implements SortService {

    private static final String DELIMITER = ",";

    public List<EC2InstanceResponse> sort(List<EC2InstanceResponse> responseList, SortRequest sortRequest) throws RequestParsingException {
       String[] attributes;
       String[] order;
       Sort sort = sortRequest.getSort();

       if (Objects.isNull(sort)){
           return responseList;
       }

       try {
           attributes = sort.getAttr().split(DELIMITER);
           order = sort.getOrder().split(DELIMITER);
       } catch (NullPointerException ex){
           throw new RequestParsingException("Empty sorting attribute or order are not allowed");
       }

       validateSortingParameters(attributes, order);
       List<Comparator<EC2InstanceResponse>> comparators = getComparators(attributes, order);
       Comparator<EC2InstanceResponse> comparator = ComparatorUtils.chainedComparator(comparators);
       responseList.sort(comparator);

       return responseList;
    }

    private void validateSortingParameters(String[] attributes, String[] order) throws RequestParsingException {
        if (attributes.length != order.length){
            throw new RequestParsingException("Invalid length of sorting parameters passed");
        }
        for (int i = 0; i < attributes.length; i++){
            if (!EnumUtils.isValidEnumIgnoreCase(SortAttributes.class, attributes[i])){
                throw new RequestParsingException("Invalid sorting attribute value passed");
            }
            if (!EnumUtils.isValidEnumIgnoreCase(SortDirection.class, order[i])){
                throw new RequestParsingException("Invalid sorting order value passed");
            }
        }
    }

    private List<Comparator<EC2InstanceResponse>> getComparators(String[] attributes, String[] order) {
        List<Comparator<EC2InstanceResponse>> comparators = new ArrayList<>();

        for (int i = 0; i < attributes.length; i++){

            if (attributes[i].equalsIgnoreCase(SortAttributes.id.name())){
                if (order[i].equalsIgnoreCase(asc.name())){
                    comparators.add(sortByIdAsc);
                } else if (order[i].equalsIgnoreCase(desc.name())) {
                    comparators.add(sortByIdDesc);
                }
            } else if (attributes[i].equalsIgnoreCase(SortAttributes.name.name())) {
                if (order[i].equalsIgnoreCase(asc.name())) {
                    comparators.add(sortByNameAsc);
                } else if (order[i].equalsIgnoreCase(desc.name())) {
                    comparators.add(sortByNameDesc);
                }
            } else if (attributes[i].equalsIgnoreCase(SortAttributes.type.name())) {
                if (order[i].equalsIgnoreCase(asc.name())) {
                    comparators.add(sortByTypeAsc);
                } else if (order[i].equalsIgnoreCase(desc.name())) {
                    comparators.add(sortByTypeDesc);
                }
            } else if (attributes[i].equalsIgnoreCase(SortAttributes.state.name())) {
                if (order[i].equalsIgnoreCase(asc.name())) {
                    comparators.add(sortByStateAsc);
                } else if (order[i].equalsIgnoreCase(desc.name())) {
                    comparators.add(sortByStateDesc);
                }
            } else if (attributes[i].equalsIgnoreCase(SortAttributes.monitoring.name())) {
                if (order[i].equalsIgnoreCase(asc.name())) {
                    comparators.add(sortByMonitoringAsc);
                } else if (order[i].equalsIgnoreCase(desc.name())) {
                    comparators.add(sortByMonitoringDesc);
                }
            } else if (attributes[i].equalsIgnoreCase(SortAttributes.az.name())) {
                if (order[i].equalsIgnoreCase(asc.name())) {
                    comparators.add(sortByAzAsc);
                } else if (order[i].equalsIgnoreCase(desc.name())) {
                    comparators.add(sortByAzDesc);
                }
            } else if (attributes[i].equalsIgnoreCase(SortAttributes.privateIP.name())) {
                if (order[i].equalsIgnoreCase(asc.name())) {
                    comparators.add(sortByPrivateIPAsc);
                } else if (order[i].equalsIgnoreCase(desc.name())) {
                    comparators.add(sortByPrivateIPDesc);
                }
            } else if (attributes[i].equalsIgnoreCase(SortAttributes.publicIP.name())) {
                if (order[i].equalsIgnoreCase(asc.name())) {
                    comparators.add(sortByPublicIPAsc);
                } else if (order[i].equalsIgnoreCase(desc.name())) {
                    comparators.add(sortByPublicIPDesc);
                }
            } else if (attributes[i].equalsIgnoreCase(SortAttributes.subnetId.name())) {
                if (order[i].equalsIgnoreCase(asc.name())) {
                    comparators.add(sortBySubnetIdAsc);
                } else  if (order[i].equalsIgnoreCase(desc.name())){
                    comparators.add(sortBySubnetIdDesc);
                }
            } else if (attributes[i].equalsIgnoreCase(SortAttributes.launchTime.name())) {
                if (order[i].equalsIgnoreCase(asc.name())) {
                    comparators.add(sortByLaunchTimeAsc);
                } else if (order[i].equalsIgnoreCase(desc.name())) {
                    comparators.add(sortByLaunchTimeDesc);
                }
            }
        }
        return comparators;
    }

}
