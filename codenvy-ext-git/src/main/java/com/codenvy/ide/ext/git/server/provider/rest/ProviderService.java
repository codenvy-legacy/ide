package com.codenvy.ide.ext.git.server.provider.rest;


import com.codenvy.commons.json.JsonHelper;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.ext.git.server.provider.GitVendorService;
import com.codenvy.ide.ext.git.server.provider.GitVendorServiceProvider;
import com.codenvy.ide.ext.git.shared.GitUrlVendorInfo;
import com.codenvy.ide.ext.ssh.server.SshKeyStoreException;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.HTTPStatus;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/** Methods to process common operations for Git services. For example upload public key, check if repository is private, etc. */
@Path("git-service/{ws-id}")
public class ProviderService {

    @PathParam("ws-id")
    private String workspaceId;

    @Inject
    private GitVendorServiceProvider gitProvider;

    /**
     * Information about Git service which support operation for specific git url.
     * See {@link GitUrlVendorInfo}.
     *
     * @param vcsUrl
     *         git url for repository
     * @return See {@link GitUrlVendorInfo}
     * @throws ProviderException
     */
    @GET
    @Path("info")
    @RolesAllowed({"developer"})
    @Produces(MediaType.APPLICATION_JSON)
    public GitUrlVendorInfo getInfoForVcsUrl(@QueryParam("vcsurl") String vcsUrl) throws ProviderException {
        GitVendorService gitService = gitProvider.getGitServiceByUrlMatch(vcsUrl);

        return DtoFactory.getInstance().createDto(GitUrlVendorInfo.class)
                         .withVendorName(gitService.getVendorName())
                         .withVendorBaseHost(gitService.getVendorBaseHost())
                         .withOAuthScopes(gitService.getVendorOAuthScopes())
                         .withGivenUrlSSH(GitVendorService.isVcsUrlIsSSH(vcsUrl));
    }

    /**
     * Perform upload SSH public key for specific Git service, e.g. github or bitbucket.
     *
     * @param vendorName
     *         git service short name
     * @throws ProviderException
     * @throws SshKeyStoreException
     */
    @POST
    @Path("{vendor-name}/ssh/upload")
    @RolesAllowed({"developer"})
    public void uploadNewPublicKey(@PathParam("vendor-name") String vendorName) throws ProviderException, SshKeyStoreException {
        GitVendorService gitService = gitProvider.getGitService(vendorName);
        if (gitService == null) {
            throw getProviderException(vendorName);
        }

        gitService.generateAndUploadNewPublicKey();
    }

    /**
     * Check if specified repository is private.
     * NOTE @vlzhukovskii not implemented now, may be removed in feature.
     *
     * @param vendorName
     *         git service short name
     * @param repositoryName
     *         repository to check
     * @return response, contains that repository is private or not
     * @throws ProviderException
     */
    @GET
    @Path("{vendor-name}/repository/visibility")
    @Consumes(MimeType.TEXT_PLAIN)
    public Response isRepositoryPrivate(@PathParam("vendor-name") String vendorName, @QueryParam("name") String repositoryName)
            throws ProviderException {
        GitVendorService gitService = gitProvider.getGitService(vendorName);
        if (gitService == null) {
            throw getProviderException(vendorName);
        }

        boolean repoPrivate = gitService.isRepositoryPrivate(repositoryName);

        Map<String, String> response = new HashMap<>(2);
        response.put("repository", repositoryName);
        response.put("private", Boolean.toString(repoPrivate));

        return Response.ok()
                       .entity(JsonHelper.toJson(response))
                       .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                       .build();
    }

    /**
     * Create exception if we can't find git service with specified short name.
     *
     * @param vendorName
     *         git service short name
     * @return {@link ProviderException} with 404 error, not found
     */
    private ProviderException getProviderException(String vendorName) {
        return new ProviderException(HTTPStatus.NOT_FOUND, "Provider for vendor " + vendorName + " not found.", MediaType.TEXT_PLAIN, null);
    }
}
