package com.asledz.kancelaria_prawnicza.specification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria implements Serializable {
    private String key;
    private String operation;
    private transient Object value;
}
