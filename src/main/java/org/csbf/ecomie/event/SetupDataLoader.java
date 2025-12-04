package org.csbf.ecomie.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.constant.TokenType;
import org.csbf.ecomie.entity.UserEntity;
import org.csbf.ecomie.entity.UserTokenEntity;
import org.csbf.ecomie.repository.UserRepository;
import org.csbf.ecomie.repository.UserTokenRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final Environment env;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserTokenRepository userTokenRepo;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!userRepo.existsByEmail(env.getProperty("super.admin.email"))) {
            UserEntity superAdminUserEntity = UserEntity.builder()
                    .email(env.getProperty("super.admin.email"))
                    .firstName(env.getProperty("super.admin.first-name"))
                    .lastName(env.getProperty("super.admin.last-name"))
                    .phoneNumber(env.getProperty("super.admin.phone-number"))
                    .password(passwordEncoder.encode(env.getProperty("super.admin.password")))
//                    .roles(Role.SUPER_ADMIN.name()+"-"+Role.ADMIN.name()+"-"+Role.ECOMIEST.name()+"-"+Role.MISSIONARY.name()+"-"+Role.PRAYER_WARRIOR.name()+"-"+Role.SPONSOR.name()+"-"+Role.USER.name())
                    .role(Role.SUPER_ADMIN)
                    .country(env.getProperty("super.admin.country"))
                    .region(env.getProperty("super.admin.region"))
                    .city(env.getProperty("super.admin.city"))
                    .language(env.getProperty("super.admin.language"))
                    .accountBlocked(false)
                    .accountEnabled(true)
                    .accountSoftDeleted(false)
                    .build();
            userRepo.save(superAdminUserEntity);

            UserTokenEntity superAdminEmailVerificationToken = UserTokenEntity.builder()
                    .user(superAdminUserEntity)
                    .type(TokenType.EMAIL_VERIFICATION)
                    .token(env.getProperty("super.admin.email-verification-token"))
                    .build();
            userTokenRepo.save(superAdminEmailVerificationToken);
        }
    }
}
