package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Domain.OrderType;
import com.example.SafeTradeX.Model.Coin;
import com.example.SafeTradeX.Model.Order;
import com.example.SafeTradeX.Model.OrderItem;
import com.example.SafeTradeX.Model.User;

import java.util.List;

public interface OrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId) throws Exception;

    List<Order> getAllOrdersOfUser(Long userId);

    Order processOrder(Coin coin, Double quantity, OrderType orderType, User user) throws Exception;
}
