package com.techtest.computedashboardapi.model.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

import javax.annotation.Nullable;

@Getter
@ApiModel
@Nullable
public final class Sort {

    private String order = SortDirection.asc.name();
    private String attr = SortAttributes.id.name();
}
