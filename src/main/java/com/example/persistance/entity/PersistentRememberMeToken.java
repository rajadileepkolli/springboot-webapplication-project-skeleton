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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "persistent_logins")
@NoArgsConstructor
@AllArgsConstructor
public class PersistentRememberMeToken
{

    @Id
    @Column(name = "SERIES", unique = true, nullable = false, length = 64)
    private String series;

    @Column(name = "USERNAME", nullable = false, length = 64)
    private String username;

    @Column(name = "TOKEN", nullable = false, length = 64)
    private String tokenValue;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "LAST_USED", nullable = false, length = 23)
    private Date date;

}
