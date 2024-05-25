package com.lojavirtual.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
// dto para devolver o acessToken e o tempo até expirar