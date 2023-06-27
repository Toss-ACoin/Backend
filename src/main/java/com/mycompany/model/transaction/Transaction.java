package com.mycompany.model.transaction;

import lombok.*;
import java.util.Date;


import javax.persistence.*;

@Setter
@Getter
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long fundraisingId;
    private Long payerId;
    private int amount;
    @Temporal(TemporalType.DATE)
    private Date date;

    @Enumerated
    @Column(name = "Type")
    private TransactionType transactionType;

}
