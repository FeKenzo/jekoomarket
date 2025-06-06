package com.jekoomarket.services;

import com.jekoomarket.models.Order;
import com.jekoomarket.models.Product;
import com.jekoomarket.models.User;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order placeOrder(User user, Product product, int quantity, Map<String, String> deliveryAddress, String paymentMethod);
    long countAllOrders(); // New method
    List<Order> findByUser(User user);
}
