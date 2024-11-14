package com.rituraj.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "admin")
public class Admin {
    @Id
    private String id;
    private Roles roles;

    private String name;

    @Indexed(unique = true, background = true)
    private String email;

    private String password;
//    private AdminRoles role;

    public Admin(String id,  String name,  String email, String password, Roles roles) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    public Roles getRoles() {
        return roles;
    }
}
