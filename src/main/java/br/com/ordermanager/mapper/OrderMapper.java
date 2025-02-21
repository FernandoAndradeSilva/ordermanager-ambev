package br.com.ordermanager.mapper;


import br.com.ordermanager.dto.OrderDTO;
import br.com.ordermanager.entties.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "products", target = "products")
    Order toEntity(OrderDTO managedOrder);
}