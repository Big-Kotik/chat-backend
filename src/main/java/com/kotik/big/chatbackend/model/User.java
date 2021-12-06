package com.kotik.big.chatbackend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(
        name = "\"user\"",
        uniqueConstraints = @UniqueConstraint(columnNames = "login"),
        indexes = @Index(columnList = "creationTime")
)
@Setter
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Pattern(regexp = "[a-zA-Z0-9]+")
    @Size(min = 4, max = 16, message = "login size between 4 and 16")
    @Column(nullable = false)
    private String login;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    private String passwordSha;

    @ColumnDefault("null")
    private String socketId;

    @CreationTimestamp
    @Column(nullable = false)
    private Date creationTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
