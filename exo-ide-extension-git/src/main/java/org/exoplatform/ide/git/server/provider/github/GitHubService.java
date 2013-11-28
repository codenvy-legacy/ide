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

import com.codenvy.commons.json.JsonParseException;
import com.codenvy.ide.commons.server.ParsingResponseException;

import org.exoplatform.ide.git.server.provider.rest.ProviderException;
import org.exoplatform.ide.git.shared.Collaborators;
import org.exoplatform.ide.git.shared.GitHubRepository;
import org.exoplatform.ide.git.shared.GitHubRepositoryList;
import org.exoplatform.ide.git.shared.GitHubUser;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

/**
 * REST service to get the list of repositories from GitHub (where sample projects are located).
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: GithubSamplesService.java Aug 29, 2011 9:59:02 AM vereshchaka $
 */
@Path("{ws-name}/github")
public class GitHubService {
    @Inject
    GitHub github;

    public GitHubService() {
    }

    @Path("list/user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubRepositoryList listRepositoriesByUser(@QueryParam("username") String userName)
            throws ProviderException, JsonParseException {
        return github.listUserPublicRepositories(userName);
    }

    @Path("list/org")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubRepositoryList listRepositoriesByOrganization(@QueryParam("organization") String organization)
            throws ProviderException, JsonParseException {
        return github.listAllOrganizationRepositories(organization);
    }

    @Path("list/account")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubRepositoryList listRepositoriesByAccount(@QueryParam("account") String account)
            throws ProviderException, JsonParseException {
        try {
            //First, try to retrieve organization repositories:
            return github.listAllOrganizationRepositories(account);
        } catch (ProviderException ghe) {
            //If account is not organization, then try by user name:
            if (ghe.getResponseStatus() == 404) {
                return github.listUserPublicRepositories(account);
            } else {
                throw ghe;
            }
        }
    }

    @Path("list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubRepositoryList listRepositories() throws ProviderException, JsonParseException {
        return github.listCurrentUserRepositories();
    }


    @Path("list/available")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<GitHubRepository>> availableRepositories() throws ProviderException, JsonParseException {
        return github.availableRepositoriesList();
    }

    @Path("page")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubRepositoryList getPage(@QueryParam("url") String url) throws ProviderException, JsonParseException {
        return github.getPage(url);
    }

    @Path("orgs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> listOrganizations() throws ProviderException, JsonParseException {
        return github.listOrganizations();
    }

    @Path("user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubUser getUserInfo() throws ProviderException, JsonParseException {
        return github.getGithubUser();
    }

    @GET
    @Path("collaborators/{user}/{repository}")
    @Produces(MediaType.APPLICATION_JSON)
    public Collaborators collaborators(@PathParam("user") String user,
                                       @PathParam("repository") String repository)
            throws ProviderException, ParsingResponseException, JsonParseException {
        return github.getCollaborators(user, repository);
    }
}
