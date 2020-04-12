package com.techtest.computedashboardapi.model.request;

import lombok.Getter;

@Getter
public final class Sort {

    private String order = SortDirection.asc.name();
    private String attr = SortAttributes.id.name();
}
