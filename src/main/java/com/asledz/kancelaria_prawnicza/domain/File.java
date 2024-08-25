package com.asledz.kancelaria_prawnicza.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Store;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity(name = "file")
@Table(name = "file", schema = "first")
public class File implements Serializable {
    @Id
    @Column(name = "document_id")
    private Long id;
    private String extension;
    @Lob
    private byte[] content;
    @Lob
    @Field(store = Store.NO, analyzer = @Analyzer(impl = MorfologikAnalyzer.class))
    private String text;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "document_id")
    @ContainedIn
    private Document document;
}
