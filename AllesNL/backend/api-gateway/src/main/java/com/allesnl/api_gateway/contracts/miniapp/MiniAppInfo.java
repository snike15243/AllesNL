package com.allesnl.api_gateway.contracts.miniapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MiniAppInfo {
    Long appId;
    String appName;
    String appDescription;
    String baseUrl;
    String logoUrl;
}
