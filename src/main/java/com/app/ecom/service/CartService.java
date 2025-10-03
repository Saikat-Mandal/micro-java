package com.app.ecom.service;

import com.app.ecom.Exception.OutOfStockException;
import com.app.ecom.Exception.ResourceNotFoundException;
import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public void addToCart(String userId, CartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getActive()) {
            throw new ResourceNotFoundException("Product not found");
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new OutOfStockException("Only " + product.getStockQuantity() + " items available in stock");
        }

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CartItem existingCartitem = cartItemRepository.findByUserAndProduct(user, product);

        if (existingCartitem != null) {
            int newQuantity = existingCartitem.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStockQuantity()) {
                throw new OutOfStockException("Only " + product.getStockQuantity() + " items available in stock");
            }
            existingCartitem.setQuantity(newQuantity);
            existingCartitem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            cartItemRepository.save(existingCartitem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(cartItem);
        }
    }

    public void deleteFromCart(String userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getActive()) {
            throw new ResourceNotFoundException("Product not found");
        }

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        cartItemRepository.deleteByUserAndProduct(user, product);

    }

    public List<CartItem> getCartForUser(String userId) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return cartItemRepository.findByUser(user);
    }
}
