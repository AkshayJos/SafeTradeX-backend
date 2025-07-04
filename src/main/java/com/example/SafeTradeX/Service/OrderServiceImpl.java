package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Domain.OrderStatus;
import com.example.SafeTradeX.Domain.OrderType;
import com.example.SafeTradeX.Model.*;
import com.example.SafeTradeX.Repository.OrderItemRepository;
import com.example.SafeTradeX.Repository.OrderRepository;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private  AssetService assetService;

    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {
        Double price = orderItem.getCoin().getCurrentPrice() * orderItem.getQuantity();

        Order order = new Order();

        order.setUser(user);
        order.setOrderItem(orderItem);
        order.setOrderType(orderType);
        order.setPrice(BigDecimal.valueOf(price));
        order.setTimestamp(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) throws Exception {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found!"));
    }

    @Override
    public List<Order> getAllOrdersOfUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    private OrderItem createOrderItem(Coin coin, Double quantity, Double buyPrice, Double sellPrice){
        OrderItem orderItem = new OrderItem();

        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);

        return orderItemRepository.save(orderItem);
    }

    @Transactional
    public Order buyAsset(Coin coin, Double quantity, User user) throws Exception {
        if(quantity <= 0){
            throw new Exception("Quantity must be greater than 0");
        }

        Double buyprice = coin.getCurrentPrice();
        BigDecimal buyPrice1 = BigDecimal.valueOf(buyprice);
        BigDecimal totalCost = buyPrice1.multiply(BigDecimal.valueOf(quantity));
        Wallet wallet = walletService.getUserWallet(user);

        System.out.println("Wallet Balance: " + wallet.getBalance().toPlainString());
        System.out.println("Total Cost: " + totalCost.toPlainString());
        System.out.println("Comparison Result: " + wallet.getBalance().compareTo(totalCost));
        System.out.println("Wallet Balance Type: " + wallet.getBalance().getClass());


        if(wallet.getBalance().compareTo(totalCost) >= 0){
            OrderItem orderItem = createOrderItem(coin, quantity, buyprice, (double)0);
            Order order = createOrder(user, orderItem, OrderType.BUY);

            orderItem.setOrder(order);

            walletService.payOrderPayment(order, user);

            order.setStatus(OrderStatus.SUCCESS);
            order.setOrderType(OrderType.BUY);

            Order savedOrder = orderRepository.save(order);

            Asset oldAsset = assetService.findAssetByUserIdAndCoinId(order.getUser().getId(), order.getOrderItem().getCoin().getId());

            if(oldAsset == null){
                assetService.createAsset(user, orderItem.getCoin(), orderItem.getQuantity());
            }
            else{
                assetService.updateAsset(oldAsset.getId(), quantity);
            }

            return savedOrder;
        }

        throw new Exception("Insufficient Balance!");
    }

    @Transactional
    public Order sellAsset(Coin coin, Double quantity, User user) throws Exception {
        if(quantity <= 0){
            throw new Exception("Quantity must be greater than 0");
        }

        Double sellPrice = coin.getCurrentPrice();

        Asset assetToSell = assetService.findAssetByUserIdAndCoinId(user.getId(), coin.getId());

        if(assetToSell != null) {

            Double buyPrice = assetToSell.getBuyPrice();
            OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, sellPrice);

            Order order = createOrder(user, orderItem, OrderType.SELL);
            orderItem.setOrder(order);

            if (assetToSell.getQuantity() >= quantity) {
                order.setStatus(OrderStatus.SUCCESS);
                order.setOrderType(OrderType.SELL);
                Order savedOrder = orderRepository.save(order);

                walletService.payOrderPayment(order, user);

                Asset updatedAsset = assetService.updateAsset(assetToSell.getId(), -quantity);

                if (updatedAsset.getQuantity() * coin.getCurrentPrice() <= 1) {
                    assetService.deleteAsset(updatedAsset.getId());
                }
                return savedOrder;
            }
            throw new Exception("Insufficient quantity to sell!");
        }
        throw  new Exception("Asset not found!");
    }

    @Override
    @Transactional
    public Order processOrder(Coin coin, Double quantity, OrderType orderType, User user) throws Exception {
        if(orderType.equals(OrderType.BUY)){
            return  buyAsset(coin, quantity, user);
        }
        else if(orderType.equals(OrderType.SELL)){
            return sellAsset(coin, quantity, user);
        }

        throw new Exception("Invalid order type!");
    }
}
