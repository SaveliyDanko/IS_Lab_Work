package com.savadanko.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location")
@Getter @Setter @NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Double x;

    @NotNull
    @Column(nullable = false)
    private Integer y;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "location")
    @JsonIgnore
    private List<Person> people = new ArrayList<>();
}

