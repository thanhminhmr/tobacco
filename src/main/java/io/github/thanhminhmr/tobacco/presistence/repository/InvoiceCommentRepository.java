/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.presistence.repository;

import io.github.thanhminhmr.tobacco.presistence.model.InvoiceComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceCommentRepository extends JpaRepository<InvoiceComment, Long> {
}
