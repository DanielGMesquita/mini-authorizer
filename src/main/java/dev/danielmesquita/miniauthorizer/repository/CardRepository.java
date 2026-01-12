package dev.danielmesquita.miniauthorizer.repository;

import dev.danielmesquita.miniauthorizer.entity.Card;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

  @Query("SELECT c FROM Card c WHERE c.cardNumber = :number")
  Optional<Card> findByCardNumber(String number);
}
