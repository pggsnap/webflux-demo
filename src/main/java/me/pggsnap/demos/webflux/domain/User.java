package me.pggsnap.demos.webflux.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.pggsnap.demos.webflux.config.IgnoreSentisiveContentSerializer;

import java.time.Instant;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author pggsnap
 * @date 2020/4/13
 */
public class User {
    private String username;

    @JsonSerialize(using = IgnoreSentisiveContentSerializer.class)
    private String password;

    private Integer state;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("username='" + username + "'")
                .add("password='" + password + "'")
                .add("state=" + state)
                .toString();
    }

    public static void main(String[] args) {
        long a = 1591758942;
        System.out.println(Date.from(Instant.ofEpochSecond(a)));
    }
}
