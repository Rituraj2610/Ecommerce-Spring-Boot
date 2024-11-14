package com.rituraj.ecommerce.service;

import com.rituraj.ecommerce.dto.product.response.ProductDescriptionListResponseDTO;
import com.rituraj.ecommerce.exception.InvalidInputException;
import com.rituraj.ecommerce.service.product.ProductService;
import com.rituraj.ecommerce.service.user.BuyerImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BuyerImplementationTest {
    @Mock
    private ProductService productService;

    @InjectMocks
    private BuyerImplementation buyerImplementation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductsByNameAndCategory_ShouldThrowException_WhenBothParametersAreNull() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () ->
                buyerImplementation.getProductsByNameAndCategory("null", "null"));
    }

    @Test
    void getProductsByNameAndCategory_ShouldCallGetByCategory_WhenNameIsNull() {
        // Arrange
        String category = "testCategory";
        ProductDescriptionListResponseDTO expectedResponse = new ProductDescriptionListResponseDTO(new ArrayList<>());
        when(productService.getByCategory(category)).thenReturn(expectedResponse);

        // Act
        ProductDescriptionListResponseDTO result =
                buyerImplementation.getProductsByNameAndCategory("null", category);

        // Assert
        assertEquals(expectedResponse, result);
        verify(productService).getByCategory(category);
    }

    @Test
    void getProductsByNameAndCategory_ShouldCallGetByName_WhenCategoryIsNull() {
        // Arrange
        String name = "testProduct";
        ProductDescriptionListResponseDTO expectedResponse = new ProductDescriptionListResponseDTO(new ArrayList<>());
        when(productService.getByName(name)).thenReturn(expectedResponse);

        // Act
        ProductDescriptionListResponseDTO result =
                buyerImplementation.getProductsByNameAndCategory(name, "null");

        // Assert
        assertEquals(expectedResponse, result);
        verify(productService).getByName(name);
    }

    @Test
    void getProductsByNameAndCategory_ShouldCallGetByNameAndCategory_WhenBothParametersProvided() {
        // Arrange
        String name = "testProduct";
        String category = "testCategory";
        ProductDescriptionListResponseDTO expectedResponse = new ProductDescriptionListResponseDTO(new ArrayList<>());
        when(productService.getByNameAndCategory(name, category)).thenReturn(expectedResponse);

        // Act
        ProductDescriptionListResponseDTO result =
                buyerImplementation.getProductsByNameAndCategory(name, category);

        // Assert
        assertEquals(expectedResponse, result);
        verify(productService).getByNameAndCategory(name, category);
    }
}