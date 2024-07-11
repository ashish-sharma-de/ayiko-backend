package com.ayiko.backend.service.payment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class BizaoTokenResponse {
    String access_token;
    String scope;
    String token_type;
    String expires_in;
}
