# E-Commerce Website in Spring Boot

## **Introduction to the Project**  
### A feature-rich e-commerce application with Spring Boot  
This project is a robust e-commerce website built using **Spring Boot**. It follows the **MVC architecture** and incorporates various Spring-related technologies and third-party integrations to manage users, products, orders, and more. It leverages **MongoDB** for storage, **JWT** for authentication, and **Cloudinary** for image management.  

---

## **Introduction to the Codebase**  
### Key Technologies and Dependencies  
- **Spring Boot Starters**: Web, Data REST, Mongo, Security, and Session.  
- **Third-party Integrations**: Cloudinary, JWT, and Jakarta.  
- **Additional Tools**: Lombok, Logging (Logback), CORS configuration, and AOP for modular design.  

---

## **Visual Helpers**  

- Application architecture.  
  ![E-Commerce Architecture](https://github.com/Rituraj2610/Ecommerce-Spring-Boot/blob/main/images/architectural%20Diagram.svg)

- ER diagrams for MongoDB collections. 
  ![Database ER Diagram](https://github.com/Rituraj2610/Ecommerce-Spring-Boot/blob/main/images/ER%20Diagram.png)
---

## **User Instructions: How to Set Up and Run the Application**  
### Prerequisites  
1. **Install Java JDK 17+** and an IDE like IntelliJ IDEA or Eclipse.  
2. **MongoDB Setup**:  
   - Create a **MongoDB database**.  
   - Replace the `spring.data.mongodb.uri` in `application.yml` with your database's connection string.  
   - Collections and schemas will be auto-generated at runtime.  
3. **Cloudinary Account**:  
   - Create an account on **Cloudinary**.  
   - Replace the values of `cloud-name`, `api-key`, and `api-secret` in `application.yml` with your Cloudinary credentials.  
4. **Mail Configuration**:  
   - Use your email and app password for the SMTP configuration in `spring.mail`.  

### Installation Steps  
1. **Clone the repository**:  
   ```bash  
   git clone <repository-url>  
   cd ecommerce  
   ```
2. **Build the project**:
   ```bash  
   mvn clean install  
   ```
3. **Run the application**:
   ```bash  
    mvn spring-boot:run  
    ```
---
## **Developer Instructions: How to Customize and Extend**
### Configuration Guidelines
1. **CORS Configuration**:
- Update the frontend URL in the CORS settings within SecurityConfig to allow cross-origin requests.
2. **Logging**:
- Change the logback.xml file path (Add your Folder Path) to your desired logging directory.
- Update the EcommerceApplication main file to reflect the updated path.
  ```bash
  File logsDir = new File("<your-custom-log-directory>");  
  ```
- Enable the Logback annotation for efficient logging.

### Mongo Repositories
- Update the @EnableMongoRepositories base package if you restructure the project.
  ```bash
  @EnableMongoRepositories(basePackages = "<your.package.name>")  
  ```
### Run Tests
- Execute the provided unit and integration tests using Maven:
  ```bash
  mvn test  

  ```
---
## **Features Overview**  
### Core Features  

#### User Management:  
- **Role-based Authentication and Authorization**:  
  - Admin with roles: `SUPER_ADMIN`, `USER_ADMIN`, and `PRODUCT_ADMIN`.  
  - Buyers and sellers with distinct functionalities.  
- **JWT-Based Authentication**:  
  - Secure user session management using JSON Web Tokens (JWT).  
- **Login and Registration**:  
  - Separate flows for buyers, sellers, and admins.  
  - Input validation and custom exception handling for security.  

#### Product Management:  
- **CRUD Operations**:  
  - Create, read, update, and delete products with constraints (e.g., Seller ID).  
- **Image Upload & Deletion**:  
  - Upload multiple product images to Cloudinary.  
  - Dynamically delete images from Cloudinary when removed from the database.  
- **Search Functionality**:  
  - Fetch products by name, category, or seller ID.  
- **Category Management**:  
  - Categorization for product organization and filtering.  

#### Order Management:  
- **Placing and Tracking Orders**:  
  - Buyers can place orders and track their statuses.  
  - Order history retrieval for users.  

#### Custom Exception Handling:  
- **Centralized Exception Management**:  
  - Handled using `@ControllerAdvice`.  
- **Custom Exceptions**:  
  - Specific exceptions for scenarios like:  
    - User not found.  
    - Invalid credentials or roles.  
    - Product conflicts (e.g., duplicate names, invalid updates).  
    - Database access errors.  

#### Database Integration:  
- **MongoDB Integration**:  
  - Document storage and retrieval.  
  - Query optimization with indexed fields like `sellerId` and `productName`.  

#### Frontend Integration:  
- **API Endpoints**:  
  - Designed to seamlessly integrate with a frontend application.  

---

## **Known Issues and Limitations**  

### Potential Errors and Fixes  
1. **MongoDB Connection Errors**:  
   - Verify the URI and credentials in `application.yml`.  
   - Ensure MongoDB is running and accessible.  
2. **Cloudinary Misconfiguration**:  
   - Ensure accurate account details in the `cloudinary` section of `application.yml`.  
   - Check for sufficient storage in your Cloudinary account.  
3. **SMTP Failures**:  
   - Use valid email credentials in `spring.mail`.  
   - Enable app access in your email settings if required.  
4. **CORS Errors**:  
   - Ensure the frontend URL is properly configured in the CORS settings.  

### Missing Enhancements  
- **Payment Integration**:  
  - Currently lacks a payment gateway for secure checkout functionality.  
- **Analytics and Reporting**:  
  - Missing tracking for user behavior, popular products, or sales trends.  
- **Multithreading**:
  - For parallelized image uploads and deletions via Cloudinary.
- **Soft Delete Enhancements**:
  - Ensure deleted items are invisible while maintaining a history.
- **Live Tracking**:
  - Currently missing delivery and order tracking functionality.
- **Authorization List for Admin**:
  - Replace hardcoded roles with a dynamic role-based structure.
- **Improved Database Structure**:
  - Lookup queries can reduce redundancy and optimize database design.

---

## Frontend and API List
- The frontend for this project is available on GitHub. Api Lists are available in Readme.md of frontend. It provides a user-friendly interface for buyers, sellers, and admins.

  [Frontend Repository](https://github.com/Rituraj2610/Frontend-Ecommerce)

---
This **README** serves as a guide for understanding, setting up, and contributing to this project. For additional queries or issues, please contact the project maintainer.
