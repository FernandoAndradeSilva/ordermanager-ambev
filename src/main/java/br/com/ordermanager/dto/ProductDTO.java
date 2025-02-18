package br.com.ordermanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDTO {

    private String name;

    private Double price;

    private Integer quantity;

}
