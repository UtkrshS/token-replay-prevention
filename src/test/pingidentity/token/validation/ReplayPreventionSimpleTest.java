package com.pingidentity.token.validation;

import com.pingidentity.token.Token;
import com.pingidentity.token.TokenSignature;
import com.pingidentity.token.entity.TokenInfo;
import com.pingidentity.token.repository.TokenInfoRepo;
import com.pingidentity.token.service.TokenInfoService;
import com.pingidentity.token.service.impl.TokenInfoServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

/**
 * Simple unit tests for a {@link TokenReplayPrevention} implementation
 */
@RunWith(MockitoJUnitRunner.class)
public class ReplayPreventionSimpleTest {
    @Mock
    TokenInfoRepo tokenInfoRepo;

    TokenReplayPrevention replayPrevention;

    @Mock
    private TokenInfoService tokenInfoService;

    @Before
    public void initialSetup() {
        tokenInfoService = new TokenInfoServiceImpl(tokenInfoRepo);
        replayPrevention = TokenReplayPreventionImpl.getInstance(tokenInfoService);
    }

    @Test
    public void testReplay() {

        // Create a test Token to test the TokenReplayPrevention

        // A dummy token ID
        String tokenID = "dummy-token-ID-1";

        // Some validity dates on the token
        Instant now = Instant.now();
        Instant notBefore = now.minusSeconds(20);
        Instant notAfter = now.plusSeconds(60);

        // For testing, just convert the tokenID to bytes for the raw token value.  A real token might have more stuff
        // but this is sufficient for testing the replay prevention
        byte[] rawToken = tokenID.getBytes();

        // This TokenReplayPrevention class shouldn't even look at the signature so we'll just leave it null
        TokenSignature tokenSignature = null;

        // Create a test Token
        Token token = new Token(tokenID, notBefore, notAfter, tokenSignature, rawToken);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setTokenId(tokenID);
        tokenInfo.setBeforeTime(notBefore.minusSeconds(40));
        tokenInfo.setAfterTime(notAfter.plusSeconds(50));
        tokenInfo.setStatus(true);
        // Now do some tests
        // The first check shouldn't be a replay because it's the first time TokenReplayPrevention object has seen it
        Mockito.when(tokenInfoRepo.findByTokenId(any())).thenReturn(Optional.empty());
        Mockito.when(tokenInfoService.checkIfTokenExists(token)).thenReturn(Optional.empty());
        assertTrue(!replayPrevention.isTokenReplayed(token));

        Mockito.when(tokenInfoRepo.findByTokenId(tokenID)).thenReturn(Optional.of(tokenInfo));
        Mockito.when(tokenInfoService.checkIfTokenExists(token)).thenReturn(Optional.of(tokenInfo));
        // The second check should be a replay because we are giving TokenReplayPrevention the same token again
        assertTrue(replayPrevention.isTokenReplayed(token));

        // Lots more could be tested...
    }
}
