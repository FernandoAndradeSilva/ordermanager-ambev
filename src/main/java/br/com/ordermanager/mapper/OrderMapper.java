package br.com.ordermanager.mapper;


import br.com.ordermanager.dto.OrderDTO;
import br.com.ordermanager.entties.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDTO toDTO(Order order);

    Order toEntity(OrderDTO managedOrder);
}