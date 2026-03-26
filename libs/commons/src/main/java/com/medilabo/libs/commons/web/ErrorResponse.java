package com.medilabo.libs.commons.web;

import lombok.Builder;

@Builder
public record ErrorResponse(String code, String message) {}