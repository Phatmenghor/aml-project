package com.cpbank.AML_API.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LosDownstreamRequest {
    private String customerId;
    private String status;
}
