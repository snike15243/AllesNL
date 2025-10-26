package com.allesnl.api_gateway.contracts.miniapp;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiniAppResponse {
    private int responseCode;
    private String message;
    private String data;

    public static MiniAppResponse success(String data) {
        return new MiniAppResponse(200, "Success", data);
    }

    public static MiniAppResponse failure() {
        return new MiniAppResponse(500, "Failure", null);
    }
}