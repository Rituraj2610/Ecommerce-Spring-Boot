package com.rituraj.ecommerce.service;

import com.rituraj.ecommerce.dto.product.request.ProductRequestDTO;
import com.rituraj.ecommerce.service.product.ProductService;
import com.rituraj.ecommerce.service.user.SellerImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SellerImplementationTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private SellerImplementation sellerImplementation;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    /*
     * Method: getAllProducts_ShouldReturnProductAddeddSuccessfully_WhenSuccess
     * Role: Tests sellerImplementation.addProduct
     */
    @Test
    void getAllProducts_ShouldReturnProductAddeddSuccessfully_WhenSuccess(){
        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        MultipartFile[] mockFiles = new MultipartFile[]{
                new MockMultipartFile("test", new byte[0])
        };
        when(productService.addProduct(any(ProductRequestDTO.class),any(MultipartFile[].class))).thenReturn("Product added successfully");

        String msg = sellerImplementation.addProduct(productRequestDTO, mockFiles);

        assertEquals("Product added successfully", msg);
    }
}
