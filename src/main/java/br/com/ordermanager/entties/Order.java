package br.com.ordermanager.entties;

import jakarta.persistence.*;
import lombok.Data;



import java.util.List;

@Data
@Entity
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Product> products;

    private Double totalAmount;

    private String status;
}

