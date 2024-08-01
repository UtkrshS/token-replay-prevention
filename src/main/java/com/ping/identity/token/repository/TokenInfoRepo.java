package com.pingidentity.token.repository;

import com.pingidentity.token.entity.TokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenInfoRepo extends JpaRepository<TokenInfo, Integer> {
    Optional<TokenInfo> findByTokenId(String tokenId);
}
