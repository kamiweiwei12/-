package edu.sysu.snowfield.common.web;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/** 声明 Swagger UI 可用的 JWT Bearer 输入，不对公开端点施加全局安全要求。 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "在 Authorization 请求头中发送 Bearer access token")
public class OpenApiSecurityConfiguration {
}
