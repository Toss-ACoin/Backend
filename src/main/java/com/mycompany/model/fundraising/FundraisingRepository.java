package com.mycompany.model.fundraising;

import com.mycompany.model.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FundraisingRepository extends CrudRepository<Fundraising, Long> {

    List<Fundraising> findAllByOwnerAndAvailableIsTrueOrderByCollectedMoney(User user);
    List<Fundraising> findAllByAvailableIsTrueOrderByFundraisingStart();
    Optional<Fundraising> findById(Long id);
}
