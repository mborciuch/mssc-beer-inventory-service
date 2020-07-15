package guru.sfg.beer.inventory.service.service;

import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.brewery.model.events.NewInventoryEvent;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.brewery.model.BeerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import static guru.sfg.beer.inventory.service.config.JmsConfiguration.NEW_INVENTORY_QUEUE;

@Slf4j
@RequiredArgsConstructor
@Service
public class CreateInventoryListener {

    private final BeerInventoryRepository beerInventoryRepository;

    @JmsListener(destination = NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent newInventoryEvent){
        BeerDto beerDto = newInventoryEvent.getBeerDto();

        BeerInventory beerInventory = BeerInventory.builder()
                .beerId(beerDto.getId())
                .upc(beerDto.getUpc())
                .quantityOnHand(beerDto.getQuantityOnHand())
                .build();

        beerInventoryRepository.save(beerInventory);
        log.debug("saved new inventory: " + beerInventory.getBeerId() + " with quantityOnHand" + beerInventory.getQuantityOnHand());
    }

}
