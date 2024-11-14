package com.rituraj.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
//
//@Data
//@Document("buyer")
//public class Buyer extends User{
//
//    @Indexed(unique = true)
//    private String email;
//
//    private List<String> orderId;
//    private List<String> reviewId;
//    private String cartId;
//
//    public Buyer(String id, String name, String email, String password, List<String> orderId, List<String> reviewId, String cartId) {
//        super(id, name, password);
//        this.orderId = orderId != null ? orderId : new ArrayList<>();
//        this.reviewId = reviewId != null ? reviewId : new ArrayList<>();
//        this.cartId = cartId;
//        this.email = email;
//    }

//}


@Document(collection = "buyer")
public class Buyer extends User {

    @Indexed(unique = true)
    private String email;

    @Field("reviewId")
    private List<String> reviewId;

    @Field("allowedReviews")
    private List<String> allowedReviews;


    private Roles roles = Roles.ROLE_BUYER;

    public Buyer() {
    }

    public Buyer(String id, String name, String email, String password, List<String> reviewId) {
        super(id, name, password);
        this.email = email;
        this.reviewId = reviewId != null ? reviewId : new ArrayList<>();
    }





    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public List<String> getReviewId() {
        return reviewId;
    }

    public void setReviewId(List<String> reviewId) {
        this.reviewId = reviewId;
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

        Buyer buyer = (Buyer) o;

        if (!email.equals(buyer.email)) return false;
        return reviewId.equals(buyer.reviewId);
    }


    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + reviewId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Buyer{" +
                "email='" + email + '\'' +
                ", reviewId=" + reviewId +
                ", id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", password='" + getPassword() + '\'' +
                '}';
    }
}
