package ru.tokarev.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tokarev.dto.MarketPlaceDto;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.service.marketplaceservice.MarketPlaceService;
import ru.tokarev.utils.MapperUtil;

import java.util.List;

@Slf4j
@RequestMapping("/api/marketplaces")
@RestController
public class MarketPlaceController {

    private final MarketPlaceService marketPlaceService;

    private final ModelMapper modelMapper;

    @Autowired
    public MarketPlaceController(MarketPlaceService marketPlaceService, ModelMapper modelMapper) {
        this.marketPlaceService = marketPlaceService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<MarketPlaceDto>> getAll() {

        List<Marketplace> marketplaceList = marketPlaceService.getAll();
        List<MarketPlaceDto> marketPlaceDtoList = MapperUtil.
                convertList(marketplaceList, this::convertToMarketPlaceDto);

        log.info("Response for GET request /marketplaces with data {}", marketPlaceDtoList);
        for(MarketPlaceDto marketPlaceDto : marketPlaceDtoList) {
            log.info("id {}, name {}", marketPlaceDto.getId(), marketPlaceDto.getName());
        }

        return new ResponseEntity<>(marketPlaceDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketPlaceDto> getById(@PathVariable Long id) {

        log.info("GET request /marketplaces/{} with data {}", id, id);

        Marketplace marketPlace = marketPlaceService.getById(id);
        MarketPlaceDto marketPlaceDto = convertToMarketPlaceDto(marketPlace);

        log.info("Response for GET request /marketplaces with data: id {}, name {}",
                marketPlaceDto.getId(), marketPlaceDto.getName());

        return new ResponseEntity<>(marketPlaceDto, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MarketPlaceDto> createMarketPlace(@RequestBody MarketPlaceDto marketPlaceDto) {

        log.info("POST request /marketplaces with data: name {}", marketPlaceDto.getName());

        Marketplace marketPlace = convertToMarketPlaceEntity(marketPlaceDto);
        Marketplace createdMarketplace = marketPlaceService.createMarketPlace(marketPlace);
        MarketPlaceDto createdMarketPlaceDto = convertToMarketPlaceDto(createdMarketplace);

        log.info("Response for POST request /marketplaces with data: id {}, name {}",
                createdMarketPlaceDto.getId(), createdMarketPlaceDto.getName());

        return new ResponseEntity<>(createdMarketPlaceDto, HttpStatus.CREATED);
    }

    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MarketPlaceDto>> createMarketplaces(
            @RequestBody List<MarketPlaceDto> marketPlaceDtoList) {

        log.info("POST request /marketplaces with data {}", marketPlaceDtoList);
        for(MarketPlaceDto marketPlaceDto : marketPlaceDtoList) {
            log.info("name {}", marketPlaceDto.getName());
        }

        List<Marketplace> marketplaceList = MapperUtil.convertList(marketPlaceDtoList, this::convertToMarketPlaceEntity);
        List<Marketplace> createdMarketplaceList = marketPlaceService.createMarketPlaces(marketplaceList);
        List<MarketPlaceDto> createdMarketplaceDtoList = MapperUtil.convertList(
                createdMarketplaceList, this::convertToMarketPlaceDto);

        log.info("Response for POST request /marketplaces with data {}", createdMarketplaceDtoList);
        for(MarketPlaceDto marketPlaceDto : createdMarketplaceDtoList) {
            log.info("id {}, name {}", marketPlaceDto.getId(), marketPlaceDto.getName());
        }

        return new ResponseEntity<>(createdMarketplaceDtoList, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MarketPlaceDto> updateMarketPlace(@PathVariable Long id,
                                                            @RequestBody MarketPlaceDto marketPlaceDto) {

        log.info("PATCH request /marketplaces/{} with data: name {}", id, marketPlaceDto.getName());

        Marketplace marketPlace = convertToMarketPlaceEntity(marketPlaceDto);
        Marketplace updatedMarketplace = marketPlaceService.updateMarketPlace(id, marketPlace);
        MarketPlaceDto updatedMarketPlaceDto = convertToMarketPlaceDto(updatedMarketplace);

        log.info("Response for PATCH request /marketplaces/{} with data: name {}", id, marketPlaceDto.getName());

        return new ResponseEntity<>(updatedMarketPlaceDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteMarketPlace(@PathVariable Long id) {

        log.info("DELETE request /marketplaces/{} with data {}", id, id);

        marketPlaceService.deleteMarketPlace(id);

        log.info("Response for DELETE request /marketplaces/{} with message {}", id,
                "Marketplace deleted successfully");

        return ResponseEntity.ok().body("Marketplace deleted successfully");
    }

    private MarketPlaceDto convertToMarketPlaceDto(Marketplace marketPlace) {
        return modelMapper.map(marketPlace, MarketPlaceDto.class);
    }

    private Marketplace convertToMarketPlaceEntity(MarketPlaceDto marketPlaceDto) {
        return modelMapper.map(marketPlaceDto, Marketplace.class);
    }
}
