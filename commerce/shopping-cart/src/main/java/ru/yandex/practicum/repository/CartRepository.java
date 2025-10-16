package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.ShoppingCart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<ShoppingCart, UUID> {
    Optional<ShoppingCart> findAllByUsername(String username);
}