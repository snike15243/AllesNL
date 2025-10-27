package com.allesnl.mini_app_service.contracts.miniapp;

import lombok.*;

@Getter
@Setter
@Data
public class MiniAppInfo {
    private Long appId;
    private String appName;
    private String appDescription;
    private String baseUrl;
    private String logoUrl;
    private String authToken;


    public MiniAppInfo(Long appId, String appName, String appDescription, String baseUrl, String logoUrl, String authToken) {
        this.appId = appId;
        this.appName = appName;
        this.appDescription = appDescription;
        this.baseUrl = baseUrl;
        this.logoUrl = logoUrl;
        this.authToken = authToken;
    }

    public MiniAppDetails toMiniAppDetails() {
        return new MiniAppDetails(appId, appName, appDescription, logoUrl);
    }
}
