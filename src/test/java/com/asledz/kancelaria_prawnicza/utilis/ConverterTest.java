package com.asledz.kancelaria_prawnicza.utilis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {Converter.class})
@ExtendWith(SpringExtension.class)
class ConverterTest {
    @Autowired
    private Converter converter;

    /**
     * Method under test: {@link Converter#convertDataFromPageToMap(Page)}
     */
    @Test
    void testConvertDataFromPageToMap() {
        PageImpl<Object> dataDTOPage = new PageImpl<>(new ArrayList<>());
        assertEquals(3, converter.convertDataFromPageToMap(dataDTOPage).size());
        assertTrue(dataDTOPage.toList().isEmpty());
    }
}

