package ru.tokarev.entity.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.entity.Product;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemCompositeId implements Serializable {

    private Integer price;

    private LocalDate dateStart;

    private LocalDate dateEnd;

    private Product product;

    private Marketplace marketplace;
}
