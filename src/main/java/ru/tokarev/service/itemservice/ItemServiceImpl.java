package ru.tokarev.service.itemservice;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tokarev.dao.marketplacedao.MarketPlaceDao;
import ru.tokarev.dao.productdao.ProductDao;
import ru.tokarev.dao.productonmarket.ItemDao;
import ru.tokarev.dto.item.PriceByDayDto;
import ru.tokarev.dto.item.ProductPriceComparingDto;
import ru.tokarev.dto.item.ProductPriceDifferenceDto;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.entity.Product;
import ru.tokarev.entity.item.Item;
import ru.tokarev.entity.item.ItemCompositeId;
import ru.tokarev.exception.itemexception.ItemBadRequestException;
import ru.tokarev.exception.itemexception.ItemExistsException;
import ru.tokarev.exception.itemexception.ItemNotFoundException;
import ru.tokarev.exception.marketplaceexception.MarketPlaceNotFoundException;
import ru.tokarev.exception.productexception.ProductNotFoundException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private ItemDao<Item> itemDao;

    private ProductDao<Product> productDao;

    private MarketPlaceDao<Marketplace> marketPlaceDao;

    @Autowired
    public void setDao(ItemDao<Item> itemDao, ProductDao<Product> productDao,
                       MarketPlaceDao<Marketplace> marketPlaceDao) {
        this.itemDao = itemDao;
        this.itemDao.setClazz(Item.class);
        this.productDao = productDao;
        this.productDao.setClazz(Product.class);
        this.marketPlaceDao = marketPlaceDao;
        this.marketPlaceDao.setClazz(Marketplace.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public Item getBySerialNumber(Long serialNumber) {
        Item item = itemDao.findBySerialNumber(serialNumber).orElseThrow(
                () -> new ItemNotFoundException("Product on market with this id not found")
        );

        item.setProduct((Product) Hibernate.unproxy(item.getProduct()));
        item.setMarketplace((Marketplace) Hibernate.unproxy(item.getMarketplace()));

        return item;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public List<Item> getAll() {

        List<Item> itemList = itemDao.findAll().orElseThrow(
                () -> new ItemNotFoundException("Products on markets not found"));

        if (itemList.size() == 0) {
            throw new ItemNotFoundException("Products on markets not found");
        }

        for (Item item : itemList) {
            item.setProduct((Product) Hibernate.unproxy(item.getProduct()));
            item.setMarketplace((Marketplace) Hibernate.unproxy(item.getMarketplace()));
        }

        return itemList;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public ProductPriceDifferenceDto checkPriceDynamicForOneItemAndOneMarketplace(
            Long productId, LocalDate dateStart, LocalDate dateEnd, Long marketplaceId) {

        Product product = productDao.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product with this id doesn't exist")
        );
        Marketplace marketplace = marketPlaceDao.findById(marketplaceId).orElseThrow(
                () -> new ProductNotFoundException("Marketplace with this id doesn't exist")
        );

        List<Item> itemList = itemDao.findAllByProductAndMarketplace(
                product, marketplace).orElseThrow(
                () -> new ItemNotFoundException("Products on markets not found")
        );

        if (itemList.size() == 0) {
            throw new ItemNotFoundException("Products on markets not found");
        }

        List<LocalDate> datesBetween = dateStart.datesUntil(dateEnd).collect(Collectors.toList());
        List<PriceByDayDto> priceByDayDtoList = createPriceByDayDtoList(itemList, datesBetween);

        return new ProductPriceDifferenceDto(product.getName(), marketplace.getName(), priceByDayDtoList);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public List<ProductPriceDifferenceDto> checkPriceDynamicForOneItem(
            Long productId, LocalDate dateStart, LocalDate dateEnd) {

        Product product = productDao.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product with this id doesn't exist")
        );

        List<Marketplace> marketplaceList = marketPlaceDao.findAll().orElseThrow(
                () -> new MarketPlaceNotFoundException("Marketplaces do not exist")
        );

        List<Item> itemList;

        List<LocalDate> datesBetween = dateStart.datesUntil(dateEnd).collect(Collectors.toList());

        List<ProductPriceDifferenceDto> productPriceDifferenceDtoList = new ArrayList<>();
        for (Marketplace marketplace : marketplaceList) {

            itemList = itemDao.findAllByProductAndMarketplace(product, marketplace)
                    .orElseThrow(() -> new ItemNotFoundException("Products on markets not found")
                    );


            List<PriceByDayDto> priceByDayDtoList = createPriceByDayDtoList(itemList, datesBetween);
            productPriceDifferenceDtoList.add(
                    new ProductPriceDifferenceDto(product.getName(), marketplace.getName(), priceByDayDtoList));
        }

        return productPriceDifferenceDtoList;

    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public List<ProductPriceComparingDto> getItemsPriceComparing(LocalDate dateStart, LocalDate dateEnd) {

        List<Product> productList = productDao.findAll().orElseThrow(
                () -> new ProductNotFoundException("Product with this id doesn't exist")
        );

        List<ProductPriceComparingDto> productPriceComparingDtoList = new ArrayList<>();
        for (Product product : productList) {
            productPriceComparingDtoList.add(getItemPriceComparing(product.getId(), dateStart, dateEnd));
        }

        return productPriceComparingDtoList;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public ProductPriceComparingDto getItemPriceComparing(
            Long productId, LocalDate dateStart, LocalDate dateEnd) {

        Product product = productDao.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product with this id not found"));

        List<Item> itemList = itemDao.findProductsOnMarketByDateAndProduct(
                product, dateStart, dateEnd).orElseThrow(
                () -> new ItemNotFoundException("No product on market found")
        );

        List<LocalDate> datesBetween = dateStart.datesUntil(dateEnd).collect(Collectors.toList());

        Map<LocalDate, Map<String, Integer>> marketplaceEverydayPricesMap = new TreeMap<>();
        for (Item item : itemList) {
            item.setMarketplace((Marketplace) Hibernate.unproxy(item.getMarketplace()));
            List<LocalDate> datesOfProduct = item.getDateStart().datesUntil(item.getDateEnd())
                    .collect(Collectors.toList());
            for (LocalDate date : datesOfProduct) {
                if (datesBetween.contains(date)) {
                    if (marketplaceEverydayPricesMap.containsKey(date)) {
                        marketplaceEverydayPricesMap.get(date).put(
                                item.getMarketplace().getName(), item.getPrice());
                    } else {
                        Map<String, Integer> marketplacePriceMap = new HashMap<>();
                        marketplacePriceMap.put(item.getMarketplace().getName(), item.getPrice());
                        marketplaceEverydayPricesMap.put(date, marketplacePriceMap);
                    }
                }
            }
        }

        return new ProductPriceComparingDto(product.getName(), marketplaceEverydayPricesMap);

    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public Item createItem(Item item) {

        ItemCompositeId itemCompositeId = new ItemCompositeId(
                item.getPrice(), item.getDateStart(), item.getDateEnd(),
                item.getProduct(), item.getMarketplace()
        );

        if (itemDao.findById(itemCompositeId).isPresent()) {
            throw new ItemExistsException("Product on market already exists");
        }

        List<Item> productOnMarketListWithCertainProductAndMarketplace =
                itemDao.findAllByProductAndMarketplace(item.getProduct(),
                        item.getMarketplace()).orElseThrow(
                        () -> new ItemNotFoundException("Products on market not found"));

        for (Item certainItem : productOnMarketListWithCertainProductAndMarketplace) {
            if (Objects.equals(certainItem.getProduct().getId(), item.getProduct().getId()) &&
                    !(item.getDateStart().isBefore(certainItem.getDateStart()) &&
                            (item.getDateEnd().isBefore(certainItem.getDateStart()) ||
                                    item.getDateEnd().isEqual(certainItem.getDateStart())) ||
                            (item.getDateStart().isAfter(certainItem.getDateEnd()) ||
                                    item.getDateStart().isEqual(certainItem.getDateEnd())) &&
                                    item.getDateEnd().isAfter(certainItem.getDateEnd()))) {
                throw new ItemBadRequestException("Product for this period already added");
            }

        }

        Product product = productDao.findById(item.getProduct().getId()).orElseThrow(
                () -> new ProductNotFoundException("Product with this id doesn't exist")
        );
        Marketplace marketplace = marketPlaceDao.findById(item.getMarketplace().getId()).orElseThrow(
                () -> new MarketPlaceNotFoundException("Marketplace with this id not found")
        );

        item.setProduct(product);
        item.setMarketplace(marketplace);
        item.setSerialNumber((long) Math.abs(itemCompositeId.hashCode()));
        return itemDao.create(item).orElseThrow(
                () -> new ItemBadRequestException("Bad request"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public List<Item> createItems(List<Item> itemList) {

        List<Item> createdItems = new ArrayList<>();
        for (Item item : itemList) {
            createdItems.add(createItem(item));
        }

        return createdItems;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public void deleteItem(Long serialNumber) {

        Item item = itemDao.findBySerialNumber(serialNumber).orElseThrow(
                () -> new ItemNotFoundException("Products on market not found"));

        itemDao.deleteBySerialNumber(item.getSerialNumber());
    }

    private List<PriceByDayDto> createPriceByDayDtoList(List<Item> itemList,
                                                        List<LocalDate> datesBetween) {
        List<PriceByDayDto> priceByDayDtoList = new ArrayList<>();

        for (Item item : itemList) {

            List<LocalDate> datesOfProduct = item.getDateStart()
                    .datesUntil(item.getDateEnd()).collect(Collectors.toList());

            for (LocalDate date : datesOfProduct) {

                if (datesBetween.contains(date)) {
                    priceByDayDtoList.add(new PriceByDayDto(item.getPrice().toString(), date));
                }
            }
        }
        return priceByDayDtoList;
    }
}
