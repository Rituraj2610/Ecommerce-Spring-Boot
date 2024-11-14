package com.rituraj.ecommerce;

import com.rituraj.ecommerce.model.Product;
import com.rituraj.ecommerce.repository.ProductRepo;
import com.rituraj.ecommerce.service.user.BuyerImplementation;
import com.rituraj.ecommerce.util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class EcommerceApplicationTests {

	@Autowired
	private ProductRepo productRepo;
	@Autowired
	private BuyerImplementation buyerImplementation;

	@Test
	void testGetHomePageProducts_Success() throws Exception {
		// Add sample products to the repository for testing
		Product product1 = new Product("123",
				"123",
				"Product1",
				10.00,
				"Description1",
				"Category1",
				12,
				new ArrayList<>(),
				new ArrayList<>());

		Product product2 = new Product("1233",
				"123",
				"Product2",
				10.00,
				"Description2",
				"Category2",
				12,
				new ArrayList<>(),
				new ArrayList<>());
		productRepo.saveAll(Arrays.asList(product1, product2));

		List<Product> found = productRepo.findAll();
		assertThat(found).isNotNull();
		assertThat(found.size()).isEqualTo(2);
		// Perform GET request to the endpoint
	}

//	@Test
//	void testGetHomePageProducts_NoProductsFound() throws Exception {
//		// Ensure the repository is empty
//		productRepo.deleteAll();



}
