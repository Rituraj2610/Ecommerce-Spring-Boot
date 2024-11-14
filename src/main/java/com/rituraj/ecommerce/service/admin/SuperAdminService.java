package com.rituraj.ecommerce.service.admin;
import com.rituraj.ecommerce.dto.admin.request.CreateAdminRequestDTO;
import com.rituraj.ecommerce.dto.admin.RegisterDTO;
import com.rituraj.ecommerce.exception.*;
import com.rituraj.ecommerce.model.*;
import com.rituraj.ecommerce.repository.AdminRepo;
import com.rituraj.ecommerce.service.user.BuyerImplementation;
import com.rituraj.ecommerce.service.user.SellerImplementation;
import com.rituraj.ecommerce.util.EmailValidator;
import com.rituraj.ecommerce.util.IdGenerator;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class SuperAdminService implements AdminService {

    private  AdminRepo adminRepo;
    private  IdGenerator idGenerator;
    private  EmailValidator emailValidator;
    private  UserAdminService userAdminService;
    private  ProductAdminService productAdminService;
    private  BuyerImplementation buyerImplementation;
    private  SellerImplementation sellerImplementation;
    private  PasswordEncoder passwordEncoder;

    public SuperAdminService(AdminRepo adminRepo, IdGenerator idGenerator, EmailValidator emailValidator, UserAdminService userAdminService, ProductAdminService productAdminService,  BuyerImplementation buyerImplementation,  SellerImplementation sellerImplementation, PasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepo;
        this.idGenerator = idGenerator;
        this.emailValidator = emailValidator;
        this.userAdminService = userAdminService;
        this.productAdminService = productAdminService;
        this.buyerImplementation = buyerImplementation;
        this.sellerImplementation = sellerImplementation;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * Method: initializeAdmin
     * Role: Creates new Super Admin if no admin is present in db using adminExistsOrNot
     */
    public String initializeAdmin(RegisterDTO registerDTO) {
        String email = registerDTO.getEmail();
        if(!emailValidator.isValidEmail(email)){
            throw new InvalidInputException("Invalid email input.");
        }
        String password = passwordEncoder.encode(registerDTO.getPassword());
        Admin superAdmin = adminRepo.save(new Admin(idGenerator.generateId(), "admin", email, password, Roles.SUPER_ADMIN));

        if(superAdmin!=null && superAdmin.getId()!=null){
            return ("Admin registered successfully");
        }
        throw new EntityCreationException("Failed to create admin with given credentials.");
    }

    /*
     * Method: adminExistsOrNot
     * Role: checks if admin exists in db
     */
    public synchronized boolean adminExistsOrNot(){
        if (adminRepo.count() < 1) {
            return false;
        }
        return true;
    }

    @Override
    public Admin login(String email, String password, Roles role) throws NoSuchAlgorithmException {
        Admin admin = adminRepo.findByEmailAndRoles(email, role);


        if(admin.getId() != null && admin!=null){
//aop
            if (passwordEncoder.matches(password, admin.getPassword())) {
                // Password matches
                System.out.println("Admin found");
                return admin;
            } else {
                // Password doesn't match
                throw new ResourceNotFoundException("Admin with the specified credentials not found.");
            }
        }
        throw new ResourceNotFoundException("Admin with the specified credentials not found.");
    }

    /*
     * Method: CreateAdmins
     * Role: Creates other admins using super admin
     */
    public String createAdmins(CreateAdminRequestDTO createAdminRequestDTO){
        String email = createAdminRequestDTO.getEmail();

        //email validation
        if(!emailValidator.isValidEmail(email)){
            throw new InvalidInputException("Invalid email input.");
        }

        // Check if the email exists in any of the repositories
        boolean emailExists = adminRepo.findByEmail(email) != null ||
                buyerImplementation.findByEmail(email) != null ||
                sellerImplementation.findByEmail(email) != null;
        if (emailExists) {
            throw new EntityAlreadyExistsException("An account with this email already exists");
        }

        String id = idGenerator.generateId();
        String password = passwordEncoder.encode(createAdminRequestDTO.getPassword());

        Admin admin = new Admin();
        admin.setId(id);
        admin.setPassword(password);
        admin.setRoles(createAdminRequestDTO.getRoles());
        admin.setEmail(email);
        admin.setName(createAdminRequestDTO.getName());

        // Save the new admin
        Optional<Admin> optionalAdmin = Optional.of(adminRepo.save(admin));
        if (optionalAdmin.isPresent()) {
            return new String("Successfully registered admin");
        }

        throw new EntityCreationException("Error creating admin");
    }

    /*
     * Method: deleteUserAdmin
     * Role: Deletes other admins using super admin
     */
    public String deleteUserAdmin(String id){
        Optional<Admin> admin = adminRepo.findById(id); // or findByAdminId(id) if that’s your method
        if (!admin.isPresent()) {
            throw new ResourceNotFoundException("Failed to fetch admin with specified resource.");
        }

        // Delete the admin
        long count = adminRepo.deleteByAdminId(id);
        if (count > 0) {
            return "Admin deleted successfully";
        }

        throw new EntityDeletionException("Failed to delete admin from database");
    }


    public List<Seller> getAllSellers() {
        return productAdminService.getAllSellers();
    }

    public List<Product> getAllProducts() {
        return productAdminService.getAllProducts();
    }

    public List<Product> getAllSellerProducts(String id) {
        return productAdminService.getAllSellerProducts(id);
    }

    public Seller getSellerFromProductId(String productId){
        return sellerImplementation.getSellerFromProductId(productId);
    }

    public String deleteSeller(String id) {
        return productAdminService.deleteSeller(id);
    }


    public String deleteProduct(String productId) {
        return productAdminService.deleteProduct(productId);
    }

    public List<Buyer> getAllBuyers() {
        return userAdminService.getAllBuyers();
    }

    public List<Review> getAllUserReviews(String buyerId) {
        return userAdminService.getAllUserReviews(buyerId);
    }

    public Cart viewUserCart(String cartId){
        return userAdminService.viewUserCart(cartId);
    }

    public String deleteUser(String userId) {
        return userAdminService.deleteUser(userId);
    }


    public ResponseEntity<List<Admin>> getAllUserAdmin() {
        Roles roles = Roles.USER_ADMIN;
        List<Admin> list = adminRepo.findByRoles(roles);
        return ResponseEntity.ok(list);
    }

    public ResponseEntity<List<Admin>> getAllProductAdmin() {
        Roles roles = Roles.PRODUCT_ADMIN;
        List<Admin> list = adminRepo.findByRoles(roles);
        return ResponseEntity.ok(list);
    }

    public List<Order> getUserOrder(String buyerId) {
        return userAdminService.getUserOrder(buyerId);
    }
}
