package org.exoplatform.ide.git.server.provider.rest;


import com.codenvy.commons.json.JsonHelper;

import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.ssh.server.SshKeyStoreException;
import org.exoplatform.ide.git.server.provider.GitVendorService;
import org.exoplatform.ide.git.server.provider.GitVendorServiceProvider;
import org.exoplatform.ide.git.shared.GitUrlVendorInfo;

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
@Path("{ws-name}/git-service")
public class ProviderService {
    @PathParam("ws-name")
    private String workspaceName;

    @Inject
    private GitVendorServiceProvider gitProvider;

    /**
     * Information about Git service which support operation for specific git url.
     * See {@link org.exoplatform.ide.git.shared.GitUrlVendorInfo}.
     *
     * @param vcsUrl
     *         git url for repository
     * @return See {@link org.exoplatform.ide.git.shared.GitUrlVendorInfo}
     * @throws ProviderException
     */
    @GET
    @Path("info")
    @RolesAllowed({"workspace/developer"})
    @Produces(MediaType.APPLICATION_JSON)
    public GitUrlVendorInfo getInfoForVcsUrl(@QueryParam("vcsurl") String vcsUrl) throws ProviderException {
        GitVendorService gitService = gitProvider.getGitServiceByUrlMatch(vcsUrl);
        return new GitUrlVendorInfo(gitService == null ? null : gitService.getVendorName(),
                                    gitService == null ? null : gitService.getVendorBaseHost(),
                                    gitService == null ? null : gitService.getVendorOAuthScopes(),
                                    GitVendorService.isVcsUrlIsSSH(vcsUrl));
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
    @RolesAllowed({"workspace/developer"})
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
     * @return {@link org.exoplatform.ide.git.server.provider.rest.ProviderException} with 404 error, not found
     */
    private ProviderException getProviderException(String vendorName) {
        return new ProviderException(HTTPStatus.NOT_FOUND, "Provider for vendor " + vendorName + " not found.", MediaType.TEXT_PLAIN, null);
    }
}
