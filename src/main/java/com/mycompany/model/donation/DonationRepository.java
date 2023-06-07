package com.mycompany.model.donation;

import com.mycompany.model.category.Category;
import com.mycompany.model.fundraising.Fundraising;
import com.mycompany.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DonationRepository extends CrudRepository<Donation, Long> {


}