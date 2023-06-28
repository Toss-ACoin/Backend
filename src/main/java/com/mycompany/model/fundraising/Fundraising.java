package com.mycompany.model.fundraising;

import com.mycompany.model.category.Category;
import com.mycompany.model.image.Image;
import lombok.*;

import com.mycompany.model.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Entity
public class Fundraising {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OwnerID")
    private User owner;

    @ManyToMany
    @Column(name = "CategoryID")
    private List<Category> category;

    private int goal;

    private int collectedMoney;

    private String title;

    @Temporal(TemporalType.DATE)
    private Date fundraisingStart;

    @Temporal(TemporalType.DATE)
    private Date fundraisingEnd;

    @OneToMany
    private List<Image> pictures;

    @Type(type="text")
    private String description;

    private boolean available;

}
