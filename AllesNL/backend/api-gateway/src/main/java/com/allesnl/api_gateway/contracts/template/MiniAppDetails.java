package com.allesnl.api_gateway.contracts.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MiniAppDetails(long registrationId, String appName, String appDescription, String logoUrl) {}
