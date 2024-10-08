package com.asledz.kancelaria_prawnicza.utilis;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Converter {
    public <T> Map<String, Object> convertDataFromPageToMap(Page<T> dataDTOPage) {
        List<T> data = dataDTOPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("currentPage", dataDTOPage.getNumber() + 1);
        response.put("totalPages", dataDTOPage.getTotalPages());
        return response;
    }
}
