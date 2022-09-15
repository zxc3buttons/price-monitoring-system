package ru.tokarev.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "marketplace")
public class Marketplace implements Serializable {
    @Id
    @SequenceGenerator(name = "market_place_seq", sequenceName = "market_place_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "market_place_seq")
    private Long id;

    @Column(name = "name")
    private String name;
}
