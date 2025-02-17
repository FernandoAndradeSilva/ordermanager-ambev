package br.com.ordermanager.repository;


import br.com.ordermanager.entties.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Order, Long> {

}
