package com.bitc.shop.controller;

import com.bitc.shop.dto.CartDetailDto;
import com.bitc.shop.dto.CartItemDto;
import com.bitc.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @ResponseBody
  @RequestMapping(value = "/cart", method = RequestMethod.POST)
  public ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal) {

    if (bindingResult.hasErrors()) {
      StringBuilder sb = new StringBuilder();
      List<FieldError> fieldErrors = bindingResult.getFieldErrors();

      for (FieldError fieldError : fieldErrors) {
        sb.append(fieldError.getDefaultMessage());
      }

      return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
    }

//    현재 로그인된 사용자 정보 가져오기
    String email = principal.getName();

    Long cartItemId;

    try {
      cartItemId = cartService.addCart(cartItemDto, email);
    }
    catch (Exception e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
  }

  @RequestMapping(value = "/cart", method = RequestMethod.GET)
  public String orderHis(Principal principal, Model model) {
    List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());
    model.addAttribute("cartItems", cartDetailList);

    return "cart/cartList";
  }
}
