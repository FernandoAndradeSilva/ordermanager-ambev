package br.com.ordermanager.entties;

import jakarta.persistence.*;
import lombok.Data;

import java.text.NumberFormat;
import java.util.Locale;

@Data
@Entity
@Table(name = "`products`")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Override
    public String toString() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        return "Product{id=" + id +
                ", nome='" + name + '\'' +
                ", preço=" + currencyFormat.format(price) +
                ", quantidade=" + quantity +
                '}';
    }
}