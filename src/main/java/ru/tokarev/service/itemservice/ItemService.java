package ru.tokarev.service.itemservice;

import ru.tokarev.dto.item.ProductPriceComparingDto;
import ru.tokarev.dto.item.ProductPriceDifferenceDto;
import ru.tokarev.entity.item.Item;

import java.time.LocalDate;
import java.util.List;

public interface ItemService {

    Item getBySerialNumber(Long serialNumber);

    List<Item> getAll();

    ProductPriceDifferenceDto checkPriceDynamicForOneItemAndOneMarketplace(
            Long productId, LocalDate dateStart, LocalDate dateEnd, Long marketplaceId);

    List<ProductPriceDifferenceDto> checkPriceDynamicForOneItem(
            Long product, LocalDate dateStart, LocalDate dateEnd);

    ProductPriceComparingDto getItemPriceComparing(Long productId, LocalDate dateStart, LocalDate dateEnd);

    List<ProductPriceComparingDto> getItemsPriceComparing(LocalDate dateStart, LocalDate dateEnd);

    Item createItem(Item item);

    List<Item> createItems(List<Item> itemList);

    void deleteItem(Long id);
}
