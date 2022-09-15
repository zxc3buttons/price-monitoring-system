package ru.tokarev.entity.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.entity.Product;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ItemCompositeId.class)
@Table(name = "item")
@Entity
public class Item implements Serializable {

    @Column(name = "serial_number")
    private Long serialNumber;

    @Id
    @Column(name = "price")
    private Integer price;

    @Id
    @Column(name = "date_start")
    private LocalDate dateStart;

    @Id
    @Column(name = "date_end")
    private LocalDate dateEnd;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "product_id")
    private Product product;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "marketplace_id")
    private Marketplace marketplace;
}
