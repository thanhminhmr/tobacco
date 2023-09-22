/*
 * Copyright (c) 2023 Mai Thanh Minh (a.k.a. thanhminhmr). All rights reserved.
 */

package io.github.thanhminhmr.tobacco.presistence.model;

public enum InvoiceStatus {
	CREATED,
	DONE,
	ABORTED,
	WAIT_FOR_SALES_MANAGER_APPROVAL,
	WAIT_FOR_ACCOUNTANT_APPROVAL,
	WAIT_FOR_MARKET_DIRECTOR_APPROVAL,
	WAIT_FOR_ACCOUNTANT_ISSUES_INVOICE,
	WAIT_FOR_SALESMAN_RECEIVE;
}
