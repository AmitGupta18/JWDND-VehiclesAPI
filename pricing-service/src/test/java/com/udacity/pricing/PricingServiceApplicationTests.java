package com.udacity.pricing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.udacity.pricing.domain.price.Price;



@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PricingServiceApplicationTests {

	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void shouldReturnPriceForVehicleIdLessThanTwenty() {
		ResponseEntity<Price> response = this.restTemplate.getForEntity("http://localhost:" + port + "/services/price?vehicleId=1",
				Price.class);
		assertNotNull(response.getBody());
	}
	
	@Test
	public void shouldGetNotFoundForVehicleIdGreaterThanTwenty() {
		ResponseEntity<Price> response = this.restTemplate.getForEntity("http://localhost:" + port + "/services/price?vehicleId=21",
				Price.class);
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}

}
