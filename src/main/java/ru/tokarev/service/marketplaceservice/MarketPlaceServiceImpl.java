package ru.tokarev.service.marketplaceservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tokarev.dao.marketplacedao.MarketPlaceDao;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.exception.marketplaceexception.MarketPlaceBadRequestException;
import ru.tokarev.exception.marketplaceexception.MarketPlaceExistsException;
import ru.tokarev.exception.marketplaceexception.MarketPlaceNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarketPlaceServiceImpl implements MarketPlaceService {

    private MarketPlaceDao<Marketplace> marketPlaceDao;

    @Autowired
    public void setMarketPlaceDao(MarketPlaceDao<Marketplace> marketPlaceDao) {
        this.marketPlaceDao = marketPlaceDao;
        this.marketPlaceDao.setClazz(Marketplace.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public Marketplace getById(Long id) {
        return marketPlaceDao.findById(id).orElseThrow(
                () -> new MarketPlaceNotFoundException("Marketplace with this id not found"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Marketplace> getAll() {

        List<Marketplace> marketplaceList = marketPlaceDao.findAll().orElseThrow(
                () -> new MarketPlaceNotFoundException("Marketplaces not found"));

        if (marketplaceList.size() == 0) {
            throw new MarketPlaceNotFoundException("Marketplaces not found");
        }

        return marketplaceList;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Marketplace createMarketPlace(Marketplace marketPlace) {
        if (marketPlaceDao.findByName(marketPlace.getName()).isPresent()) {
            throw new MarketPlaceExistsException("Marketplace with this name already exists");
        }

        return marketPlaceDao.create(marketPlace).orElseThrow(MarketPlaceBadRequestException::new);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public List<Marketplace> createMarketPlaces(List<Marketplace> marketplaceList) {

        List<Marketplace> createdMarketplaceList = new ArrayList<>();
        for (Marketplace marketplace : marketplaceList) {
            createdMarketplaceList.add(createMarketPlace(marketplace));
        }

        return createdMarketplaceList;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Marketplace updateMarketPlace(Long id, Marketplace marketPlace) {
        Marketplace existingMarketplace = marketPlaceDao.findById(id).orElseThrow(
                () -> new MarketPlaceNotFoundException("Marketplace with this id not found")
        );
        existingMarketplace.setName(marketPlace.getName());

        return marketPlaceDao.update(existingMarketplace).orElseThrow(MarketPlaceBadRequestException::new);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void deleteMarketPlace(Long id) {

        Marketplace marketplace = marketPlaceDao.findById(id).orElseThrow(
                () -> new MarketPlaceNotFoundException("Marketplaces not found")
        );

        marketPlaceDao.deleteById(marketplace.getId());
    }
}
