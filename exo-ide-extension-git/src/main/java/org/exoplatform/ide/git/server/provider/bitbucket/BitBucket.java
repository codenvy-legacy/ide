package org.exoplatform.ide.git.server.provider.bitbucket;

import com.codenvy.commons.json.JsonParseException;
import com.codenvy.security.oauth.OAuthTokenProvider;
import com.codenvy.security.oauth.oauth1.OAuth1UrlInfo;
import com.codenvy.security.shared.Token;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.ssh.server.SshKey;
import org.exoplatform.ide.extension.ssh.server.SshKeyStore;
import org.exoplatform.ide.git.server.provider.GitVendorService;
import org.exoplatform.ide.git.server.provider.rest.ProviderException;
import org.scribe.model.ParameterList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Git service for BitBucket.
 */
public class BitBucket extends GitVendorService {
    private OAuthTokenProvider tokenProvider;

    private static final Logger LOG = LoggerFactory.getLogger(BitBucket.class);

    public BitBucket(InitParams initParams, SshKeyStore sshKeyStore, OAuthTokenProvider tokenProvider) {
        super(initParams, sshKeyStore);
        this.tokenProvider = tokenProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void uploadNewPublicKey(SshKey publicKey) throws ProviderException {
        try {
            BitBucketProfile userBitBucketProfile = getUserInfo();
            final String url = "https://bitbucket.org/api/1.0/users/" + userBitBucketProfile.getUser().getUsername() + "/ssh-keys";

            LOG.debug("Getting user from BitBucket with name {0}", userBitBucketProfile.getUser().getUsername());

            Map<String, String> body = new HashMap<>(2);
            body.put("label", getSSHKeyLabel());
            body.put("key", new String(publicKey.getBytes()));

            LOG.debug("Body for creating request for upload new public key: {0}", body.toString());

            Response response = new RequestBuilder().withUrl(url)
                                                    .withBody(new ParameterList(body).asFormUrlEncodedString())
                                                    .withMethod(HTTPMethod.POST)
                                                    .withRequestHeader(HTTPHeader.AUTHORIZATION,
                                                                       getAuthHeader(url, HTTPMethod.POST, null, body))
                                                    .makeRequest();

            LOG.debug("Uploading public key response: {0}", response.getBody());
        } catch (JsonParseException e) {
            LOG.error("JSON parsing exception for {0}: {1}", getVendorName(), e.getLocalizedMessage());
            throw new ProviderException(HTTPStatus.INTERNAL_ERROR, e.getLocalizedMessage(), MimeType.TEXT_PLAIN, getVendorName());
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRepositoryPrivate(String repositoryName) {
        return false;
    }

    /**
     * Get user profile information from BitBucket for feature constructing requests with given information about user.
     *
     * @return object, containing all necessary information about current logged in user and its repositories
     * @throws ProviderException
     * @throws JsonParseException
     */
    private BitBucketProfile getUserInfo() throws ProviderException, JsonParseException {
        final String url = "https://bitbucket.org/api/1.0/user";

        Response response = new RequestBuilder().withUrl(url)
                                                .withMethod(HTTPMethod.GET)
                                                .withRequestHeader(HTTPHeader.AUTHORIZATION, getAuthHeader(url, HTTPMethod.GET, null, null))
                                                .makeRequest();

        return parseJsonResponse(response.getBody(), BitBucketProfile.class);
    }

    /**
     * Get authentication header for OAuth 1.0 request with given url and body/query-parameters.
     *
     * @param url
     *         url for request
     * @param requestMethod
     *         request method, e.g. GET/POST/PUT etc
     * @param queryParams
     *         query string parameters if presents
     * @param bodyParams
     *         body parameters if presents
     * @return String containing authentication Header which should be placed in every request
     */
    private String getAuthHeader(String url, String requestMethod, Map<String, String> queryParams, Map<String, String> bodyParams) {

        Token token = null;
        try {
            token = tokenProvider.getToken(getVendorName(), getUserId(), new OAuth1UrlInfo(url, requestMethod, queryParams, bodyParams));
        } catch (IOException e) {
            LOG.warn("Failed to obtain token for Bitbucket", e);
        }

        if (token != null && token.getAuthHeader() != null && !token.getAuthHeader().isEmpty()) {
            return token.getAuthHeader();
        }

        return "";
    }
}
