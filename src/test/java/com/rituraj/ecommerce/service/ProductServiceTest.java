package com.rituraj.ecommerce.service;

import com.rituraj.ecommerce.dto.product.request.ProductRequestDTO;
import com.rituraj.ecommerce.dto.product.response.ProductDescriptionListResponseDTO;
import com.rituraj.ecommerce.exception.EntityPushException;
import com.rituraj.ecommerce.exception.ResourceNotFoundException;
import com.rituraj.ecommerce.middleware.JwtAspect;
import com.rituraj.ecommerce.model.Product;
import com.rituraj.ecommerce.repository.ProductRepo;
import com.rituraj.ecommerce.service.cloudinary.CloudinaryService;
import com.rituraj.ecommerce.service.product.ProductService;
import com.rituraj.ecommerce.service.user.SellerImplementation;
import com.rituraj.ecommerce.util.IdGenerator;
import com.rituraj.ecommerce.util.JwtUtil;
import io.jsonwebtoken.Jwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private SellerImplementation sellerImplementation;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static final String TEST_IMAGE_URL = "test_image_url";
    private static final String TEST_PRODUCT_ID = "test_product_id";
    private static final String TEST_SELLER_ID = "test_seller_id";

    @Test
    void getByNameAndCategory_ShouldReturnProducts_WhenProductsFound() {
        // Arrange
        String name = "testProduct";
        String category = "testCategory";
        List<Product> expectedProducts = Arrays.asList(new Product());

        // Capture the Query object
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        when(mongoTemplate.find(queryCaptor.capture(), eq(Product.class)))
                .thenReturn(expectedProducts);

        // Act
        ProductDescriptionListResponseDTO result =
                productService.getByNameAndCategory(name, category);

        // Assert
        assertNotNull(result);
        verify(mongoTemplate).find(any(Query.class), eq(Product.class));

        // Verify the query structure
        Query capturedQuery = queryCaptor.getValue();
        assertNotNull(capturedQuery);
    }

    @Test
    void getByNameAndCategory_ShouldThrowException_WhenNoProductsFound() {
        // Arrange
        String name = "nonExistentProduct";
        String category = "nonExistentCategory";
        when(mongoTemplate.find(any(Query.class), eq(Product.class)))
                .thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                productService.getByNameAndCategory(name, category));
    }

    @Test
    void getByCategory_ShouldReturnDTO_WhenProductsExist() {
        // Arrange
        String category = "testCategory";
        List<Product> productList = Arrays.asList(new Product());
        when(productRepo.findByCategory(category)).thenReturn(productList);

        // Act
        ProductDescriptionListResponseDTO result = productService.getByCategory(category);

        // Assert
        assertNotNull(result);
        assertEquals(productList.size(), result.getProductList().size()); // Checking DTO contains expected products
        verify(productRepo).findByCategory(category);
    }

    @Test
    void getByName_ShouldReturnProductAddedSuccessfully_WhenProductAdded() throws IOException {
        try(MockedStatic<JwtAspect> jwtAspectMockedStatic = mockStatic(JwtAspect.class)) {
            jwtAspectMockedStatic.when(JwtAspect::getCurrentUserId).thenReturn("sample");

            ProductRequestDTO productRequestDTO = new ProductRequestDTO();
            productRequestDTO.setName("Test Product");
            productRequestDTO.setPrice(100.0);
            productRequestDTO.setDescription("Test Description");
            productRequestDTO.setCategory("Test Category");
            productRequestDTO.setStock(10);

            MultipartFile[] images = new MultipartFile[]{
                    new MockMultipartFile("test", new byte[0])
            };


            String expectedId = "userId";
            when(idGenerator.generateId()).thenReturn(expectedId);

            when(cloudinaryService.uploadImage(any(MultipartFile.class))).thenReturn(TEST_IMAGE_URL);

            Product product = new Product(
                    TEST_PRODUCT_ID,
                    TEST_SELLER_ID,
                    productRequestDTO.getName(),
                    productRequestDTO.getPrice(),
                    productRequestDTO.getDescription(),
                    productRequestDTO.getCategory(),
                    productRequestDTO.getStock(),
                    new ArrayList<>(),
                    Collections.singletonList(TEST_IMAGE_URL)
            );

            doNothing().when(productService).getByNameAndSellerId(eq(product.getName()), eq(product.getSellerId()));
            when(productRepo.save(any(Product.class))).thenReturn(product);
            doNothing().when(sellerImplementation).updateSellerProductList(TEST_SELLER_ID, TEST_PRODUCT_ID);


            String msg = productService.addProduct(productRequestDTO, images);

            assertEquals("Product added successfully", msg);

            verify(idGenerator.generateId());

            verify(cloudinaryService.uploadImage(any(MultipartFile.class)));

            verify(productRepo.save(any(Product.class)));
        }
    }

    @Test
    void addProduct_ShouldThrowException_WhenUserIdIsEmpty(){
        try(MockedStatic<JwtAspect> jwtAspectMockedStatic = mockStatic(JwtAspect.class)){
            jwtAspectMockedStatic.when(JwtAspect::getCurrentUserId).thenReturn("");


        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Test Product");
        productRequestDTO.setPrice(100.0);
        productRequestDTO.setDescription("Test Description");
        productRequestDTO.setCategory("Test Category");
        productRequestDTO.setStock(10);
        MultipartFile[] images = new MultipartFile[]{
                new MockMultipartFile("test", new byte[0])
        };
        assertThrows(ResourceNotFoundException.class, ()-> productService.addProduct(productRequestDTO, images));
    }}

    @Test
    void addProduct_ShouldThrowException_WhenCloudinaryUploadFails() throws IOException {
        try(MockedStatic<JwtAspect> jwtAspectMockedStatic = mockStatic(JwtAspect.class)){
            jwtAspectMockedStatic.when(JwtAspect::getCurrentUserId).thenReturn("id");
            MultipartFile[] images = new MultipartFile[]{
                    new MockMultipartFile("test", new byte[0])
            };

            ProductRequestDTO productRequestDTO = new ProductRequestDTO();
            productRequestDTO.setName("Test Product");
            productRequestDTO.setPrice(100.0);
            productRequestDTO.setDescription("Test Description");
            productRequestDTO.setCategory("Test Category");
            productRequestDTO.setStock(10);


            when(idGenerator.generateId()).thenReturn("userId");



            when(cloudinaryService.uploadImage(any(MultipartFile.class)))
                    .thenThrow(new IOException("Upload failed"));




            EntityPushException exception = assertThrows(EntityPushException.class, () ->
                    productService.addProduct(productRequestDTO, images));
            assertEquals("Failed to uplaod image to cloudinary", exception.getMessage());
        }
    }

    @Test
    void addProduct_ShouldThrowException_WhenProductSaveFails() throws IOException {

        try(MockedStatic<JwtAspect> jwtAspectMockedStatic = mockStatic(JwtAspect.class)){
            jwtAspectMockedStatic.when(JwtAspect::getCurrentUserId).thenReturn("id");
            MultipartFile[] images = new MultipartFile[]{
                    new MockMultipartFile("test", new byte[0])
            };

            ProductRequestDTO productRequestDTO = new ProductRequestDTO();
            productRequestDTO.setName("Test Product");
            productRequestDTO.setPrice(100.0);
            productRequestDTO.setDescription("Test Description");
            productRequestDTO.setCategory("Test Category");
            productRequestDTO.setStock(10);
        // Arrange
        when(idGenerator.generateId()).thenReturn(TEST_PRODUCT_ID);
        when(cloudinaryService.uploadImage(any(MultipartFile.class))).thenReturn(TEST_IMAGE_URL);
        when(productRepo.save(any(Product.class))).thenReturn(null);

        // Act & Assert
        EntityPushException exception = assertThrows(EntityPushException.class, () ->
                productService.addProduct(productRequestDTO, images));
        assertEquals("Failed to add product to database", exception.getMessage());
    }}

}