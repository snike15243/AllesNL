package com.allesnl.mini_app_service.contracts.miniapp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MiniAppResponse {
    private int responseCode;
    private String message;
    private String data;

    public MiniAppResponse() {
    }

    public MiniAppResponse(int responseCode, String message, String data) {
        this.responseCode = responseCode;
        this.message = message;
        this.data = data;
    }

    public static MiniAppResponse success(String data) {
        return new MiniAppResponse(200, "Success", data);
    }

    public static MiniAppResponse failure() {
        return new MiniAppResponse(500, "Failure", null);
    }

}