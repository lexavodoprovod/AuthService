package com.innowise.authservice.repository;

import com.innowise.authservice.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    String ALL_ACCESS_TOKEN_BY_USER_ID_JPQL = """
            select t
            from Token t
            join User u
            on t.user.id = u.id
            where t.user.id = :userId and t.loggedOut = false
            """;

    @Query(value = ALL_ACCESS_TOKEN_BY_USER_ID_JPQL)
    List<Token> findAllAccessTokensByUserId(@Param("userId") Long userId);

    Optional<Token> findTokenByAccessToken(String accessToken);

    Optional<Token> findTokenByRefreshToken(String refreshToken);
}
