package ru.yandex.practicum.feignclient;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryFeignClient {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody @Valid DeliveryDto delivery);

    @PostMapping("/successful")
    void deliverySuccessful(@RequestBody UUID deliveryId);

    @PostMapping("/picked")
    void deliveryPicked(@RequestBody UUID deliveryId);

    @PostMapping("/failed")
    void deliveryFailed(@RequestBody UUID deliveryId);

    @PostMapping("/cost")
    BigDecimal deliveryCost(@RequestBody @Valid OrderDto order);

}