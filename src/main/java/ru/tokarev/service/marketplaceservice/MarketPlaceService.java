package ru.tokarev.service.marketplaceservice;

import ru.tokarev.entity.Marketplace;

import java.util.List;

public interface MarketPlaceService {

    Marketplace getById(Long id);

    List<Marketplace> getAll();

    Marketplace createMarketPlace(Marketplace marketPlace);

    List<Marketplace> createMarketPlaces(List<Marketplace> marketplaceList);

    Marketplace updateMarketPlace(Long id, Marketplace marketPlace);

    void deleteMarketPlace(Long id);
}
