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


    public MiniAppInfo(Long appId, String appName, String appDescription, String baseUrl, String logoUrl) {
        this.appId = appId;
        this.appName = appName;
        this.appDescription = appDescription;
        this.baseUrl = baseUrl;
        this.logoUrl = logoUrl;
    }

    public MiniAppDetails toMiniAppDetails() {
        return new MiniAppDetails(appId, appName, appDescription, logoUrl);
    }
}
