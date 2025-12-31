package com.cpbank.AML_API.dto;

import lombok.Data;
import com.cpbank.AML_API.dto.AmlUpdateResult;

@Data
public class AmlUpdateResponse {
    private String status;
    private String message;
    private String appResponse;
    private String appType;
    private AmlUpdateResult result;
}
