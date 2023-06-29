package com.asledz.kancelaria_prawnicza.utilis;

import com.asledz.kancelaria_prawnicza.enums.SortEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SortGenerator {
    private SortGenerator() {
    }

    public static List<Sort> getSortsFromMap(Map<String, String> params) {
        List<Sort> sortList = new ArrayList<>();
        params.forEach((key, value) -> {
            log.info(key + " " + value);
            switch (SortEnum.valueOfSort(key)) {
                case SORT_TITLE -> {
                    Sort sort = Sort.by("title");
                    if ("true".equals(value)) {
                        sort = sort.descending();
                    }
                    sortList.add(sort);
                }
                case SORT_DATE -> {
                    Sort sort = Sort.by("date");
                    if ("true".equals(value)) {
                        sort = sort.descending();
                    }
                    sortList.add(sort);
                }
                case SORT_COST -> {
                    Sort sort = Sort.by("cost");
                    if ("true".equals(value)) {
                        sort = sort.descending();
                    }
                    sortList.add(sort);
                }
                case SORT_PAID -> {
                    Sort sort = Sort.by("paid");
                    if ("true".equals(value)) {
                        sort = sort.descending();
                    }
                    sortList.add(sort);
                }
                case SORT_TYPE -> {
                    Sort sort = Sort.by("type.name");
                    if ("true".equals(value)) {
                        sort = sort.descending();
                    }
                    sortList.add(sort);
                }
            }
        });
        return sortList;
    }
}
