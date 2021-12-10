package com.kotik.big.chatbackend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(
        name = "\"user\"",
        uniqueConstraints = @UniqueConstraint(columnNames = "username"),
        indexes = @Index(columnList = "creationTime")
)
@Setter
@Getter
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Pattern(regexp = "[a-zA-Z0-9]+")
    @Size(min = 4, max = 16, message = "login size between 4 and 16")
    @Column(nullable = false)
    private String username;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    private String password;

    @ColumnDefault("null")
    private String socketId;

    @CreationTimestamp
    @Column(nullable = false)
    private Date creationTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRole().getPermissions();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
