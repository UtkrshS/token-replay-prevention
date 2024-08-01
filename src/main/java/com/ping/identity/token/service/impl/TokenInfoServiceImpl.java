package com.pingidentity.token.service.impl;

import antlr.TokenWithIndex;
import com.pingidentity.token.Token;
import com.pingidentity.token.entity.TokenInfo;
import com.pingidentity.token.repository.TokenInfoRepo;
import com.pingidentity.token.service.TokenInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class TokenInfoServiceImpl implements TokenInfoService {

    private final TokenInfoRepo tokenInfoRepo;

    public TokenInfoServiceImpl(TokenInfoRepo tokenInfoRepo) {
        this.tokenInfoRepo = tokenInfoRepo;
    }

    @Override
    public Optional<TokenInfo> checkIfTokenExists(Token token) {
        try {
            return tokenInfoRepo.findByTokenId(token.getTokenID());
        } catch (Exception e) {
            log.error("Exception occurred while checking the token in DB:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void addToken(TokenInfo tokenInfo) {
        try {
            tokenInfoRepo.save(tokenInfo);
        } catch (Exception e) {
            log.error("Exception occurred while saving the token:{}", e.getMessage());
        }
    }

}
