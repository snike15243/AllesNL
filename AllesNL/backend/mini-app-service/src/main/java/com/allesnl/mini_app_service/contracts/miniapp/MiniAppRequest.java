package com.allesnl.mini_app_service.contracts.miniapp;

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