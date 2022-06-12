package cn.edu.nju.cs.mapper;

import cn.edu.nju.cs.dto.CartDto;
import cn.edu.nju.cs.dto.CartItemDto;
import cn.edu.nju.cs.dto.ProductDto;
import cn.edu.nju.cs.model.Cart;
import cn.edu.nju.cs.model.Item;
import org.mapstruct.Mapper;

@Mapper
public interface CartMapper {

    Cart toCart(CartDto cartDto);

    CartDto toCartDto(Cart cart);

    default Item toItem(CartItemDto itemDto) {
        return new Item(
                itemDto.getProduct().getId(),
                itemDto.getProduct().getName(),
                itemDto.getProduct().getPrice(),
                itemDto.getAmount()
        );
    }

    default CartItemDto toItemDto(Item item){
        return new CartItemDto()
                .amount(item.getQuantity())
                .product(new ProductDto()
                        .id(item.getId())
                        .price(item.getPrice())
                        .name(item.getName()));
    }
}
