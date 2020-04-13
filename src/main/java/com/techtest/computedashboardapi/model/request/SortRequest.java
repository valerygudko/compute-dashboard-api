package com.techtest.computedashboardapi.model.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
@Nullable
public final class SortRequest {

    private Sort sort;

}
