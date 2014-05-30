package com.codenvy.ide.ext.git.shared;

import com.codenvy.dto.shared.DTO;

/**
 * Request for calling git ls-remote.
 *
 * @author Vladyslav Zhukovskii
 */
@DTO
public interface LsRemoteRequest extends GitRequest {
    /** @return url of remote repository. */
    String getRemoteUrl();

    void setRemoteUrl(String remoteUrl);

    LsRemoteRequest withRemoteUrl(String remoteUrl);

    /** @return true if request require authorization, false otherwise. */
    boolean isUseAuthorization();

    void setUseAuthorization(boolean useAuthorization);

    LsRemoteRequest withUseAuthorization(boolean useAuthorization);
}
