package com.elshoura.lokit.service;

import com.elshoura.lokit.errors.exception.NotFoundException;
import com.elshoura.lokit.errors.exception.OrderStatusNotAllowedException;
import com.elshoura.lokit.errors.exception.UserForbiddenException;
import com.elshoura.lokit.models.dto.request.UpdateOrderStatusRequest;
import com.elshoura.lokit.models.dto.response.OrderItemResponse;
import com.elshoura.lokit.models.dto.response.OrderResponse;
import com.elshoura.lokit.models.entitys.Address;
import com.elshoura.lokit.models.entitys.Order;
import com.elshoura.lokit.models.entitys.OrderItem;
import com.elshoura.lokit.models.entitys.User;
import com.elshoura.lokit.repository.OrderItemRepository;
import com.elshoura.lokit.repository.OrderRepository;
import com.elshoura.lokit.repository.ProductImageRepository;
import com.elshoura.lokit.utils.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Long userId){

        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toOrderResponse)
                .toList();

  }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getMyOrderById(Long userId,Long orderId){

  Order order = orderRepository.findById(orderId)
          .orElseThrow(()-> new NotFoundException("Order not found"));

    if (!order.getUser().getId().equals(userId) ){
        throw new UserForbiddenException("Order does not belong to this user");
    }

    return toOrderResponse(order);

    }

    private OrderResponse toOrderResponse(Order order){

        User user = order.getUser();
        Address address = order.getAddress();

        List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId())
                .stream()
                .map(this::toOrderItemResponse)
                .toList();

        return OrderResponse.builder()

                .id(order.getId())

                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())

                .addressId(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .governorate(address.getGovernorate())
                .country(address.getCountry())
                .zipCode(address.getZipCode())

                .items(items)

                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())

                .subtotal(order.getSubtotal())
                .shipping(order.getShipping())
                .tax(order.getTax())
                .totalPrice(order.getTotalPrice())

                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .deliveredAt(order.getDeliveredAt())
                .cancelledAt(order.getCancelledAt())

                .build();

    }
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        OrderStatus newStatus = request.getStatus();

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderStatusNotAllowedException("Delivered order status cannot be changed");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderStatusNotAllowedException("Cancelled order status cannot be changed");
        }

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(java.time.LocalDateTime.now());
        }

        if (newStatus == OrderStatus.CANCELLED) {
            order.setCancelledAt(java.time.LocalDateTime.now());
        }

        Order savedOrder = orderRepository.save(order);

        return toOrderResponse(savedOrder);
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item){

        Long productId = item.getVariant().getProduct().getId();

        String imageUrl = productImageRepository.findFirstByProductIdAndIsMainTrue(productId)
                .or(() -> productImageRepository.findFirstByProductIdOrderByIdAsc(productId))
                .map(productImage -> productImage.getImageUrl())
                .orElse(null);
               // .orElseThrow(() -> new NotFoundException("Image not found for product id: " + productId));

        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(productId)
                .productName(item.getVariant().getProduct().getName())

                .variantId(item.getVariant().getId())
                .colorName(item.getVariant().getColor().getName())
                .sizeName(item.getVariant().getSize().getName())

                .imageUrl(imageUrl)

                .price(item.getPrice())
                .quantity(item.getQuantity())
                .lineTotal(item.getLineTotal())
                .build();

    }
    @Override
    @Transactional
    public OrderResponse cancelMyOrder(Long userId, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new UserForbiddenException("Order does not belong to this user");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderStatusNotAllowedException("Order is already cancelled");
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderStatusNotAllowedException("Delivered order cannot be cancelled");
        }

        if (order.getStatus() == OrderStatus.ON_THE_ROAD) {
            throw new OrderStatusNotAllowedException("Order cannot be cancelled because it is already on the road");
        }

        if (order.getStatus() != OrderStatus.ORDER_PLACED
                && order.getStatus() != OrderStatus.PACKAGING) {
            throw new OrderStatusNotAllowedException("Order cannot be cancelled at this stage");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(java.time.LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        return toOrderResponse(savedOrder);
    }

}
