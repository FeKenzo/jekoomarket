package com.jekoomarket.services;

import com.jekoomarket.models.Order;
import com.jekoomarket.models.OrderItem;
import com.jekoomarket.models.Product;
import com.jekoomarket.models.User;
import com.jekoomarket.repositories.OrderRepository;
import com.jekoomarket.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository; // Keep this if needed elsewhere or remove if not.

    @Override
    @Transactional
    public Order placeOrder(User user, Product productToBuy, int quantity, Map<String, String> deliveryAddress, String paymentMethod) {
        logger.info("Iniciando processo de criação de pedido para o usuário: {} e produto: {}", user.getEmail(), productToBuy.getTitle());

        Optional<Product> productOptional = productRepository.findById(productToBuy.getId());
        if (!productOptional.isPresent()) {
            logger.error("Produto com ID {} não encontrado ao tentar criar pedido.", productToBuy.getId());
            throw new RuntimeException("Produto não encontrado com ID: " + productToBuy.getId());
        }
        Product product = productOptional.get();

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING_PAYMENT");
        order.setPaymentMethod(paymentMethod);

        order.setDeliveryCep(deliveryAddress.getOrDefault("cep", user.getCep()));
        order.setDeliveryLogradouro(deliveryAddress.getOrDefault("logradouro", user.getLogradouro()));
        order.setDeliveryNumero(deliveryAddress.getOrDefault("numero", user.getNumero()));
        order.setDeliveryComplemento(deliveryAddress.getOrDefault("complemento", user.getComplemento()));
        order.setDeliveryBairro(deliveryAddress.getOrDefault("bairro", user.getBairro()));
        order.setDeliveryCidade(deliveryAddress.getOrDefault("cidade", user.getCidade()));
        order.setDeliveryEstado(deliveryAddress.getOrDefault("estado", user.getEstado()));

        order.setEstimatedDeliveryDate(LocalDate.now().plusDays(7));

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPriceAtPurchase(product.getPrice());

        order.addOrderItem(orderItem);

        double total = product.getPrice() * quantity;
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);
        logger.info("Pedido criado com sucesso. ID do Pedido: {}", savedOrder.getId());
        return savedOrder;
    }

    @Override
    public long countAllOrders() { // Implementation of new method
        return orderRepository.count();
    }

    @Override
    public List<Order> findByUser(User user) {
        logger.info("Buscando pedidos para o usuário: {}", user.getEmail());
        return orderRepository.findByUserId(user.getId());
    }
}
