package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Type;
import com.asledz.kancelaria_prawnicza.dto.TypeDTO;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.mapper.TypeDTOMapper;
import com.asledz.kancelaria_prawnicza.repository.TypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static com.asledz.kancelaria_prawnicza.service.TypeService.TYPE_NOT_FOUND_MSG;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TypeServiceTest {
    @Spy
    private TypeDTOMapper dTOMapper;

    @Mock
    private TypeRepository typeRepository;

    @InjectMocks
    private TypeService typeService;

    /**
     * Method under test: {@link TypeService#getTypes()}
     */
    @Test
    void testGetTypes() {
        when(typeRepository.findAll(Mockito.<Sort>any())).thenReturn(new ArrayList<>());
        assertTrue(typeService.getTypes().isEmpty());
        verify(typeRepository).findAll(Mockito.<Sort>any());
    }

    /**
     * Method under test: {@link TypeService#getTypes()}
     */
    @Test
    void testGetTypeByIdThrowsNotFound() {
        Long id = 1L;
        given(typeRepository.findById(id)).willReturn(Optional.empty());
        assertThatThrownBy(() -> typeService.getTypeById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(TYPE_NOT_FOUND_MSG, id));
    }

    /**
     * Method under test: {@link TypeService#getTypeById(Long)}
     */
    @Test
    void testGetTypeById() {
        Type type = new Type();
        type.setDocuments(new ArrayList<>());
        type.setId(1L);
        type.setName("Name");
        Optional<Type> ofResult = Optional.of(type);
        when(typeRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        assertSame(type, typeService.getTypeById(1L));
        verify(typeRepository).findById(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link TypeService#addType(TypeDTO)}
     */
    @Test
    void testAddType() {
        Type type = new Type();
        type.setDocuments(new ArrayList<>());
        type.setId(1L);
        type.setName("Name");
        when(typeRepository.save(Mockito.<Type>any())).thenReturn(type);
        TypeDTO typeDTO = new TypeDTO(1L, "Name");

        assertEquals(typeDTO, typeService.addType(new TypeDTO(1L, "Name")));
        verify(typeRepository).save(Mockito.<Type>any());
        verify(dTOMapper).map(Mockito.<Type>any());
    }
}

