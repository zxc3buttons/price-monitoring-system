package ru.tokarev.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceDifferenceDto {

    private String productName;

    private String marketplaceName;

    private List<PriceByDayDto> priceByDayDtoList;
}
