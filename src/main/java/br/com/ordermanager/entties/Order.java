package br.com.ordermanager.entties;

import br.com.ordermanager.enums.OrderStatus;
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

    @OneToMany(mappedBy = "order")
    private List<Product> products;

    private Double totalAmount;

    private OrderStatus status;
}

