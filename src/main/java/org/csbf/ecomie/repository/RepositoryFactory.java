package org.csbf.ecomie.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RepositoryFactory {

    private static RepositoryFactory instance;

    private final SessionRepository sessionRepo;

    public static SessionRepository getSessionRepository() {
        return instance.sessionRepo;
    }

//    public static <T, ID> JpaRepository<T, ID> getRepository(Class<? extends JpaRepository<T, ID>> repositoryClass) {
//        if (repositoryClass == UserRepository.class) {
//            return (JpaRepository<T, ID>) instance.userRepository;
//        }
//        throw new IllegalArgumentException("Unsupported repository type: " + repositoryClass);
//    }
}