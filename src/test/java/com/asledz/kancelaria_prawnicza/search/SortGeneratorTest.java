package com.asledz.kancelaria_prawnicza.search;

import com.asledz.kancelaria_prawnicza.enums.SortEnum;
import com.asledz.kancelaria_prawnicza.exception.WrongRequestValuesException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SortGeneratorTest {
    /**
     * Method under test: {@link SortGenerator#getSortsFromMap(Map)}
     */
    @Test
    void testGetSortsFromMap() {
        HashMap<String, String> params = new HashMap<>();
        for (SortEnum sortEnum : SortEnum.values()) {
            params.put(sortEnum.value, "false");
        }
        params.remove("null");
        List<Sort> sortList = new ArrayList<>();

        Sort sort = Sort.by("type.name");
        sortList.add(sort);
        sort = Sort.by("title");
        sortList.add(sort);
        sort = Sort.by("paid");
        sortList.add(sort);
        sort = Sort.by("cost");
        sortList.add(sort);
        sort = Sort.by("date");
        sortList.add(sort);

        assertEquals(sortList, SortGenerator.getSortsFromMap(params));
    }

    /**
     * Method under test: {@link SortGenerator#getSortsFromMap(Map)}
     */
    @Test
    void testGetSortsFromMapThrowWrongRequestValuesExceptionIfKeyIsNotSupported() {
        HashMap<String, String> params = new HashMap<>();
        String key = "sortByGod";
        params.put(key, "true");
        assertThatThrownBy(() -> SortGenerator.getSortsFromMap(params))
                .isInstanceOf(WrongRequestValuesException.class)
                .hasMessageContaining(String.format("Can't sort by given key: %s".formatted(key)));
    }
}

