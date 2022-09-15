package ru.tokarev.service.marketplaceservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tokarev.dao.marketplacedao.MarketPlaceDao;
import ru.tokarev.entity.Marketplace;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MarketPlaceServiceTest {

    @Mock
    private MarketPlaceDao<Marketplace> marketPlaceDao;

    @InjectMocks
    private MarketPlaceServiceImpl marketPlaceService;

    @Test
    void getById() {

        //arrange
        Marketplace existingMarketplace = new Marketplace(1L, "Perekrestok");
        given(marketPlaceDao.findById(1L)).willReturn(Optional.of(existingMarketplace));

        //act
        Marketplace marketplace = marketPlaceService.getById(1L);

        //assert
        assertEquals(marketplace, existingMarketplace);
    }

    @Test
    void getAll() {

        //arrange
        Marketplace existingMarketplace1 = new Marketplace(1L, "Perekrestok");
        Marketplace existingMarketplace2 = new Marketplace(2L, "Magnit");
        given(marketPlaceDao.findAll()).willReturn(Optional.of(List.of(existingMarketplace1, existingMarketplace2)));

        //act
        List<Marketplace> marketplaceList = marketPlaceService.getAll();

        //assert
        assertEquals(marketplaceList, List.of(existingMarketplace1, existingMarketplace2));

    }

    @Test
    void createMarketPlace() {

        //arrange
        Marketplace marketplaceToCreate = new Marketplace(1L, "Perekrestok");
        given(marketPlaceDao.create(marketplaceToCreate)).willReturn(Optional.of(marketplaceToCreate));

        //act
        Marketplace marketplace = marketPlaceService.createMarketPlace(marketplaceToCreate);

        //assert
        assertEquals(marketplace, marketplaceToCreate);

    }

    @Test
    void updateMarketPlace() {
        //arrange
        Marketplace existingMarketplace = new Marketplace(1L, "Perekrestok");
        Marketplace updatedMarketplace = new Marketplace(1L, "Magnit");
        given(marketPlaceDao.findById(1L)).willReturn(Optional.of(existingMarketplace));
        given(marketPlaceDao.update(existingMarketplace)).willReturn(Optional.of(updatedMarketplace));

        //act
        Marketplace marketplace = marketPlaceService.updateMarketPlace(1L, existingMarketplace);

        //assert
        assertEquals(marketplace, updatedMarketplace);
    }

    @Test
    void deleteMarketPlace() {
        //arrange
        Marketplace existingMarketplace = new Marketplace(1L, "Perekrestok");
        given(marketPlaceDao.findById(1L)).willReturn(Optional.of(existingMarketplace));
        willDoNothing().given(marketPlaceDao).deleteById(1L);

        //act
        marketPlaceService.deleteMarketPlace(1L);

        //assert
        verify(marketPlaceDao, times(1)).deleteById(1L);
    }
}