package ru.tokarev.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tokarev.dto.MarketPlaceDto;
import ru.tokarev.dto.item.ItemDto;
import ru.tokarev.dto.item.ProductForItemDto;
import ru.tokarev.dto.item.ProductPriceComparingDto;
import ru.tokarev.dto.item.ProductPriceDifferenceDto;
import ru.tokarev.entity.item.Item;
import ru.tokarev.exception.itemexception.ItemBadRequestException;
import ru.tokarev.service.itemservice.ItemService;
import ru.tokarev.utils.MapperUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("/api/items")
@RestController
public class ItemController {

    private final ItemService itemService;

    private final ModelMapper modelMapper;

    @Autowired
    public ItemController(ItemService itemService, ModelMapper modelMapper) {
        this.itemService = itemService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll() {

        log.info("GET request for /items with no data");

        List<Item> itemList = itemService.getAll();
        List<ItemDto> itemDtoList = MapperUtil.convertList(itemList, this::convertToItemDto);

        log.info("Response for GET request for /items with data {}", itemDtoList);
        for (Item item : itemList) {
            log.info("serialNumber {}, productId {}, price {}, marketplaceId {} dateStart {}, dateEnd {}",
                    item.getSerialNumber(), item.getProduct().getId(), item.getPrice(), item.getMarketplace().getId(),
                    item.getDateStart(), item.getDateEnd());
        }

        return new ResponseEntity<>(itemDtoList, HttpStatus.OK);
    }

    @GetMapping("/{serial_number}")
    public ResponseEntity<ItemDto> getBySerialNumber(
            @PathVariable(value = "serial_number") Long serialNumber) {

        log.info("GET request for /items/{} with data {}", serialNumber, serialNumber);

        Item item = itemService.getBySerialNumber(serialNumber);
        ItemDto itemDto = convertToItemDto(item);

        log.info("Response for GET request for /items/{} with itemDto:" +
                " serialNumber {}, productId {}, price {}, marketplaceId {} dateStart {}, dateEnd {}", serialNumber,
                item.getSerialNumber(), item.getProduct().getId(), item.getPrice(), item.getMarketplace().getId(),
                item.getDateStart(), item.getDateEnd());

        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @GetMapping("/check_price_dynamic")
    public ResponseEntity<Object> getItemPriceDynamic(
            @RequestParam(name = "product_id") Long productId,
            @RequestParam(name = "date_start") String dateStart,
            @RequestParam(name = "date_end") String dateEnd,
            @RequestParam(name = "marketplace_id", required = false) Long marketplaceId) {

        log.info("GET request for /check_price_dynamic with params: {}, {}, {}, {}",
                productId, dateStart, dateEnd, marketplaceId);

        LocalDate dateStartConverted = LocalDate.parse(dateStart);
        LocalDate dateEndConverted = LocalDate.parse(dateEnd).plusDays(1);

        if (dateEndConverted.isBefore(dateStartConverted)) {
            throw new ItemBadRequestException("Date end cannot be before date start");
        }

        List<ProductPriceDifferenceDto> productPriceDifferenceDtos = new ArrayList<>();

        if (marketplaceId != null) {
            productPriceDifferenceDtos.add(itemService.checkPriceDynamicForOneItemAndOneMarketplace(
                    productId, dateStartConverted, dateEndConverted, marketplaceId));
        } else {
            productPriceDifferenceDtos = itemService.checkPriceDynamicForOneItem(
                    productId, dateStartConverted, dateEndConverted);
        }

        log.info("Response for GET request for /check_price_dynamic with data: {}", productPriceDifferenceDtos);
        for(ProductPriceDifferenceDto productPriceDifferenceDto : productPriceDifferenceDtos) {
            log.info("productName {}, marketplaceName {}, priceByDayDtoList {}",
                    productPriceDifferenceDto.getProductName(), productPriceDifferenceDto.getMarketplaceName(),
                    productPriceDifferenceDto.getPriceByDayDtoList());
        }

        return new ResponseEntity<>(productPriceDifferenceDtos, HttpStatus.OK);
    }

    @GetMapping("/compare_prices")
    public ResponseEntity<Object> getItemPriceComparing(
            @RequestParam(name = "product_id", required = false) Long productId,
            @RequestParam(name = "date_start") String dateStart,
            @RequestParam(name = "date_end") String dateEnd) {

        log.info("GET request for /compare_prices with params: {}, {}, {}",
                productId, dateStart, dateEnd);

        LocalDate dateStartConverted = LocalDate.parse(dateStart);
        LocalDate dateEndConverted = LocalDate.parse(dateEnd).plusDays(1);

        List<ProductPriceComparingDto> productPriceComparingDtoList = new ArrayList<>();

        if (productId == null) {
            productPriceComparingDtoList = itemService.getItemsPriceComparing(dateStartConverted, dateEndConverted);

        } else {
            productPriceComparingDtoList.add(itemService.getItemPriceComparing(
                    productId, dateStartConverted, dateEndConverted));
        }

        log.info("Response for GET request for /compare_prices with data: {}", productPriceComparingDtoList);
        for(ProductPriceComparingDto productPriceComparingDto : productPriceComparingDtoList) {
            log.info("productName {} and marketplacePriceMap {}", productPriceComparingDto.getProductName(),
                    productPriceComparingDto.getMarketplaceEverydayPricesMap());
        }

        return new ResponseEntity<>(productPriceComparingDtoList, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> createItem(
            @RequestBody ItemDto itemDto) {

        log.info("POST request for /items with data:" +
                " productId {}, price {}, marketplaceId {} dateStart {}, dateEnd {}",
                itemDto.getProductForItemDto().getId(), itemDto.getPrice(), itemDto.getMarketPlaceDto().getId(),
                itemDto.getDateStart(), itemDto.getDateEnd());

        if (itemDto.getDateEnd().isBefore((itemDto.getDateStart()))) {
            throw new ItemBadRequestException("Date end cannot be before date start");
        }

        Item item = convertToItemEntity(itemDto);
        Item createdItem = itemService.createItem(item);
        ItemDto createdItemDto = convertToItemDto(createdItem);

        log.info("Response for POST request for /items with data:  " +
                "serialNumber {}, productId {}, price {}, marketplaceId {} dateStart {}, dateEnd {}",
                createdItemDto.getSerialNumber(),
                createdItemDto.getProductForItemDto().getId(), createdItemDto.getPrice(),
                createdItemDto.getMarketPlaceDto().getId(),
                createdItemDto.getDateStart(), createdItemDto.getDateEnd());

        return new ResponseEntity<>(createdItemDto, HttpStatus.CREATED);
    }

    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemDto>> importProductsOnMarket(
            @RequestBody List<ItemDto> itemDtoList) {

        log.info("POST request for /items/import with data {}", itemDtoList);
        for(ItemDto itemDto: itemDtoList) {
            log.info("productId {}, price {}, marketplaceId {} dateStart {}, dateEnd {}",
                    itemDto.getProductForItemDto().getId(), itemDto.getPrice(),
                    itemDto.getMarketPlaceDto().getId(), itemDto.getDateStart(), itemDto.getDateEnd());
        }

        for (ItemDto itemDto : itemDtoList) {
            if (itemDto.getDateEnd().isBefore((itemDto.getDateStart()))) {
                throw new ItemBadRequestException("Date end cannot be before date start");
            }
        }

        List<Item> itemList
                = MapperUtil.convertList(itemDtoList, this::convertToItemEntity);
        List<Item> createdProductsOnMarketList =
                itemService.createItems(itemList);
        List<ItemDto> createdProductsOnMarketDtoList =
                MapperUtil.convertList(createdProductsOnMarketList, this::convertToItemDto);

        log.info("Response for POST request for /items/import with data {}", createdProductsOnMarketDtoList);
        for(ItemDto itemDto: createdProductsOnMarketDtoList) {
            log.info("serialNumber {}, productId {}, price {}, marketplaceId {} dateStart {}, dateEnd {}",
                    itemDto.getSerialNumber(), itemDto.getProductForItemDto().getId(), itemDto.getPrice(),
                    itemDto.getMarketPlaceDto().getId(), itemDto.getDateStart(), itemDto.getDateEnd());
        }

        return new ResponseEntity<>(createdProductsOnMarketDtoList, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{serial_number}")
    public ResponseEntity<String> deleteProductOnMarket(@PathVariable(value = "serial_number") Long serialNumber) {

        log.info("DELETE request for /items/{} with data {}", serialNumber, serialNumber);

        itemService.deleteItem(serialNumber);

        log.info("Response for DELETE request for /items/{} with data {}", serialNumber, "Item deleted successfully");

        return new ResponseEntity<>("Item deleted successfully", HttpStatus.OK);
    }

    private ItemDto convertToItemDto(Item item) {
        ProductForItemDto productForItemDto = modelMapper.map(item.getProduct(), ProductForItemDto.class);
        MarketPlaceDto marketPlaceDto = modelMapper.map(item.getMarketplace(), MarketPlaceDto.class);
        ItemDto itemDto = modelMapper.map(item, ItemDto.class);
        itemDto.setProductForItemDto(productForItemDto);
        itemDto.setMarketPlaceDto(marketPlaceDto);

        return itemDto;
    }

    private Item convertToItemEntity(ItemDto itemDto) {
        return modelMapper.map(itemDto, Item.class);
    }
}
