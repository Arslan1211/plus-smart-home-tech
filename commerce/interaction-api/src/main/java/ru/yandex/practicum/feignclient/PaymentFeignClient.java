package ru.yandex.practicum.feignclient;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentFeignClient {

    @PostMapping
    PaymentDto payment(@RequestBody @Valid OrderDto order);

    @PostMapping("/totalCost")
    OrderDto getTotalCost(@RequestBody @Valid OrderDto order);

    @PostMapping("/refund")
    void paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping("/productCost")
    BigDecimal productCost(@RequestBody @Valid OrderDto order);

    @PostMapping("/failed")
    void paymentFailed(@RequestBody UUID paymentId);

}
