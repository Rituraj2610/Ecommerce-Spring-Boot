package com.rituraj.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

//@Data
//@Document("seller")
//public class Seller extends User{
//
//    @Indexed(unique = true)
//    private String email;
//
//    private List<String> productId;
//
//    public Seller(String id, String name, String email, String password, List<String> productId) {
//        super(id, name, password);
//        this.productId = productId;
//        this.email = email;
//    }
//}
@Document(collection = "seller")
public class Seller extends User {

    @Indexed(unique = true)
    private String email;
    private Roles roles = Roles.ROLE_SELLER;

    private List<String> productId;

    public Seller() {
    }

    public Seller(String id, String name, String email, String password, List<String> productId) {
        super(id, name, password);
        this.email = email;
        this.productId = productId != null ? productId : new ArrayList<>();
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getProductId() {
        return productId;
    }

    public void setProductId(List<String> productId) {
        this.productId = productId;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Seller seller = (Seller) o;

        if (!email.equals(seller.email)) return false;
        return productId.equals(seller.productId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + productId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Seller{" +
                "email='" + email + '\'' +
                ", productId=" + productId +
                ", id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", password='" + getPassword() + '\'' +
                '}';
    }
}