package br.com.ordermanager.entties;

import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "`product`")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    private Integer quantity;
}
