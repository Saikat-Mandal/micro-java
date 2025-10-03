package com.app.ecom.controller;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.model.CartItem;
import com.app.ecom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartItemController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestHeader("X-User-ID") String userId,
                                          @RequestBody CartItemRequest request) {
        cartService.addToCart(userId , request);
        return new ResponseEntity<>( "Added to cart successfully",HttpStatus.OK);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<?> removeFromCart(@RequestHeader("X-User-ID") String userId ,@PathVariable Long productId) {
        cartService.deleteFromCart(userId , productId);
        return new ResponseEntity<>( "Removed from cart successfully",HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCartForUser(@RequestHeader("X-User-ID") String userId) {
        return new ResponseEntity<>(cartService.getCartForUser(userId), HttpStatus.OK);
    }
}
