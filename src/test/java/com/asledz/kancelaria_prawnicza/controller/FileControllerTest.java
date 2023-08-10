package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.FileDTO;
import com.asledz.kancelaria_prawnicza.enums.Path;
import com.asledz.kancelaria_prawnicza.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FileControllerTest {
    @Mock
    private FileService fileService;
    @InjectMocks
    private FileController fileController;

    /**
     * Method under test: {@link FileController#downloadFile(Long)}
     */
    @Test
    void testDownloadFile() throws Exception {
        FileDTO fileDTO = new FileDTO(1L, "Name", "text/plain", "AXAXAXAX".getBytes("UTF-8"));
        when(fileService.getFileById(Mockito.<Long>any()))
                .thenReturn(fileDTO);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(Path.FILE_VALUE + "s/{id}",
                        fileDTO.id());
        String fileName = fileDTO.name() + fileDTO.extension();
        MockMvcBuilders.standaloneSetup(fileController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\""))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.valueOf(fileDTO.extension())))
                .andExpect(MockMvcResultMatchers.content().bytes(fileDTO.content()));
    }

    /**
     * Method under test: {@link FileController#saveFile(Long, MultipartFile)}
     */
    @Test
    void testSaveFile() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        long id = 1L;
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.multipart(Path.FILE_VALUE + "s/{id}",
                                id)
                        .file(file);

        doNothing().when(fileService).addFile(file, 1L);
        MockMvcBuilders.standaloneSetup(fileController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
    }
}

