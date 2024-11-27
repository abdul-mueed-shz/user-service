package com.abdul.admin.domain.twitter.port.in;

import com.abdul.admin.domain.twitter.model.XOauthRedirectInfo;
import com.twitter.clientlib.ApiException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface HandleXRedirectUseCase {

    String execute(XOauthRedirectInfo xOauthRedirectInfo)
            throws IOException, ExecutionException, InterruptedException, ApiException;
}
