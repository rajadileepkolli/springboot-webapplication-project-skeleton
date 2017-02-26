package com.example.persistance.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "persistent_logins")
public class PersistentRememberMeToken
{
    @Column(length = 64, nullable = false)
    private String username;
    @Id
    @Column(length = 64, nullable = false)
    private String series;
    @Column(name = "token", length = 64, nullable = false)
    private String tokenValue;
    @Column(name = "last_used", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;

    public PersistentRememberMeToken(String username, String series, String tokenValue,
            Date date)
    {
        this.username = username;
        this.series = series;
        this.tokenValue = tokenValue;
        this.date = date;
    }

    public String getUsername()
    {
        return username;
    }

    public String getSeries()
    {
        return series;
    }

    public String getTokenValue()
    {
        return tokenValue;
    }

    public Date getDate()
    {
        return date;
    }
}
