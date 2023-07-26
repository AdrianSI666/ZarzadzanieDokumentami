package com.asledz.kancelaria_prawnicza.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.annotations.Store;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity(name = "type")
@Table(name = "type", schema = "first")
public class Type implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    @Field(name = "typeId", index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    private Long id;
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    @Analyzer(impl = MorfologikAnalyzer.class)
    @SortableField
    private String name;
    @OneToMany(mappedBy = "type", cascade = CascadeType.PERSIST)
    @JsonBackReference
    @ContainedIn
    private Collection<Document> documents;
}
