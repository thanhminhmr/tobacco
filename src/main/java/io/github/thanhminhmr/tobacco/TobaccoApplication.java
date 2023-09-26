/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco;

import io.github.thanhminhmr.tobacco.presistence.model.*;
import io.github.thanhminhmr.tobacco.presistence.repository.InvoiceRepository;
import io.github.thanhminhmr.tobacco.presistence.repository.ProductRepository;
import io.github.thanhminhmr.tobacco.presistence.repository.UserRepository;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
public class TobaccoApplication {
	private static final @Nonnull Logger LOGGER = LoggerFactory.getLogger(TobaccoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TobaccoApplication.class, args);
	}

	@Bean
	public @Nonnull CommandLineRunner demo(
			@Nonnull UserRepository userRepository,
			@Nonnull ProductRepository productRepository,
			@Nonnull InvoiceRepository invoiceRepository,
			@Nonnull PasswordEncoder passwordEncoder
	) {
		return (args) -> {
			// save a few customers
			if (!userRepository.existsByUsername("username")) {
				userRepository.save(new User()
						.setUsername("username")
						.setPassword(passwordEncoder.encode("password"))
						.setDisplayName("User One")
						.setAuthorities(Set.of(Authority.SUPER_ADMIN, Authority.NORMAL_USER))
						.setDeleted(false));
			}

			productRepository.save(new Product()
					.setDisplayName("Product One")
					.setDisplayDescription("The product that has a giant number one.")
					.setDisplayUnit("ONE")
					.setCurrentPrice(10000L)
					.setDeleted(false));
			productRepository.save(new Product()
					.setDisplayName("Product Two")
					.setDisplayDescription("The product that has a giant number two.")
					.setDisplayUnit("TWO")
					.setCurrentPrice(20000L)
					.setDeleted(false));
			productRepository.save(new Product()
					.setDisplayName("Product Three")
					.setDisplayDescription("The product that has a giant number three.")
					.setDisplayUnit("three")
					.setCurrentPrice(30000L)
					.setDeleted(false));

			final Invoice invoice = new Invoice()
					.setUser(userRepository.findById(1L).orElseThrow())
					.setDisplayDescription("New invoice")
					.setStatus(InvoiceStatus.CREATED)
					.setDeleted(false);

			invoice.setItems(List.of(new InvoiceItem()
					.setInvoice(invoice)
					.setProduct(productRepository.findById(1L).orElseThrow())
					.setQuantity(100L)
					.setUnitPrice(10000L)
					.setDeleted(false)));

			invoice.setComments(List.of(new InvoiceComment()
					.setInvoice(invoice)
					.setUser(userRepository.findById(1L).orElseThrow())
					.setDisplayComment("Comment one")
					.setStatusBefore(InvoiceStatus.CREATED)
					.setStatusAfter(InvoiceStatus.CREATED)
					.setDeleted(false)));

			invoiceRepository.save(invoice);

			// fetch all customers
			LOGGER.info("Customers found with findAll():");
			LOGGER.info("-------------------------------");
			for (Product product : productRepository.findAll()) {
				LOGGER.info(product.toString());
			}
			LOGGER.info("");

			// fetch an individual customer by ID
			Optional<Product> product = productRepository.findById(1L);
			LOGGER.info("Product found with findById(1L):");
			LOGGER.info("--------------------------------");
			LOGGER.info(product.toString());
			LOGGER.info("");
		};
	}
}
