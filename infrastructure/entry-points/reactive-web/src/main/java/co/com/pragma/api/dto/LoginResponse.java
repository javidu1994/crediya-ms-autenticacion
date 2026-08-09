package co.com.pragma.api.dto;

import lombok.Builder;

@Builder
public record LoginResponse (
        String token
) {
}
