package com.pingidentity.token.validation;

import com.pingidentity.token.Token;
import com.pingidentity.token.entity.TokenInfo;
import com.pingidentity.token.service.TokenInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenReplayPreventionImpl implements TokenReplayPrevention {
    private final TokenInfoService tokenInfoService;
    private static TokenReplayPreventionImpl instance;

    /**
     * A no-arg constructor used to evaluate the implementation with automated tests.
     * <p>
     * If your implementation requires tuning parameters, this constructor should
     * assign reasonable defaults. Please feel free to create additional constructors to
     * allow any tests you choose to implement to control these parameters.
     */
    public TokenReplayPreventionImpl(TokenInfoService tokenInfoService) {
        if (instance != null) {
            throw new RuntimeException("Instance already exists");
        }
        this.tokenInfoService = tokenInfoService;
    }

    public boolean isTokenReplayed(Token token) {
        Optional<TokenInfo> tokenInfo = tokenInfoService.checkIfTokenExists(token);
        if (!tokenInfo.isPresent()) {
            tokenInfoService.addToken(tokenInfoDTO(token));
            return false;
        } else {
            // check if received token is expired or not
            boolean checkBeforeTime = token.getNotValidBefore().equals(tokenInfo.get().getBeforeTime()) || token.getNotValidBefore().isAfter(tokenInfo.get().getBeforeTime());
            boolean checkAfterTime = token.getNotValidAfter().equals(tokenInfo.get().getAfterTime()) || token.getNotValidAfter().isBefore(tokenInfo.get().getAfterTime());
            boolean checkReplayCondition = checkBeforeTime && checkAfterTime && tokenInfo.get().isStatus();
            if (checkReplayCondition) {
                return true;
            } else {
                // update the existing token as expired
                tokenInfo.get().setStatus(false);
                tokenInfoService.addToken(tokenInfo.get());
            }
            return false;
        }
    }

    public static synchronized TokenReplayPreventionImpl getInstance(TokenInfoService tokenInfoService) {
        if (instance == null) {
            synchronized (TokenReplayPreventionImpl.class) {
                instance = new TokenReplayPreventionImpl(tokenInfoService);
            }
        }
        return instance;
    }

    private TokenInfo tokenInfoDTO(Token token) {
        TokenInfo tokenInfo = new TokenInfo();
        if (token != null) {
            tokenInfo.setTokenId(token.getTokenID());
            tokenInfo.setBeforeTime(token.getNotValidBefore());
            tokenInfo.setAfterTime(token.getNotValidAfter());
            tokenInfo.setStatus(true);
        }
        return tokenInfo;
    }
}
