package com.medilabo.patient.config;

import com.medilabo.libs.commons.web.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(GlobalExceptionHandler.class)
public class CommonsWebImportConfig {}