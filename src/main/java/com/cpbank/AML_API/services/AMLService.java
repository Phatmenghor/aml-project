package com.cpbank.AML_API.services;

import com.cpbank.AML_API.dto.AMLRequest;
import com.cpbank.AML_API.dto.AMLResponse;
import com.cpbank.AML_API.dto.AmlUpdateRequest;
import com.cpbank.AML_API.dto.AmlUpdateResponse;

import javax.servlet.http.HttpServletRequest;

public interface AMLService {
    AMLResponse screenCustomer(AMLRequest request, HttpServletRequest httpRequest);
    AMLResponse checkCustomer(AMLRequest request, String authHeader, HttpServletRequest httpRequest);
    AmlUpdateResponse updateCustomer(AmlUpdateRequest request);
}