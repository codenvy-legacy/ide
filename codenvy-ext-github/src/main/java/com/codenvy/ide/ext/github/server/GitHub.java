/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.github.server;

import com.codenvy.api.auth.oauth.OAuthTokenProvider;
import com.codenvy.api.auth.shared.dto.OAuthToken;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonNameConventions;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.commons.ParsingResponseException;
import com.codenvy.ide.ext.github.shared.Collaborators;
import com.codenvy.ide.ext.github.shared.GitHubRepository;
import com.codenvy.ide.ext.github.shared.GitHubRepositoryList;
import com.codenvy.ide.ext.github.shared.GitHubUser;

import org.everrest.core.impl.provider.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
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
 *
 * @author Oksana Vereshchaka
 */
public class GitHub {

    private final static Logger LOG = LoggerFactory.getLogger(GitHub.class);

    /** Predefined name of GitHub user. Use it to make possible for users to clone repositories with samples. */
    private final String             myGitHubUser;
    private final OAuthTokenProvider oauthTokenProvider;

    /** Pattern to parse Link header from GitHub response. */
    private final Pattern linkPattern = Pattern.compile("<(.+)>;\\srel=\"(\\w+)\"");

    /** Links' delimiter. */
    private static final String DELIM_LINKS = ",";

    /** Name of the Link header from GitHub response. */
    private static final String HEADER_LINK = "Link";

    /** Name of the link for the first page. */
    private static final String META_FIRST = "first";

    /** Name of the link for the last page. */
    private static final String META_LAST = "last";

    /** Name of the link for the previous page. */
    private static final String META_PREV = "prev";

    /** Name of the link for the next page. */
    private static final String META_NEXT = "next";

    @Inject
    public GitHub(@Named("github.user") String myGitHubUser,
                  OAuthTokenProvider oauthTokenProvider) {
        this.myGitHubUser = myGitHubUser;
        this.oauthTokenProvider = oauthTokenProvider;
    }

    /**
     * Get the list of public repositories by user's name.
     *
     * @param user
     *         name of user
     * @return {@link GitHubRepositoryList} list of GitHub repositories
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubRepositoryList listUserPublicRepositories(String user) throws IOException, GitHubException, ParsingResponseException {
        user = (user == null || user.isEmpty()) ? myGitHubUser : user;
        if (user == null) {
            LOG.error("Git user is not set.");
            throw new IllegalArgumentException("User's name must not be null.");
        }
        final String url = "https://api.github.com/users/" + user + "/repos";
        final String method = "GET";
        GitHubRepositoryList gitHubRepositoryList = DtoFactory.getInstance().createDto(GitHubRepositoryList.class);
        String response = doJsonRequest(url, method, 200, gitHubRepositoryList);
        GitHubRepository[] repositories = parseJsonResponse(response, GitHubRepository[].class, null);
        gitHubRepositoryList.setRepositories(Arrays.asList(repositories));
        return gitHubRepositoryList;
    }

    /**
     * Get the list of all repositories by organization name.
     *
     * @param organization
     *         name of user
     * @return {@link GitHubRepositoryList} list of GitHub repositories
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubRepositoryList listAllOrganizationRepositories(String organization) throws IOException,
                                                                                            GitHubException,
                                                                                            ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String url = "https://api.github.com/orgs/" + organization + "/repos?access_token=" + oauthToken;
        final String method = "GET";
        GitHubRepositoryList gitHubRepositoryList = DtoFactory.getInstance().createDto(GitHubRepositoryList.class);
        final String response = doJsonRequest(url, method, 200, gitHubRepositoryList);
        GitHubRepository[] repositories = parseJsonResponse(response, GitHubRepository[].class, null);
        gitHubRepositoryList.setRepositories(Arrays.asList(repositories));
        return gitHubRepositoryList;
    }

    /**
     * Get the page of GitHub repositories by it's link.
     *
     * @param url
     *         location of the page with repositories
     * @return {@link GitHubRepositoryList} list of GitHub repositories
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubRepositoryList getPage(String url) throws IOException,
                                                           GitHubException,
                                                           ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String method = "GET";
        url += "&access_token=" + oauthToken;
        GitHubRepositoryList gitHubRepositoryList = DtoFactory.getInstance().createDto(GitHubRepositoryList.class);
        final String response = doJsonRequest(url, method, 200, gitHubRepositoryList);
        GitHubRepository[] repositories = parseJsonResponse(response, GitHubRepository[].class, null);
        gitHubRepositoryList.setRepositories(Arrays.asList(repositories));
        return gitHubRepositoryList;
    }

    /**
     * Get the list of the repositories of the current authorized user.
     *
     * @return {@link GitHubRepositoryList} list of GitHub repositories
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubRepositoryList listCurrentUserRepositories() throws IOException, GitHubException, ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String url = "https://api.github.com/user/repos?access_token=" + oauthToken;
        final String method = "GET";
        GitHubRepositoryList gitHubRepositoryList = DtoFactory.getInstance().createDto(GitHubRepositoryList.class);
        final String response = doJsonRequest(url, method, 200, gitHubRepositoryList);
        GitHubRepository[] repositories = parseJsonResponse(response, GitHubRepository[].class, null);
        gitHubRepositoryList.setRepositories(Arrays.asList(repositories));
        return gitHubRepositoryList;
    }

    /**
     * Get the Map which contains available repositories in format Map<Organization name, List<Available repositories>>.
     *
     * @return ap which contains available repositories in format Map<Organization name, List<Available repositories>>
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public Map<String, List<GitHubRepository>> availableRepositoriesList() throws IOException, GitHubException,
                                                                                  ParsingResponseException {
        Map<String, List<GitHubRepository>> repoList = new HashMap<>();
        repoList.put(getGithubUser().getLogin(), listCurrentUserRepositories().getRepositories());
        for (String organizationId : this.listOrganizations()) {
            repoList.put(organizationId, listAllOrganizationRepositories(organizationId).getRepositories());
        }
        return repoList;
    }

    /**
     * Get the array of the organizations of the authorized user.
     *
     * @return list of organizations
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public List<String> listOrganizations() throws IOException, GitHubException, ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final List<String> result = new ArrayList<>();
        final String url = "https://api.github.com/user/orgs?access_token=" + oauthToken;
        final String method = "GET";
        final String response = doJsonRequest(url, method, 200);
        try {
            JsonValue rootEl = JsonHelper.parseJson(response);
            if (rootEl.isArray()) {
                Iterator<JsonValue> iter = rootEl.getElements();
                while (iter.hasNext()) {
                    result.add(iter.next().getElement("login").getStringValue());
                }
            }

        } catch (JsonParseException e) {
            LOG.error(e.getMessage(), e);
            throw new ParsingResponseException(e);
        }
        return result;
    }

    /**
     * Get authorized user's information.
     *
     * @return {@link GitHubUser} user information
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse
     */
    public GitHubUser getGithubUser() throws IOException, GitHubException, ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String url = "https://api.github.com/user?access_token=" + oauthToken;
        final String method = "GET";
        final String response = doJsonRequest(url, method, 200);
        return parseJsonResponse(response, GitHubUser.class, null);
    }

    public Collaborators getCollaborators(String user, String repository)
            throws IOException, ParsingResponseException, GitHubException {
        final String oauthToken = getToken(getUserId());
        final Collaborators myCollaborators = DtoFactory.getInstance().createDto(Collaborators.class);
        if (oauthToken != null && oauthToken.length() != 0) {
            final String url = "https://api.github.com/repos/" + user + '/' + repository + "/collaborators?access_token=" + oauthToken;
            final String method = "GET";
            String response = doJsonRequest(url, method, 200);
            // It seems that collaborators response does not contains all required fields.
            // Iterate over list and request more info about each user.
            final GitHubUser[] collaborators = parseJsonResponse(response, GitHubUser[].class, null);
            final String userId = getUserId();
            for (GitHubUser collaborator : collaborators) {
                response = doJsonRequest(collaborator.getUrl() + "?access_token=" + oauthToken, method, 200);
                GitHubUser gitHubUser = parseJsonResponse(response, GitHubUser.class, null);
                String email = gitHubUser.getEmail();
                if (!(email == null || email.isEmpty() || email.equals(userId) || isAlreadyInvited(email))) {
                    myCollaborators.getCollaborators().add(gitHubUser);
                }
            }
        }
        return myCollaborators;
    }


    private boolean isAlreadyInvited(String collaborator) throws GitHubException {
        /*
         * try { String currentId = getUserId(); for (Invite invite : inviteService.getInvites(false)) { if (invite .getFrom() != null &&
         * invite.getFrom().equals(currentId) && invite.getEmail().equals(collaborator)) { return true; } } return false; } catch
         * (InviteException e) { throw new GitHubException(500, e.getMessage(), "text/plain"); }
         */
        // TODO : temporary, just to be able compile. Re-work it after update invitation mechanism.
        return false;
    }

    public String getToken(String user) throws GitHubException, IOException {
        OAuthToken token = oauthTokenProvider.getToken("github", user);
        String oauthToken = token != null ? token.getToken() : null;
        if (oauthToken == null || oauthToken.isEmpty()) {
            return "";
        }
        return oauthToken;
    }


    /**
     * Do json request (without authorization!)
     *
     * @param url
     *         the request url
     * @param method
     *         the request method
     * @param success
     *         expected success code of request
     * @return {@link String} response
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     */
    private String doJsonRequest(String url, String method, int success) throws IOException, GitHubException {
        return doJsonRequest(url, method, success, null, null);
    }

    /**
     * @param url
     *         the request url
     * @param method
     *         the request method
     * @param success
     *         expected success code of request
     * @param gitHubRepositoryList
     *         bean to fill pages info, if exists
     * @return {@link String} response
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     */
    private String doJsonRequest(String url, String method, int success, GitHubRepositoryList gitHubRepositoryList) throws IOException,
                                                                                                                           GitHubException {
        return doJsonRequest(url, method, success, null, gitHubRepositoryList);
    }

    /**
     * @param url
     *         the request url
     * @param method
     *         the request method
     * @param success
     *         expected success code of request
     * @param postData
     *         post data represented by json string
     * @return {@link String} response
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     */
    private String doJsonRequest(String url, String method, int success, String postData) throws IOException, GitHubException {
        return doJsonRequest(url, method, success, postData, null);
    }

    /**
     * Do json request (without authorization!)
     *
     * @param url
     *         the request url
     * @param method
     *         the request method
     * @param success
     *         expected success code of request
     * @param postData
     *         post data represented by json string
     * @param gitHubRepositoryList
     *         bean to fill pages info, if exists
     * @return {@link String} response
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     */
    private String doJsonRequest(String url, String method, int success, String postData, GitHubRepositoryList gitHubRepositoryList)
            throws IOException, GitHubException {
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)new URL(url).openConnection();
            http.setInstanceFollowRedirects(false);
            http.setRequestMethod(method);
            http.setRequestProperty("Accept", "application/json");
            if (postData != null && !postData.isEmpty()) {
                http.setRequestProperty("Content-Type", "application/json");
                http.setDoOutput(true);

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()))) {
                    writer.write(postData);
                }
            }

            if (http.getResponseCode() != success) {
                throw fault(http);
            }

            String result;
            try (InputStream input = http.getInputStream()) {
                result = readBody(input, http.getContentLength());
                if (gitHubRepositoryList != null) {
                    parseLinkHeader(gitHubRepositoryList, http.getHeaderField(HEADER_LINK));
                }
            }
            return result;
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * @param json
     *         json to parse
     * @param clazz
     *         class described in JSON
     * @param type
     * @return
     * @throws ParsingResponseException
     */
    private <O> O parseJsonResponse(String json, Class<O> clazz, Type type) throws ParsingResponseException {
        try {
            return JsonHelper.fromJson(json, clazz, type, JsonNameConventions.CAMEL_UNDERSCORE);
        } catch (JsonParseException e) {
            throw new ParsingResponseException(e.getMessage(), e);
        }
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
     * @param linkHeader
     *         the value of link header
     */
    private void parseLinkHeader(GitHubRepositoryList repositoryList, String linkHeader) {
        if (linkHeader == null || linkHeader.isEmpty()) {
            return;
        }
        String[] links = linkHeader.split(DELIM_LINKS);
        for (String link : links) {
            Matcher matcher = linkPattern.matcher(link.trim());
            if (matcher.matches() && matcher.groupCount() >= 2) {
                // First group is the page's location:
                String value = matcher.group(1);
                // Remove the value of access_token parameter if exists, not to be send to client:
                value = value.replaceFirst("access_token=\\w+&?", "");
                // Second group is page's type
                String rel = matcher.group(2);
                if (META_FIRST.equals(rel)) {
                    repositoryList.setFirstPage(value);
                } else if (META_LAST.equals(rel)) {
                    repositoryList.setLastPage(value);
                } else if (META_NEXT.equals(rel)) {
                    repositoryList.setNextPage(value);
                } else if (META_PREV.equals(rel)) {
                    repositoryList.setPrevPage(value);
                }
            }
        }
    }

    private GitHubException fault(HttpURLConnection http) throws IOException {
        InputStream errorStream = null;
        try {
            int responseCode = http.getResponseCode();
            errorStream = http.getErrorStream();
            if (errorStream == null) {
                errorStream = http.getInputStream();
            }
            if (errorStream == null) {
                return new GitHubException(responseCode, null, null);
            }

            int length = http.getContentLength();
            String body = readBody(errorStream, length);

            if (body != null) {
                if (http.getResponseCode() != 401) {
                    return new GitHubException(http.getResponseCode(), body, http.getContentType());
                } else {
                    return new GitHubException(400, body, http.getContentType());
                }
            }

            return new GitHubException(responseCode, null, null);
        } finally {
            if (errorStream != null) {
                errorStream.close();
            }
        }
    }

    private static String readBody(InputStream input, int contentLength) throws IOException {
        String body = null;
        if (contentLength > 0) {
            byte[] b = new byte[contentLength];
            int off = 0;
            int i;
            while ((i = input.read(b, off, contentLength - off)) > 0) {
                off += i;
            }
            body = new String(b);
        } else if (contentLength < 0) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int point;
            while ((point = input.read(buf)) != -1) {
                bout.write(buf, 0, point);
            }
            body = bout.toString();
        }
        return body;
    }

    /**
     * ************************************************************************************
     * Common methods
     * *************************************************************************************
     */
    private String getUserId() {
        return EnvironmentContext.getCurrent().getUser().getId();
    }
}
