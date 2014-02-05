/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.git.server.provider.github;

import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.security.oauth.OAuthTokenProvider;
import com.codenvy.security.shared.Token;

import org.everrest.core.impl.provider.json.JsonValue;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.ssh.server.SshKey;
import org.exoplatform.ide.extension.ssh.server.SshKeyStore;
import org.exoplatform.ide.git.server.provider.GitVendorService;
import org.exoplatform.ide.git.server.provider.rest.ProviderException;
import org.exoplatform.ide.git.shared.Collaborators;
import org.exoplatform.ide.git.shared.GitHubRepository;
import org.exoplatform.ide.git.shared.GitHubRepositoryList;
import org.exoplatform.ide.git.shared.GitHubRepositoryListImpl;
import org.exoplatform.ide.git.shared.GitHubUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains methods for retrieving data from GitHub and processing it before sending to client side.
 */
public class GitHub extends GitVendorService {

    private final static Logger LOG = LoggerFactory.getLogger(GitHub.class);

    private final OAuthTokenProvider oauthTokenProvider;

    /**
     * Pattern to parse Link header from GitHub response.
     */
    private final Pattern linkPattern = Pattern.compile("<(.+)>;\\srel=\"(\\w+)\"");

    /**
     * Name of the Link header from GitHub response.
     */
    private static final String HEADER_LINK = "Link";

    public GitHub(InitParams initParams, OAuthTokenProvider oauthTokenProvider, SshKeyStore sshKeyStore) {
        super(initParams, sshKeyStore);
        this.oauthTokenProvider = oauthTokenProvider;
    }

    /**
     * Get the list of public repositories by user's name.
     *
     * @param user
     *         name of user
     * @return {@link GitHubRepositoryList} list of GitHub repositories
     * @throws ProviderException
     *         if GitHub server return unexpected or error status for request
     */
    public GitHubRepositoryList listUserPublicRepositories(String user) throws ProviderException, JsonParseException {

        Response response = new RequestBuilder().withUrl(String.format("https://api.github.com/users/%s/repos", user) + getTokenString())
                                                .withMethod(HTTPMethod.GET)
                                                .withResponseHeader(HEADER_LINK)
                                                .withRequestHeader(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                                                .makeRequest();

        GitHubRepositoryList gitHubRepositoryList = parseLinkHeader(new GitHubRepositoryListImpl(),
                                                                    response.getResponseHeaders().get(HEADER_LINK));

        GitHubRepository[] repositories = parseJsonResponse(response.getBody(), GitHubRepository[].class);
        gitHubRepositoryList.setRepositories(Arrays.asList(repositories));
        return gitHubRepositoryList;
    }

    /**
     * Get the list of all repositories by organization name.
     *
     * @param organization
     *         name of user
     * @return {@link GitHubRepositoryList} list of GitHub repositories
     * @throws ProviderException
     *         if GitHub server return unexpected or error status for request
     */
    public GitHubRepositoryList listAllOrganizationRepositories(String organization) throws ProviderException, JsonParseException {
        Response response = new RequestBuilder()
                .withUrl(String.format("https://api.github.com/orgs/%s/repos", organization) + getTokenString())
                .withMethod(HTTPMethod.GET)
                .withResponseHeader(HEADER_LINK)
                .withRequestHeader(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                .makeRequest();

        GitHubRepositoryList gitHubRepositoryList = parseLinkHeader(new GitHubRepositoryListImpl(),
                                                                    response.getResponseHeaders().get(HEADER_LINK));

        GitHubRepository[] repositories = parseJsonResponse(response.getBody(), GitHubRepository[].class);
        gitHubRepositoryList.setRepositories(Arrays.asList(repositories));
        return gitHubRepositoryList;
    }

    /**
     * Get the page of GitHub repositories by it's link.
     *
     * @param url
     *         location of the page with repositories
     * @return {@link GitHubRepositoryList} list of GitHub repositories
     * @throws ProviderException
     *         if GitHub server return unexpected or error status for request
     */
    public GitHubRepositoryList getPage(String url) throws ProviderException, JsonParseException {
        Response response = new RequestBuilder().withUrl(url + (url.contains("?") ? getTokenString().replace('?', '&') : getTokenString()))
                                                .withMethod(HTTPMethod.GET)
                                                .withResponseHeader(HEADER_LINK)
                                                .withRequestHeader(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                                                .makeRequest();

        GitHubRepositoryList gitHubRepositoryList = parseLinkHeader(new GitHubRepositoryListImpl(),
                                                                    response.getResponseHeaders().get(HEADER_LINK));

        GitHubRepository[] repositories = parseJsonResponse(response.getBody(), GitHubRepository[].class);
        gitHubRepositoryList.setRepositories(Arrays.asList(repositories));
        return gitHubRepositoryList;
    }

    /**
     * Get the list of the repositories of the current authorized user.
     *
     * @return {@link GitHubRepositoryList} list of GitHub repositories
     * @throws ProviderException
     *         if GitHub server return unexpected or error status for request
     */
    public GitHubRepositoryList listCurrentUserRepositories() throws ProviderException, JsonParseException {
        Response response = new RequestBuilder().withUrl("https://api.github.com/user/repos" + getTokenString())
                                                .withMethod(HTTPMethod.GET)
                                                .withRequestHeader(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                                                .withResponseHeader(HEADER_LINK)
                                                .makeRequest();

        GitHubRepositoryList gitHubRepositoryList = parseLinkHeader(new GitHubRepositoryListImpl(),
                                                                    response.getResponseHeaders().get(HEADER_LINK));

        GitHubRepository[] repositories = parseJsonResponse(response.getBody(), GitHubRepository[].class);
        gitHubRepositoryList.setRepositories(Arrays.asList(repositories));
        return gitHubRepositoryList;
    }

    /**
     * Get the Map which contains available repositories in format Map<Organization name, List<Available repositories>>.
     *
     * @return ap which contains available repositories in format Map<Organization name, List<Available repositories>>
     * @throws ProviderException
     *         if GitHub server return unexpected or error status for request
     */
    public Map<String, List<GitHubRepository>> availableRepositoriesList() throws ProviderException, JsonParseException {
        Map<String, List<GitHubRepository>> repoList = new HashMap<>();
        repoList.put(getGithubUser().getLogin(), listCurrentUserRepositories().getRepositories());
        for (String organizationId : listOrganizations()) {
            repoList.put(organizationId, listAllOrganizationRepositories(organizationId).getRepositories());
        }
        return repoList;
    }

    /**
     * Get the array of the organizations of the authorized user.
     *
     * @return list of organizations
     * @throws ProviderException
     *         if GitHub server return unexpected or error status for request
     */
    public List<String> listOrganizations() throws ProviderException, JsonParseException {
        final List<String> result = new ArrayList<>();
        final Response response = new RequestBuilder().withUrl("https://api.github.com/user/orgs" + getTokenString())
                                                      .withMethod(HTTPMethod.GET)
                                                      .withRequestHeader(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                                                      .makeRequest();
        JsonValue rootEl = JsonHelper.parseJson(response.getBody());
        if (rootEl.isArray()) {
            Iterator<JsonValue> iter = rootEl.getElements();
            while (iter.hasNext()) {
                result.add(iter.next().getElement("login").getStringValue());
            }
        }
        return result;
    }

    /**
     * Get authorized user's information.
     *
     * @return {@link GitHubUser} user information
     * @throws ProviderException
     *         if GitHub server return unexpected or error status for request
     */
    public GitHubUser getGithubUser() throws ProviderException, JsonParseException {

        Response response = new RequestBuilder().withUrl("https://api.github.com/user" + getTokenString())
                                                .withMethod(HTTPMethod.GET)
                                                .makeRequest();

        return parseJsonResponse(response.getBody(), GitHubUserImpl.class);
    }

    public Collaborators getCollaborators(String user, String repository) throws ProviderException, JsonParseException {
        final String url = String.format("https://api.github.com/repos/%s/%s/collaborators", user, repository);

        Response response = new RequestBuilder().withUrl(url + getTokenString())
                                                .withMethod(HTTPMethod.GET)
                                                .withRequestHeader(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                                                .makeRequest();
        // It seems that collaborators response does not contains all required fields.
        // Iterate over list and request more info about each user.
        final GitHubUserImpl[] collaborators = parseJsonResponse(response.getBody(), GitHubUserImpl[].class);
        final Collaborators myCollaborators = new CollaboratorsImpl();

        for (GitHubUserImpl collaborator : collaborators) {

            response = new RequestBuilder().withUrl(collaborator.getUrl() + getTokenString())
                                           .withMethod(HTTPMethod.GET)
                                           .withRequestHeader(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                                           .makeRequest();

            GitHubUserImpl gitHubUser = parseJsonResponse(response.getBody(), GitHubUserImpl.class);
            String email = gitHubUser.getEmail();


            if (!(email == null || email.isEmpty() || email.equals(getUserId()))) {
                myCollaborators.getCollaborators().add(gitHubUser);
            }
        }

        return myCollaborators;
    }

    /**
     * ************************************************************************************
     * SSH Public key upload operation
     * *************************************************************************************
     */

    /** {@inheritDoc} */
    @Override
    public void uploadNewPublicKey(SshKey publicKey) throws ProviderException {
        Map<String, String> postParams = new HashMap<>(2);
        postParams.put("title", getSSHKeyLabel());
        postParams.put("key", new String(publicKey.getBytes()));

        new RequestBuilder().withUrl("https://api.github.com/user/keys" + getTokenString())
                            .withMethod(HTTPMethod.POST)
                            .withRequestHeader(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                            .withRequestHeader(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                            .withBody(JsonHelper.toJson(postParams))
                            .makeRequest();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRepositoryPrivate(String repositoryName) {
        return false;
    }

    /**
     * Parse Link header to retrieve page location. Example of link header:
     * <code><https://api.github.com/organizations/259384/repos?page=3&access_token=123>; rel="next",
     * <https://api.github.com/organizations/259384/repos?page=3&access_token=123>; rel="last",
     * <https://api.github.com/organizations/259384/repos?page=1&access_token=123>; rel="first",
     * <https://api.github.com/organizations/259384/repos?page=1&access_token=123>; rel="prev"
     * </code>
     *
     * @param repositoryList
     *         repository list
     * @param linkHeader
     *         the value of link header
     */
    private GitHubRepositoryList parseLinkHeader(GitHubRepositoryList repositoryList, String linkHeader) {
        if (linkHeader == null || linkHeader.isEmpty()) {
            return repositoryList;
        }
        String[] links = linkHeader.split(",");
        for (String link : links) {
            Matcher matcher = linkPattern.matcher(link.trim());
            if (matcher.matches() && matcher.groupCount() >= 2) {
                // First group is the page's location:
                String value = matcher.group(1);
                // Remove the value of access_token parameter if exists, not to be send to client:
                value = value.replaceFirst("access_token=\\w+&?", "");
                // Second group is page's type
                String rel = matcher.group(2);
                switch (rel) {
                    case "first":
                        repositoryList.setFirstPage(value);
                        break;
                    case "last":
                        repositoryList.setLastPage(value);
                        break;
                    case "next":
                        repositoryList.setNextPage(value);
                        break;
                    case "prev":
                        repositoryList.setPrevPage(value);
                        break;
                }
            }
        }

        return repositoryList;
    }

    /**
     * ************************************************************************************
     * Common methods
     * *************************************************************************************
     */
    public String getTokenString() {
        Token token = null;
        try {
            token = oauthTokenProvider.getToken(getVendorName(), getUserId());
        } catch (IOException e) {
            LOG.warn("Failed to obtain token for GitHub", e);
        }

        StringBuilder oauthToken = new StringBuilder("?access_token=");

        if (token != null && token.getToken() != null && !token.getToken().isEmpty()) {
            oauthToken.append(token.getToken());
        }

        return oauthToken.toString();
    }
}
