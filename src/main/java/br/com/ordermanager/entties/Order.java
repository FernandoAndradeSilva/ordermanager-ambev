package br.com.ordermanager.entties;

import br.com.ordermanager.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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

    @Override
    public String toString() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        return "Order{\n" +
                "id=" + id + ",\n" +
                "produtos=" + products + ",\n" +
                "valorTotal=" + currencyFormat.format(totalAmount) + ",\n" +
                "status='" + status + '\'' + "\n" +
                '}';
    }
}