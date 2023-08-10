package com.asledz.kancelaria_prawnicza.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class FilterAndSortEnumTest {
    /**
     * Method under test: {@link FilterAndSortEnum#valueOfFilterAndSort(String)}
     */
    @Test
    void testValueOfFilterAndSort() {
        assertNull(FilterAndSortEnum.valueOfFilterAndSort("Label"));
        assertEquals(FilterAndSortEnum.FILTER_COST, FilterAndSortEnum.valueOfFilterAndSort("filter_cost"));
        assertEquals(FilterAndSortEnum.FILTER_COST_TO, FilterAndSortEnum.valueOfFilterAndSort("filter_cost_to"));
    }
}

