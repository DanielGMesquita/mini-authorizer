package dev.danielmesquita.miniauthorizer.repository;

import dev.danielmesquita.miniauthorizer.entity.Card;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

  @Query(value = "SELECT * FROM tb_card WHERE card_number = :number FOR UPDATE", nativeQuery = true)
  Optional<Card> findByCardNumberForUpdate(String number);

  Optional<Card> findByCardNumber(String number);
}
