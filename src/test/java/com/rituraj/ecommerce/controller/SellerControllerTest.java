package com.rituraj.ecommerce.controller;

import com.rituraj.ecommerce.dto.product.request.ProductRequestDTO;
import com.rituraj.ecommerce.service.user.SellerImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SellerControllerTest {
    @Mock
    private SellerImplementation sellerImplementation;

    @InjectMocks
    private SellerController sellerController;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    /*
     * Method: addProduct_ShouldReturnSuccess_WhenValidInput
     * Role: Tests sellerController.addProduct during SUCCESS
     */
    @Test
    void addProduct_ShouldReturnSuccess_WhenValidInput(){
        MultipartFile[] mockFiles = new MultipartFile[]{
                new MockMultipartFile("test", new byte[0])
        };
        ProductRequestDTO dto = new ProductRequestDTO();
        when(sellerImplementation.addProduct(any(ProductRequestDTO.class), any(MultipartFile[].class))).thenReturn("Product added successfully");

        // Act
        ResponseEntity<String> response = sellerController.addProduct(
                dto,
                mockFiles,
                new MockHttpServletRequest()
        );

        assertNotNull(response);
        assertEquals("Product added successfully", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(sellerImplementation).addProduct(any(ProductRequestDTO.class), any(MultipartFile[].class));
    }
}
