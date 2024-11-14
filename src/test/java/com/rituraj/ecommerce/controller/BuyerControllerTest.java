package com.rituraj.ecommerce.controller;

import com.rituraj.ecommerce.dto.orders.response.OrderAddResponseDTO;
import com.rituraj.ecommerce.dto.product.response.ProductDescriptionListResponseDTO;
import com.rituraj.ecommerce.service.user.BuyerImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BuyerControllerTest {
    @Mock
    private BuyerImplementation buyerImplementation;

    @InjectMocks
    private BuyerController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*
     * Method: getProductByNameAndCategory_ShouldReturnProducts_WhenBothParametersProvided
     * Role: Test for getting product by category and name
     */
    @Test
    void getProductByNameAndCategory_ShouldReturnProducts_WhenBothParametersProvided() {
        // Arrange
        String name = "testProduct";
        String category = "testCategory";
        ProductDescriptionListResponseDTO expectedResponse = new ProductDescriptionListResponseDTO(new ArrayList<>());
        when(buyerImplementation.getProductsByNameAndCategory(name, category))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ProductDescriptionListResponseDTO> response =
                productController.getProductByNameAndCategory(name, category);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(buyerImplementation).getProductsByNameAndCategory(name, category);
    }

    @Test
    void addOrder_ShouldReturnHttpOk_WhenAllOrdersPlaced(){
        OrderAddResponseDTO expectedOrderResponse = new OrderAddResponseDTO(
                Arrays.asList("1","2"),
                new ArrayList<>(),
                new ArrayList<>()
        );
        when(buyerImplementation.placeOrder()).thenReturn(expectedOrderResponse);

        ResponseEntity<OrderAddResponseDTO> response = productController.addOrder();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOrderResponse, response.getBody());
    }

    @Test
    void addOrder_ShouldReturnHttpPARTIAL_CONTENT_WhenSomeOrdersPlaced(){
        OrderAddResponseDTO expectedOrderResponse = new OrderAddResponseDTO(
                Arrays.asList("1","2"),
                Arrays.asList("3"),
                new ArrayList<>()
        );
        when(buyerImplementation.placeOrder()).thenReturn(expectedOrderResponse);

        ResponseEntity<OrderAddResponseDTO> response = productController.addOrder();

        assertNotNull(response);
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertEquals(expectedOrderResponse, response.getBody());
    }
}