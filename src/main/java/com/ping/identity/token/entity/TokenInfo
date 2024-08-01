package com.pingidentity.token.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "token")
@Data
public class TokenInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "token_id")
    private String tokenId;
    @Column(name = "before_time")
    private Instant beforeTime;
    @Column(name = "after_time")
    private Instant afterTime;
    @Column(name = "status")
    private boolean status;
}
