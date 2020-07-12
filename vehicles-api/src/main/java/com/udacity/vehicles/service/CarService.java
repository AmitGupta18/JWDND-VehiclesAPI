package com.udacity.vehicles.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.udacity.vehicles.client.maps.Address;
import com.udacity.vehicles.client.prices.Price;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;

/**
 * Implements the car service create, read, update or delete information about
 * vehicles, as well as gather related location and price data when desired.
 */
@Service
public class CarService {

	private final CarRepository repository;
	private WebClient mapsClient;
	private WebClient pricingClient;

	public CarService(CarRepository repository, @Qualifier("maps") WebClient mapsClient,
			@Qualifier("pricing") WebClient pricingClient) {
		this.repository = repository;
		this.mapsClient = mapsClient;
		this.pricingClient = pricingClient;
	}

	/**
	 * Gathers a list of all vehicles
	 * 
	 * @return a list of all vehicles in the CarRepository
	 */
	public List<Car> list() {
		return repository.findAll();
	}

	/**
	 * Gets car information by ID (or throws exception if non-existent)
	 * 
	 * @param id the ID number of the car to gather information on
	 * @return the requested car's information, including location and price
	 */
	public Car findById(Long id) {

		Car car = repository.findById(id).orElseThrow(CarNotFoundException::new);

		Price price = pricingClient.get()
				.uri(uriBuilder -> uriBuilder.path("/services/price").queryParam("vehicleId", car.getId()).build())
				.retrieve().bodyToMono(Price.class).block();
		car.setPrice(price.getCurrency() + " " + price.getPrice());

		Address address = mapsClient.get()
				.uri(uriBuilder -> uriBuilder.path("/maps").queryParam("lat", car.getLocation().getLat())
						.queryParam("lon", car.getLocation().getLon()).build())
				.retrieve().bodyToMono(Address.class).block();
		car.getLocation().setAddress(address.getAddress());
		car.getLocation().setCity(address.getCity());
		car.getLocation().setState(address.getState());
		car.getLocation().setZip(address.getZip());
		return car;
	}

	/**
	 * Either creates or updates a vehicle, based on prior existence of car
	 * 
	 * @param car A car object, which can be either new or existing
	 * @return the new/updated car is stored in the repository
	 */
	public Car save(Car car) {
		if (car.getId() != null) {
			return repository.findById(car.getId()).map(carToBeUpdated -> {
				carToBeUpdated.setDetails(car.getDetails());
				carToBeUpdated.setLocation(car.getLocation());
				return repository.save(carToBeUpdated);
			}).orElseThrow(CarNotFoundException::new);
		}

		return repository.save(car);
	}

	/**
	 * Deletes a given car by ID
	 * 
	 * @param id the ID number of the car to delete
	 */
	public void delete(Long id) {
		Car car = repository.findById(id).orElseThrow(CarNotFoundException::new);
		repository.deleteById(car.getId());
	}
}
