package com.asledz.kancelaria_prawnicza.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity(name = "user")
@Table(name = "user", schema = "first")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    @Field(name = "ownerId", index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String password;
    @Column(columnDefinition = "boolean default true")
    private Boolean enabled = true;
    @ManyToMany
    private Collection<Role> roles;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.PERSIST)
    @JsonBackReference(value = "owner")
    @ContainedIn
    private Collection<Document> documents;
}
