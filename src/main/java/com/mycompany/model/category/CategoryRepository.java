package com.mycompany.model.category;

import com.mycompany.model.fundraising.Fundraising;
import com.mycompany.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    Optional<Category> findById(Long id);
}