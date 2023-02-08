package com.asledz.kancelaria_prawnicza.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity(name = "file")
@Table(name = "file", schema = "first")
public class File {
    @Id
    @Column(name = "document_id")
    private Long id;
    private String extension;
    @Lob
    private byte[] content;
    @OneToOne()
    @MapsId
    @JoinColumn(name = "document_id")
    private Document document;
}
