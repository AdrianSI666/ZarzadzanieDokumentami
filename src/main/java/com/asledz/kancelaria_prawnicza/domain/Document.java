package com.asledz.kancelaria_prawnicza.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import java.time.Instant;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
@Entity(name = "document")
@Table(name = "document", schema = "first")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    @Analyzer(impl = SimpleAnalyzer.class)
    private String title;
    @Field(index = Index.YES, analyze= Analyze.NO, store = Store.NO)
    private Instant date;
    private Double cost;
    @Field(index = Index.YES, analyze= Analyze.NO, store = Store.NO)
    private Boolean paid;
    @ManyToOne
    private User owner;
    @ManyToOne
    @IndexedEmbedded
    private Type type;
    @ManyToMany
    @IndexedEmbedded
    private Collection<Tag> tags;
    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private File file;
}
