package com.mycompany.model.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t.amount, COUNT(t.amount) FROM Transaction t WHERE t.fundraisingId = :id GROUP BY t.amount")
    List<String> selectAmountCount(@Param("id") long id);

    List<Transaction> findAllByFundraisingId(Long id);
}
