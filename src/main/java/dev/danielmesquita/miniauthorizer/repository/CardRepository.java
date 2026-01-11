package dev.danielmesquita.miniauthorizer.repository;

import dev.danielmesquita.miniauthorizer.entity.Card;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM Card c WHERE c.cardNumber = :number")
  Optional<Card> findByCardNumberForUpdate(String number);
}
