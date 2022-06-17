package com.bitc.shop.controller;

import com.bitc.shop.dto.OrderDto;
import com.bitc.shop.dto.OrderHisDto;
import com.bitc.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

//  @ResponseBody : 해당 어노테이션 사용 시 반환값을 클라이언트로 데이터만 전달함
  @ResponseBody
  @PostMapping(value = "/order")
  public ResponseEntity order (@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult, Principal principal) {
//    사용자가 입력한 필드에 오류가 있는지 확인하고 오류가 있을 경우 그 오류 정보를 다시 클라이언트에게 전달(데이터만 전달)
    if (bindingResult.hasErrors()) {
//      StringBuilder : 문자열을 하나의 주소에서 생성 및 추가해주는 클래스 타입, 메모리를 효율적으로 사용함
      StringBuilder sb = new StringBuilder();
      List<FieldError> fieldErrors = bindingResult.getFieldErrors();

      for (FieldError fieldError : fieldErrors) {
        sb.append(fieldError.getDefaultMessage());
      }

      return new ResponseEntity<String>(sb.toString(),HttpStatus.BAD_REQUEST);
    }

    String email = principal.getName(); // 사용자 정보 가져옴
    Long orderId; // 주문 정보가 저장될 변수

//    주문 정보 생성
    try {
      orderId = orderService.order(orderDto, email);
    }
    catch (Exception e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

//    생성된 주문 id와 서버 통신 상태 정보를 클라이언트에게 전달
    return new ResponseEntity<Long>(orderId, HttpStatus.OK);
  }

  @GetMapping(value = {"/orders", "/orders/{page}"})
  public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model) {
    Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);

    Page<OrderHisDto> ordersHisDtoList = orderService.getOrderList(principal.getName(), pageable);

    model.addAttribute("orders", ordersHisDtoList);
    model.addAttribute("page", pageable.getPageNumber());
    model.addAttribute("maxPage", 5);

    return "order/orderHis";
  }

  @ResponseBody
  @RequestMapping(value = "/order/{orderId}/cancel", method = RequestMethod.POST)
  public ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal) {
//    현재 사용자 정보 및 주문id를 기준으로 주문자와 현재 사용자가 같은지 확인
    if (!orderService.validateOrder(orderId, principal.getName())) {
      return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }

//    주문 취소
    orderService.cancelOrder(orderId);
    return new ResponseEntity<Long>(orderId, HttpStatus.OK);
  }
}
