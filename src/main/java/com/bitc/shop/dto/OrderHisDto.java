package com.bitc.shop.dto;

import com.bitc.shop.constant.OrderStatus;
import com.bitc.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderHisDto {
//  주문 이력에 관련된 데이터를 저장하는 클래스
//  주문 정보를 가져옴
  public OrderHisDto(Order order) {
    this.orderId = order.getId();
//    사용자가 지정한 방식으로 날짜 및 시간을 저장
    this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    this.orderStatus = order.getOrderStatus();
  }

  private Long orderId;

  private String orderDate;

  private OrderStatus orderStatus;

  private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

  public void addOrderItemDto(OrderItemDto orderItemDto) {
    orderItemDtoList.add(orderItemDto);
  }
}
