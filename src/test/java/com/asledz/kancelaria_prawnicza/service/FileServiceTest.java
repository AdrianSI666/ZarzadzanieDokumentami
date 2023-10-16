package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Document;
import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.domain.Type;
import com.asledz.kancelaria_prawnicza.domain.User;
import com.asledz.kancelaria_prawnicza.dto.FileDTO;
import com.asledz.kancelaria_prawnicza.exception.BadRequestException;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.mapper.FileDTOMapper;
import com.asledz.kancelaria_prawnicza.mother.UserMother;
import com.asledz.kancelaria_prawnicza.repository.FileRepository;
import com.asledz.kancelaria_prawnicza.repository.TypeRepository;
import com.asledz.kancelaria_prawnicza.repository.UserRepository;
import com.asledz.kancelaria_prawnicza.utilis.TextExtractor;
import com.asledz.kancelaria_prawnicza.utilis.Zipper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Optional;

import static com.asledz.kancelaria_prawnicza.service.FileService.FILE_NOT_FOUND_MSG;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class FileServiceTest {
    @Spy
    private FileDTOMapper dTOMapper;

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    @Mock
    private TypeRepository typeRepository;

    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<File> fileArgumentCaptor;
    User owner = UserMother.basic()
            .documents(new ArrayList<>())
            .email("jane.doe@example.org")
            .id(1L)
            .name("Name")
            .password("iloveyou")
            .roles(new ArrayList<>())
            .surname("Doe").build();
    Type type = Type.builder().id(0L).name("brak typu").build();

    /**
     * Method under test: {@link FileService#getFileById(Long)}
     */
    @Test
    void testGetFileById() {
        byte[] bytes = "Hello, World!".getBytes();
        long fileId = 1L;

        Document document = new Document();
        document.setCost(10.0d);
        document.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document.setFile(new File());
        document.setId(1L);
        document.setOwner(new User());
        document.setPaid(true);
        document.setTitle("Dr");
        document.setType(new Type());

        File file = new File();
        file.setContent(bytes);
        file.setDocument(document);
        document.setFile(file);
        file.setExtension("Extension");
        file.setId(1L);
        file.setText("Text");

        FileDTO fileDTO = new FileDTO(fileId, file.getDocument().getTitle(), file.getExtension(), bytes);

        Optional<File> ofResult = Optional.of(file);
        given(fileRepository.findById(fileId)).willReturn(ofResult);

        try (MockedStatic<Zipper> zipperMockedStatic = Mockito.mockStatic(Zipper.class)) {
            zipperMockedStatic.when(() -> Zipper.decompress(file.getContent()))
                    .thenReturn(bytes);

            FileDTO actualDTO = fileService.getFileById(fileId);
            assertEquals(fileDTO, actualDTO);
        }
    }

    /**
     * Method under test: {@link FileService#getFileById(Long)}
     */
    @Test
    void testGetFileByIdThrowsNotFound() {
        {
            Long id = 1L;
            given(fileRepository.findById(id)).willReturn(Optional.empty());
            assertThatThrownBy(() -> fileService.getFileById(id))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(String.format(FILE_NOT_FOUND_MSG, id));
        }
    }

    /**
     * Method under test: {@link FileService#addFile(MultipartFile, Long)}
     */
    @Test
    void testAddFile() {
        byte[] bytes = "Hello, World!".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                bytes
        );
        long userId = owner.getId();
        given(userRepository.findById(userId)).willReturn(Optional.ofNullable(owner));
        given(typeRepository.findById(0L)).willReturn(Optional.ofNullable(type));

        try (MockedStatic<TextExtractor> textExtractor = Mockito.mockStatic(TextExtractor.class)) {
            textExtractor.when(() -> TextExtractor.extractTextFromFile(any(), anyString(), anyString()))
                    .thenReturn("Hello, World!");
            try (MockedStatic<Zipper> zipperMockedStatic = Mockito.mockStatic(Zipper.class)) {
                zipperMockedStatic.when(() -> Zipper.compress(file.getBytes()))
                        .thenReturn(bytes);
                File expectedFile = File.builder()
                        .extension(file.getContentType())
                        .text("Hello, World!")
                        .content(bytes)
                        .document(Document.builder()
                                .title("hello")
                                .type(type)
                                .owner(owner)
                                .build())
                        .build();

                fileService.addFile(file, userId);
                verify(fileRepository).save(fileArgumentCaptor.capture());
                assertEquals(expectedFile.getDocument().getTitle(), fileArgumentCaptor.getValue().getDocument().getTitle());
                assertEquals(expectedFile.getDocument().getType().getName(), fileArgumentCaptor.getValue().getDocument().getType().getName());
                assertEquals(expectedFile.getText(), fileArgumentCaptor.getValue().getText());
                assertEquals(expectedFile.getContent(), fileArgumentCaptor.getValue().getContent());
            }
        }
    }

    /**
     * Method under test: {@link FileService#addFile(MultipartFile, Long)}
     */
    @Test
    void testAddFile2() {
        byte[] bytes = "Hello, World!".getBytes();
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                null,
                MediaType.APPLICATION_PDF_VALUE,
                bytes
        );
        long userId = owner.getId();
        given(userRepository.findById(userId)).willReturn(Optional.ofNullable(owner));
        given(typeRepository.findById(0L)).willReturn(
                Optional.ofNullable(type));

        try (MockedStatic<TextExtractor> textExtractor = Mockito.mockStatic(TextExtractor.class)) {
            textExtractor.when(() -> TextExtractor.extractTextFromFile(any(), anyString(), any()))
                    .thenReturn("Hello, World!");
            try (MockedStatic<Zipper> zipperMockedStatic = Mockito.mockStatic(Zipper.class)) {
                zipperMockedStatic.when(() -> Zipper.compress(file.getBytes()))
                        .thenReturn(bytes);
                File expectedFile = File.builder()
                        .extension(file.getContentType())
                        .text("Hello, World!")
                        .content(bytes)
                        .document(Document.builder()
                                .title("No name")
                                .type(type)
                                .owner(owner)
                                .build())
                        .build();

                fileService.addFile(file, userId);
                verify(fileRepository).save(fileArgumentCaptor.capture());
                assertEquals(expectedFile.getDocument().getTitle(), fileArgumentCaptor.getValue().getDocument().getTitle());
                assertEquals(expectedFile.getDocument().getType().getName(), fileArgumentCaptor.getValue().getDocument().getType().getName());
                assertEquals(expectedFile.getText(), fileArgumentCaptor.getValue().getText());
                assertEquals(expectedFile.getContent(), fileArgumentCaptor.getValue().getContent());
            }
        }
    }

    /**
     * Method under test: {@link FileService#addFile(MultipartFile, Long)}
     */
    @Test
    void testAddFileThrowNotFound() {
        byte[] bytes = "Hello, World!".getBytes();
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                null,
                MediaType.APPLICATION_PDF_VALUE,
                bytes
        );
        Long id = 1L;
        given(userRepository.findById(id)).willReturn(Optional.empty());
        assertThatThrownBy(() -> fileService.addFile(file, id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Failed to save file, because couldn't find user with given id: %d".formatted(id));
    }

    /**
     * Method under test: {@link FileService#addFile(MultipartFile, Long)}
     */
    @Test
    void testAddFileThrowNotFound2() {
        byte[] bytes = "Hello, World!".getBytes();
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                null,
                MediaType.APPLICATION_PDF_VALUE,
                bytes
        );
        Long id = 1L;
        given(userRepository.findById(id)).willReturn(Optional.of(owner));
        given(typeRepository.findById(0L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> fileService.addFile(file, id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Database lacks default type for newly added files");
    }

    /**
     * Error Reading File
     */
    @Test
    void testAddFileThrowIOException() {
        byte[] bytes = "Hello, World!".getBytes();
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                null,
                MediaType.APPLICATION_PDF_VALUE,
                bytes
        );
        Long id = 1L;
        given(userRepository.findById(id)).willReturn(Optional.of(owner));
        given(typeRepository.findById(0L)).willReturn(Optional.ofNullable(type));
        try (MockedStatic<TextExtractor> textExtractor = Mockito.mockStatic(TextExtractor.class)) {
            textExtractor.when(() -> TextExtractor.extractTextFromFile(any(), anyString(), any()))
                    .thenThrow(IOException.class);
            assertThatThrownBy(() -> fileService.addFile(file, id))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Couldn't read file.");
        }
    }

    /**
     * Error Compressing File
     */
    @Test
    void testAddFileThrowIOException2() {
        byte[] bytes = "Hello, World!".getBytes();
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                null,
                MediaType.APPLICATION_PDF_VALUE,
                bytes
        );
        Long id = 1L;
        given(userRepository.findById(id)).willReturn(Optional.of(owner));
        given(typeRepository.findById(0L)).willReturn(Optional.ofNullable(type));
        try (MockedStatic<TextExtractor> textExtractor = Mockito.mockStatic(TextExtractor.class)) {
            textExtractor.when(() -> TextExtractor.extractTextFromFile(any(), anyString(), anyString()))
                    .thenReturn("Hello, World!");
            try (MockedStatic<Zipper> zipperMockedStatic = Mockito.mockStatic(Zipper.class)) {
                zipperMockedStatic.when(() -> Zipper.compress(file.getBytes()))
                        .thenThrow(IOException.class);
                assertThatThrownBy(() -> fileService.addFile(file, id))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessageContaining("Couldn't read file.");
            }
        }
    }
}

