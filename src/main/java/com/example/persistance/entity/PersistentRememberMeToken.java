package com.example.persistance.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "persistent_logins")
@AllArgsConstructor
@Getter
@Setter
public class PersistentRememberMeToken
{
    @Id
    @Column(length = 64, nullable = false)
    private String series;

    @Column(length = 64, nullable = false, unique = true)
    private String username;

    @Column(name = "token", length = 64, nullable = false)
    private String tokenValue;

    @Column(name = "last_used", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;
}
