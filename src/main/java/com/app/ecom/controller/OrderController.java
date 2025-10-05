package com.app.ecom.controller;

import com.app.ecom.dto.OrderResponse;
import com.app.ecom.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestHeader("X-User-ID") String userId) {
        Optional<OrderResponse> order = orderService.createOrder(userId);
        if(order.isPresent()) {
            return new ResponseEntity<>(order.get(), HttpStatus.CREATED);
        }
        return new ResponseEntity<>("No order created", HttpStatus.BAD_REQUEST);
    }
}
