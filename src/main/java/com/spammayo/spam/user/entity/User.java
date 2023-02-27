package com.spammayo.spam.user.entity;

import com.spammayo.spam.audit.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "USERS")
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String profileUrl;

    @Column(nullable = false)
    private String profileKey;

    @Column
    private String field;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> roles = new ArrayList<>();

//    @OneToMany(mappedBy = "user")
//    private List<Stack> stack = new ArrayList<>();


}
