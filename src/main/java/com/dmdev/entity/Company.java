package com.dmdev.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SortNatural;

import java.util.*;

@Entity
@Data
@EqualsAndHashCode(of = "name")
@ToString(exclude = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,unique = false,  name = "name")
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OrderBy("username ASC, personalInfo.lastname ASC")
    @MapKey(name = "username")
//    @SortNatural
    private Map<String, User> users = new HashMap<>();


//    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "company_locale")
    private List<LocaleInfo> locales;

    public void addUser(User user){
        users.put(user.getUsername(), user);
        user.setCompany(this);
    }
}
