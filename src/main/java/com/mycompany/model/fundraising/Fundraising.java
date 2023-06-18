package com.mycompany.model.fundraising;

import com.mycompany.model.category.Category;
import com.mycompany.model.donation.Donation;
import lombok.*;

import com.mycompany.model.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Entity
public class Fundraising {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OwnerID")
    private User owner;

    @ManyToMany
    @Column(name = "CategoryID")
    private List<Category> category;

    @OneToMany
    private List<Donation> donations;

    private int goal;

    private int collectedMoney;

    private String title;

    @Temporal(TemporalType.DATE)
    private Date fundraisingStart;

    @Temporal(TemporalType.DATE)
    private Date fundraisingEnd;

    @Lob
    private byte[] image;

    @Type(type="text")
    private String description;


    private boolean available;

    public String getBasicInfo(){
        return "{\n\t" +
                "\"fundraising_start\": \""+ this.fundraisingStart+"\"\n,\t"+
                "\"fundraising_end\": \""+ this.fundraisingEnd+"\"\n,\t"+
                "\"title\": \""+ this.title+"\"\n,\t"+
                "\"collected_money\": \""+ this.collectedMoney+"\"\n,\t"+
                "\"goal\": \""+ this.goal+"\"\n,\t"+
                "\"image\": \""+ Arrays.toString(this.image) +"\"\n,\t"+
                "\"owner_name\": \""+ this.owner.getName()+"\"\n,\t"+
                "\"owner_surname\": \""+ this.owner.getSurname()+"\"\n"+
                "}";
    }

}
