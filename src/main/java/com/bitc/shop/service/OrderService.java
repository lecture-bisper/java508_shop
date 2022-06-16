package com.bitc.shop.service;

import com.bitc.shop.dto.OrderDto;
import com.bitc.shop.dto.OrderHisDto;
import com.bitc.shop.dto.OrderItemDto;
import com.bitc.shop.entity.*;
import com.bitc.shop.repository.ItemImgRepository;
import com.bitc.shop.repository.ItemRepository;
import com.bitc.shop.repository.MemberRepository;
import com.bitc.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

  private final ItemRepository itemRepository;
  private final MemberRepository memberRepository;
  private final OrderRepository orderRepository;
  private final ItemImgRepository itemImgRepository;

  public Long order(OrderDto orderDto, String email) {
    // 지정한 상품 정보를 DB에서 조회하여 가져옴
    Item item = itemRepository.findById(orderDto.getItemId())
        .orElseThrow(EntityNotFoundException::new);

//    사용자 정보가 DB에서 조회하여 가져옴
    Member member = memberRepository.findByEmail(email);

    List<OrderItem> orderItemList = new ArrayList<>();
    OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
    orderItemList.add(orderItem);

//    사용자 정보와 주문할 상품 목록을 사용하여 Order 객체를 생성
    Order order = Order.createOrder(member, orderItemList);
    orderRepository.save(order); // 생성된 주문 정보 DB에 저장

    return order.getId();
  }

  @Transactional(readOnly = true)
  public Page<OrderHisDto> getOrderList(String email, Pageable pageable) {
    List<Order> orders = orderRepository.findOrders(email, pageable);
    Long totalCount = orderRepository.countOrder(email);

    List<OrderHisDto> orderHisDtos = new ArrayList<>();

    for (Order order : orders) {
      OrderHisDto orderHisDto = new OrderHisDto(order);
      List<OrderItem> orderItems = order.getOrderItems();
      for (OrderItem orderItem : orderItems) {
        ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
        OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
        orderHisDto.addOrderItemDto(orderItemDto);
      }

      orderHisDtos.add(orderHisDto);
    }

    return new PageImpl<OrderHisDto>(orderHisDtos, pageable, totalCount);
  }
}
