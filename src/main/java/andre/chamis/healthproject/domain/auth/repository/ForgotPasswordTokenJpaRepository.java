package andre.chamis.healthproject.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import andre.chamis.healthproject.domain.auth.model.ForgotPasswordToken;

@Repository
interface ForgotPasswordTokenJpaRepository extends JpaRepository<ForgotPasswordToken, String> {
}
