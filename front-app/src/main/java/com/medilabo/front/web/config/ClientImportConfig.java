package com.medilabo.front.web.config;

import com.medilabo.libs.client.config.ClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ClientAutoConfiguration.class)
public class ClientImportConfig {}