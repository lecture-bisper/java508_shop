package com.bitc.shop.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "cart_item")
@Data
public class CartItem extends BaseEntity {
  @Id
  @Column(name = "cart_item_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id")
  private Cart cart;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id")
  private Item item;

  private int count;

  public static CartItem createCartItem(Cart cart, Item item, int count) {
    CartItem cartItem = new CartItem();
    cartItem.setCart(cart);
    cartItem.setItem(item);
    cartItem.setCount(count);

    return cartItem;
  }

//  장바구니에 있는 상품의 개수를 추가
  public void addCount(int count) {
    this.count = count;
  }
}
