package com.mycompany.model.donation;

import com.mycompany.model.category.Category;
import com.mycompany.model.fundraising.Fundraising;
import com.mycompany.model.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter

@Entity
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Fundraising fundraisingID;
    @ManyToOne
    @JoinColumn
    private User userID;

    private double amount;

}
