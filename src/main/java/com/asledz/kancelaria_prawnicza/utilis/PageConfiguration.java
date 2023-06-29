package com.asledz.kancelaria_prawnicza.utilis;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageConfiguration {
    private PageConfiguration() {

    }

    public static Pageable getPageRequest(Integer page, Integer pageSize, String sortBy) {
        page -= 1;
        return PageRequest.of(page, pageSize, Sort.by(sortBy));
    }
}
