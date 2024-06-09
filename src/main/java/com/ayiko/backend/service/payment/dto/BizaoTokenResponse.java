package com.ayiko.backend.service.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BizaoTokenResponse {
    String access_token;
    String scope;
    String token_type;
    String expires_is;
}
