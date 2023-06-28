package com.mycompany.model.fundraising;

import com.mycompany.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FundraisingRepository extends JpaRepository<Fundraising, Long> {

    List<Fundraising> findAllByOwnerAndAvailableIsTrueOrderByCollectedMoney(User user);
    List<Fundraising> findAllByAvailableIsTrueOrderByFundraisingStart();
    Optional<Fundraising> findById(Long id);
    Fundraising getFundraisingByTitle(String title);
    Page<Fundraising> findAllByTitleContainsOrDescriptionContains(String title, String description, Pageable pageable);

}
