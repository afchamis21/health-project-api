package andre.chamis.healthproject.domain.auth.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import andre.chamis.healthproject.domain.auth.model.ForgotPasswordToken;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ForgotPasswordTokenRepository {
    private final ForgotPasswordTokenJpaRepository jpaRepository;

    public Optional<ForgotPasswordToken> findTokenByEmail(String email) {
        return jpaRepository.findById(email);
    }
    
}
