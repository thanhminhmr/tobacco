/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco;

import io.github.thanhminhmr.tobacco.presistence.model.*;
import io.github.thanhminhmr.tobacco.presistence.repository.GroupRepository;
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
			@Nonnull GroupRepository groupRepository,
			@Nonnull ProductRepository productRepository,
			@Nonnull InvoiceRepository invoiceRepository,
			@Nonnull PasswordEncoder passwordEncoder
	) {
		return new CommandLineRunner() {
			private @Nonnull User createUser(@Nonnull String username, @Nonnull Authority... authorities) {
				final User user = userRepository.findByUsername(username);
				return user != null ? user : userRepository.save(new User()
						.setUsername(username)
						.setPassword(passwordEncoder.encode("password"))
						.setDisplayName(username)
						.setAuthorities(Set.of(authorities))
						.setDeleted(false));
			}

			@Override public void run(String... args) throws Exception {
				// save a few customers
				final User saleManager1 = createUser("saleManager1", Authority.USER_SALE_MANAGER);
				final User saleManager2 = createUser("saleManager2", Authority.USER_SALE_MANAGER);
				final User salesman1 = createUser("salesman1", Authority.USER_SALESMAN);
				final User salesman2 = createUser("salesman2", Authority.USER_SALESMAN);
				final User salesman3 = createUser("salesman3", Authority.USER_SALESMAN);

				groupRepository.save(new Group()
						.setDisplayName("Group One")
						.setUsers(Set.of(saleManager1, salesman1, salesman2))
						.setDeleted(false));
				groupRepository.save(new Group()
						.setDisplayName("Group One")
						.setUsers(Set.of(saleManager2, salesman3))
						.setDeleted(false));

				final Product product1 = productRepository.save(new Product()
						.setDisplayName("Product One")
						.setDisplayDescription("The product that has a giant number one.")
						.setDisplayUnit("ONE")
						.setCurrentPrice(10000L)
						.setDeleted(false));
				final Product product2 = productRepository.save(new Product()
						.setDisplayName("Product Two")
						.setDisplayDescription("The product that has a giant number two.")
						.setDisplayUnit("TWO")
						.setCurrentPrice(20000L)
						.setDeleted(false));
				final Product product3 = productRepository.save(new Product()
						.setDisplayName("Product Three")
						.setDisplayDescription("The product that has a giant number three.")
						.setDisplayUnit("three")
						.setCurrentPrice(30000L)
						.setDeleted(false));

				final Invoice invoice1 = new Invoice()
						.setAuthor(salesman1)
						.setDisplayDescription("New invoice ONE")
						.setStatus(InvoiceStatus.CREATED)
						.setDeleted(false);
				invoice1.setItems(List.of(new InvoiceItem()
						.setInvoice(invoice1)
						.setProduct(product1)
						.setQuantity(100L)
						.setUnitPrice(10000L)
						.setDeleted(false)));
				invoice1.setComments(List.of(new InvoiceComment()
						.setInvoice(invoice1)
						.setUser(salesman1)
						.setDisplayComment("Comment one")
						.setStatusBefore(InvoiceStatus.CREATED)
						.setStatusAfter(InvoiceStatus.CREATED)
						.setDeleted(false)));

				final Invoice invoice2 = new Invoice()
						.setAuthor(salesman2)
						.setDisplayDescription("New invoice TWO")
						.setStatus(InvoiceStatus.CREATED)
						.setDeleted(false);
				invoice2.setItems(List.of(new InvoiceItem()
						.setInvoice(invoice2)
						.setProduct(product2)
						.setQuantity(100L)
						.setUnitPrice(10000L)
						.setDeleted(false)));
				invoice2.setComments(List.of(new InvoiceComment()
						.setInvoice(invoice2)
						.setUser(salesman2)
						.setDisplayComment("Comment two")
						.setStatusBefore(InvoiceStatus.CREATED)
						.setStatusAfter(InvoiceStatus.CREATED)
						.setDeleted(false)));

				final Invoice invoice3 = new Invoice()
						.setAuthor(salesman3)
						.setDisplayDescription("New invoice THREE")
						.setStatus(InvoiceStatus.CREATED)
						.setDeleted(false);
				invoice3.setItems(List.of(new InvoiceItem()
						.setInvoice(invoice3)
						.setProduct(product3)
						.setQuantity(100L)
						.setUnitPrice(10000L)
						.setDeleted(false)));
				invoice3.setComments(List.of(new InvoiceComment()
						.setInvoice(invoice3)
						.setUser(salesman3)
						.setDisplayComment("Comment three")
						.setStatusBefore(InvoiceStatus.CREATED)
						.setStatusAfter(InvoiceStatus.CREATED)
						.setDeleted(false)));

				invoiceRepository.save(invoice1);
				invoiceRepository.save(invoice2);
				invoiceRepository.save(invoice3);
			}
		};
	}
}
