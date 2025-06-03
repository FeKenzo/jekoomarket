package com.jekoomarket.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate orderDate;
    private String status;
    private Double totalAmount;

    private String deliveryCep;
    private String deliveryLogradouro;
    private String deliveryNumero;
    private String deliveryComplemento;
    private String deliveryBairro;
    private String deliveryCidade;
    private String deliveryEstado;

    private String paymentMethod;
    private LocalDate estimatedDeliveryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getDeliveryCep() { return deliveryCep; }
    public void setDeliveryCep(String deliveryCep) { this.deliveryCep = deliveryCep; }
    public String getDeliveryLogradouro() { return deliveryLogradouro; }
    public void setDeliveryLogradouro(String deliveryLogradouro) { this.deliveryLogradouro = deliveryLogradouro; }
    public String getDeliveryNumero() { return deliveryNumero; }
    public void setDeliveryNumero(String deliveryNumero) { this.deliveryNumero = deliveryNumero; }
    public String getDeliveryComplemento() { return deliveryComplemento; }
    public void setDeliveryComplemento(String deliveryComplemento) { this.deliveryComplemento = deliveryComplemento; }
    public String getDeliveryBairro() { return deliveryBairro; }
    public void setDeliveryBairro(String deliveryBairro) { this.deliveryBairro = deliveryBairro; }
    public String getDeliveryCidade() { return deliveryCidade; }
    public void setDeliveryCidade(String deliveryCidade) { this.deliveryCidade = deliveryCidade; }
    public String getDeliveryEstado() { return deliveryEstado; }
    public void setDeliveryEstado(String deliveryEstado) { this.deliveryEstado = deliveryEstado; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public LocalDate getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
}