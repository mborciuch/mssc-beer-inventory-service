package guru.sfg.beer.inventory.service.service;

import guru.sfg.beer.inventory.service.config.JmsConfiguration;
import guru.sfg.brewery.model.events.AllocateOrderRequest;
import guru.sfg.brewery.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationListener {
    private final AllocateService allocateService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfiguration.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderRequest request){
        AllocateOrderResult.AllocateOrderResultBuilder builder = AllocateOrderResult.builder();
        builder.beerOrderDto(request.getBeerOrderDto());

        try {
            Boolean allocationResult = allocateService.allocateOrder(request.getBeerOrderDto());

            if(allocationResult){
                builder.pendingInventory(false);
            } else {
                builder.pendingInventory(true);
            }
        }catch (Exception e){
            builder.allocationError(true);
        }
        jmsTemplate.convertAndSend(JmsConfiguration.ALLOCATE_ORDER_RESPONSE_QUEUE, builder.build());
    }

}
