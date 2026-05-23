// RefreshTokenResponseDTO
package com.JaimeAmuedoJAH.backend.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponseDTO {
    private String accessToken;
    private String refreshToken;
}