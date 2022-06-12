package cn.edu.nju.cs.mapper;

import cn.edu.nju.cs.dto.ProductDto;
import cn.edu.nju.cs.model.Product;
import org.mapstruct.Mapper;

@Mapper
public interface ProductMapper {


    ProductDto toProductDto(Product pet);
}
