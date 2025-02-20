package br.com.ordermanager.dto;

import br.com.ordermanager.entties.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {

    @OneToMany(cascade = CascadeType.ALL)
    private List<ProductDTO> products;

    private Double totalAmount;

    private String status;

    @Override
    public String toString() {
        return "OrderDTO{" +
                "produtos=" + products +
                ", valorTotal=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }

}
