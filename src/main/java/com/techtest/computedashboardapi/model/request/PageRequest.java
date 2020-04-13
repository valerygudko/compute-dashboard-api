package com.techtest.computedashboardapi.model.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@NoArgsConstructor
@ApiModel
@Nullable
public final class PageRequest {

    @Min(1)
    private int page = 1;

    @Min(5)
    @Max(1000)
    private int size = 5;
}
