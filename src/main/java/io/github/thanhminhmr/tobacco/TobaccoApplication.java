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
				userRepository.save(User.builder()
						.username("username")
						.password(passwordEncoder.encode("password"))
						.displayName("User One")
						.authorities(Set.of(Authority.SUPER_ADMIN, Authority.NORMAL_USER))
						.deleted(false)
						.build());
			}

			productRepository.save(Product.builder()
					.displayName("Product One")
					.displayDescription("The product that has a giant number one.")
					.displayUnit("ONE")
					.currentPrice(10000L)
					.deleted(false)
					.build());
			productRepository.save(Product.builder()
					.displayName("Product Two")
					.displayDescription("The product that has a giant number two.")
					.displayUnit("TWO")
					.currentPrice(20000L)
					.deleted(false)
					.build());
			productRepository.save(Product.builder()
					.displayName("Product Three")
					.displayDescription("The product that has a giant number three.")
					.displayUnit("three")
					.currentPrice(30000L)
					.deleted(false)
					.build());

			final Invoice invoice = Invoice.builder()
					.user(userRepository.findById(1L).orElseThrow())
					.status(InvoiceStatus.CREATED)
					.build();

			invoice.setItems(List.of(InvoiceItem.builder()
					.invoice(invoice)
					.product(productRepository.findById(1L).orElseThrow())
					.quantity(100L)
					.unitPrice(10000L)
					.build()));

			invoice.setComments(List.of(InvoiceComment.builder()
					.invoice(invoice)
					.user(userRepository.findById(1L).orElseThrow())
					.displayComment("Comment one")
					.statusBefore(InvoiceStatus.CREATED)
					.statusAfter(InvoiceStatus.CREATED)
					.build()));

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
