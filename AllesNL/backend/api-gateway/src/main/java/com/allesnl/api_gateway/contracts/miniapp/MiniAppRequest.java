package com.allesnl.api_gateway.contracts.miniapp;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiniAppRequest {
    private String appId;
    private String userId;
    private String payload;
}