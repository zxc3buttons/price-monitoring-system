package ru.tokarev.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceComparingDto {

    private String productName;

    private Map<LocalDate, Map<String, Integer>> marketplaceEverydayPricesMap;
}
