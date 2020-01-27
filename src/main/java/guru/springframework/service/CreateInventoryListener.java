package guru.springframework.service;

import guru.springframework.domain.BeerInventory;
import guru.springframework.events.NewInventoryEvent;
import guru.springframework.repositories.BeerInventoryRepository;
import guru.springframework.web.model.BeerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import static guru.springframework.config.JmsConfiguration.NEW_INVENTORY_QUEUE;

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
