package org.exoplatform.ide.git.server.provider;

import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonNameConventions;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.ide.commons.server.ContainerUtils;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.extension.ssh.server.SshKey;
import org.exoplatform.ide.extension.ssh.server.SshKeyStore;
import org.exoplatform.ide.extension.ssh.server.SshKeyStoreException;
import org.exoplatform.ide.git.server.provider.rest.ProviderException;
import org.exoplatform.services.security.ConversationState;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Git service, which combines some commons operations, for example upload SSH keys, checking for private repository.
 * Each Git service which will support this common operation should extends this abstract class.
 */
public abstract class GitVendorService {

    private SshKeyStore sshKeyStore;

    private final String       vendorName;
    private final String       vendorBaseHost;
    private final Pattern      vendorUrlPattern;
    private final List<String> vendorOAuthScopes;
    private final boolean      oauth2;

    protected GitVendorService(InitParams initParams) {
        this(initParams, null);
    }

    protected GitVendorService(InitParams initParams, SshKeyStore sshKeyStore) {
        this.vendorName = ContainerUtils.readValueParam(initParams, "vendorName");
        this.vendorBaseHost = ContainerUtils.readValueParam(initParams, "vendorBaseHost");
        this.vendorOAuthScopes = ContainerUtils.readValuesParam(initParams, "vendorOAuthScopes");
        this.vendorUrlPattern = Pattern.compile(ContainerUtils.readValueParam(initParams, "vendorUrlPattern", ""));
        this.oauth2 = Boolean.valueOf(ContainerUtils.readValueParam(initParams, "vendorSupportOAuth2", "false"));

        this.sshKeyStore = sshKeyStore;
    }

    /**
     * Git service short name. This value need to use when we send to client or to serve side requests for specified operations.
     * For example if we want to upload new public key for specified Git service we pass only service name to rest service.
     *
     * @return short name of Git service
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Base host for configured Git service, e.g. "github.com", "bitbucket.org".
     * Need for generation new SSH Key pair.
     *
     * @return base host for Git service.
     */
    public String getVendorBaseHost() {
        return vendorBaseHost;
    }

    /**
     * Pattern which detect that specified git url should be processed with configured Git service.
     *
     * @return pattern to mach given url
     */
    public Pattern getVendorUrlPattern() {
        return vendorUrlPattern;
    }

    /**
     * Get authorization scope for specified Gt service. This scopes sends to client-side, to allow client make successful
     * authorization for Git service. Scopes configure via configuration file, if we won't to pass scope, we don't configure them.
     *
     * @return scopes which need for authorization
     */
    public List<String> getVendorOAuthScopes() {
        return vendorOAuthScopes;
    }

    /**
     * Get label for SSH key.
     *
     * @return formatted label for public key
     */
    protected String getSSHKeyLabel() {
        return "Codenvy SSH Key (" + new SimpleDateFormat().format(new Date()) + ")";
    }

    /**
     * Support OAuth 2.0 or not.
     *
     * @return true if support.
     */
    public boolean isOAuth2() {
        return oauth2;
    }

    /**
     * Perform generate new Key pair for specified Git service. After successfully generation method call operation to upload
     * public key to specified Git service.
     *
     * @throws SshKeyStoreException
     * @throws ProviderException
     */
    public void generateAndUploadNewPublicKey() throws SshKeyStoreException, ProviderException {
        sshKeyStore.removeKeys(getVendorBaseHost());
        sshKeyStore.genKeyPair(getVendorBaseHost(), null, null);

        uploadNewPublicKey(sshKeyStore.getPublicKey(getVendorBaseHost()));
    }

    /**
     * Perform upload SSH key to Git service. Need to be implemented if Git service support upload public key to add possibility
     * to clone repositories via SSH.
     *
     * @param publicKey
     *         generated public key
     * @throws ProviderException
     */
    public abstract void uploadNewPublicKey(SshKey publicKey) throws ProviderException;

    /**
     * Checks if specified repository is private. Need to be implemented by each Git service.
     * NOTE @vlzhukovskii need to be decided if we should have checking by API.
     *
     * @param repositoryName
     *         name of repository to be checked for private mode
     * @return true if specified repository is private
     */
    public abstract boolean isRepositoryPrivate(String repositoryName);

    /**
     * Checks if git url address is ssh url.
     *
     * @param vcsUrl
     *         git url address
     * @return true if ssh, otherwise false
     */
    public static boolean isVcsUrlIsSSH(String vcsUrl) {
        // TODO improve it to not match ssh url with some mistakes
        return vcsUrl.matches("^(?:ssh://)?(?:\\w+@)([a-zA-Z-.]+)(?::(\\d+))?+(?:/|:)(.+)$");
    }

    /**
     * Get current logged in user email.
     *
     * @return user email
     */
    protected String getUserId() {
        return ConversationState.getCurrent().getIdentity().getUserId();
    }

    /**
     * **********************************************************
     * HTTP Request Common Methods
     * **********************************************************
     */

    /**
     * Builder to construct request with specified parameters.
     */
    public class RequestBuilder {
        private String url;
        private String requestMethod;
        private String body;
        private Map<String, String> requestHeaders  = new HashMap<>();
        private List<String>        responseHeaders = new ArrayList<>();

        public RequestBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public RequestBuilder withMethod(String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public RequestBuilder withBody(String body) {
            this.body = body;
            return this;
        }

        public RequestBuilder withRequestHeader(String key, String value) {
            this.requestHeaders.put(key, value);
            return this;
        }

        public RequestBuilder withResponseHeader(String key) {
            this.responseHeaders.add(key);
            return this;
        }

        public Response makeRequest() throws ProviderException {
            return doRequest(this);
        }
    }

    /**
     * Response object which should contains response body and headers that user wants to view in feature.
     */
    public class Response {
        private String body;
        private Map<String, String> responseHeaders = new HashMap<>();

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public Map<String, String> getResponseHeaders() {
            return responseHeaders;
        }

        public void setResponseHeaders(Map<String, String> responseHeaders) {
            this.responseHeaders = responseHeaders;
        }
    }

    /**
     * Do http request for git vendor service with specified parameters setted in
     * {@link org.exoplatform.ide.git.server.provider.GitVendorService.RequestBuilder} object.
     *
     * @param request
     *         parameters for this request.
     * @return {@link org.exoplatform.ide.git.server.provider.GitVendorService.Response} object containing response body and headers which
     * should be retrieved.
     * @throws ProviderException
     */
    private Response doRequest(RequestBuilder request) throws ProviderException {
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)new URL(request.url).openConnection();
            http.setInstanceFollowRedirects(false);
            http.setRequestMethod(request.requestMethod);

            if (request.requestHeaders != null) {
                for (String key : request.requestHeaders.keySet())
                    http.setRequestProperty(key, request.requestHeaders.get(key));
            }

            if (request.body != null && !request.body.isEmpty()) {
                http.setRequestProperty(HTTPHeader.CONTENT_LENGTH, String.valueOf(request.body.length()));

                if (http.getRequestProperty(HTTPHeader.CONTENT_TYPE) == null) {
                    http.setRequestProperty(HTTPHeader.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
                }

                http.setDoOutput(true);
                http.getOutputStream().write(request.body.getBytes(Charset.defaultCharset().name()));
            }

            http.connect();

            final int code = http.getResponseCode();

            Response response = new Response();

            if (code >= 200 && code < 400) {
                response.setBody(IoUtil.readStream(http.getInputStream()));

                if (request.responseHeaders != null) {
                    for (String headerParam : request.responseHeaders) {
                        response.getResponseHeaders().put(headerParam, http.getHeaderField(headerParam));
                    }
                }
            } else {
                String body;

                if (code == HTTPStatus.UNAUTHORIZED) {
                    body = "You need to authorize to perform this operation for " + getVendorName();
                } else {
                    InputStream stream = http.getErrorStream();
                    body = stream != null ? IoUtil.readStream(stream) : null;
                }


                throw new ProviderException(code == HTTPStatus.UNAUTHORIZED ? HTTPStatus.UNAUTHORIZED
                                                                            : HTTPStatus.BAD_REQUEST,
                                            body,
                                            MediaType.TEXT_PLAIN,
                                            getVendorName());
            }

            return response;

        } catch (IOException e) {
            throw new ProviderException(HTTPStatus.INTERNAL_ERROR, e.getLocalizedMessage(), MediaType.TEXT_PLAIN, getVendorName());
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Map JSON object on POJO.
     *
     * @param json
     *         string, containing JSON text
     * @param clazz
     *         type of class which should be mapped
     * @return POJO object with type of clazz.
     * @throws JsonParseException
     */
    protected <O> O parseJsonResponse(String json, Class<O> clazz) throws JsonParseException {
        return JsonHelper.fromJson(json, clazz, null, JsonNameConventions.CAMEL_UNDERSCORE);
    }
}
