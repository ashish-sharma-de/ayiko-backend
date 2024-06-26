package com.ayiko.backend.repository.payment;

import com.ayiko.backend.repository.payment.entity.BizaoAccessTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizaoAccessTokenRepository extends JpaRepository<BizaoAccessTokenEntity, String> {
}
