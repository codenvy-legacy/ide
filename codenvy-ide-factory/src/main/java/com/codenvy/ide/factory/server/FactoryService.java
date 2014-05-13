package com.codenvy.ide.factory.server;

import com.codenvy.api.core.ApiException;
import com.codenvy.api.core.rest.Service;
import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.factory.dto.Factory;
import com.codenvy.api.factory.dto.ProjectAttributes;
import com.codenvy.api.factory.dto.Variable;
import com.codenvy.api.project.server.AbstractVirtualFileEntry;
import com.codenvy.api.project.server.FolderEntry;
import com.codenvy.api.project.server.ProjectImporter;
import com.codenvy.api.project.server.ProjectImporterRegistry;
import com.codenvy.api.project.server.ProjectManager;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.commons.lang.NameGenerator;
import com.codenvy.commons.lang.Strings;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.git.server.GitConnection;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.server.NotAuthorizedException;
import com.codenvy.ide.ext.git.server.nativegit.NativeGitConnectionFactory;
import com.codenvy.ide.ext.git.shared.BranchCheckoutRequest;
import com.codenvy.ide.factory.server.variable.VariableReplacer;
import com.codenvy.vfs.impl.fs.LocalPathResolver;

import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.everrest.websockets.message.MessageConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static javax.ws.rs.core.Response.Status.*;

/**
 * @author Vladyslav Zhukovskii
 */
@Path("factory-handler/{ws-id}")
public class FactoryService extends Service {

    private static final Logger LOG = LoggerFactory.getLogger(FactoryService.class);

    @PathParam("ws-id")
    private String workspace;

    @Inject
    private ProjectManager             projectManager;
    @Inject
    private ProjectImporterRegistry    importers;
    @Inject
    private NativeGitConnectionFactory gitConnectionFactory;
    @Inject
    private LocalPathResolver          localPathResolver;

    private static final String IMPORTER             = "git";
    private static final String DEFAULT_PROJECT_NAME = "Unnamed";
    private static final String DEFAULT_BRANCH_NAME  = "temp";

    @POST
    @Path("accept")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Factory acceptFactory(final Factory factory) {
        //first, we should get instance of importer to be able to import project from git url
        final ProjectImporter importer = getGitProjectImporter();

        //try to check if factory contains project name value otherwise we set default project name to "Unnamed"
        ProjectAttributes projectAttributes = factory.getProjectattributes();
        if (projectAttributes == null) {
            projectAttributes = DtoFactory.getInstance().createDto(ProjectAttributes.class).withPname(DEFAULT_PROJECT_NAME);
            factory.setProjectattributes(projectAttributes);
        } else if (projectAttributes.getPname() == null || projectAttributes.getPname().isEmpty()) {
            projectAttributes = factory.getProjectattributes().withPname(DEFAULT_PROJECT_NAME);
        }

        List<FolderEntry> existedFolders = projectManager.getProjectsRoot(workspace).getChildFolders();

        //if project with the same name exist we should create new one with another name.
        for (FolderEntry existProject : existedFolders) {
            if (projectAttributes.getPname().equals(existProject.getName())) {
                projectAttributes.setPname(NameGenerator.generate(projectAttributes.getPname(), 4));
                break;
            }
        }

        //get newly created empty folder in which clone should be proceed
        FolderEntry projectFolder = projectManager.getProjectsRoot(workspace).createFolder(factory.getProjectattributes().getPname());

        try {
            importer.importSources(projectFolder, factory.getVcsurl());
        } catch (IOException | ApiException e) {
            if (e.getCause() != null && e.getCause() instanceof NotAuthorizedException) {
                throw halt(UNAUTHORIZED, e.getMessage(), e);
            }
            throw halt(INTERNAL_SERVER_ERROR, e.getMessage());
        }

        //get physical path to project on file system to allow native git to work with repository
        String absoluteProjectPath;
        try {
            absoluteProjectPath = localPathResolver.resolve(projectFolder.getVirtualFile());
        } catch (VirtualFileSystemException e) {
            removeInvalidClonedFolder(projectFolder);
            throw halt(INTERNAL_SERVER_ERROR, "Unable to resolve Git project directory.", e);
        }

        GitConnection gitConnection;
        try {
            gitConnection = gitConnectionFactory.getConnection(absoluteProjectPath);
        } catch (GitException e) {
            removeInvalidClonedFolder(projectFolder);
            throw halt(INTERNAL_SERVER_ERROR, "Unable to get Git connection to cloned project.", e);
        }

        if (!Strings.isNullOrEmpty(factory.getCommitid())) {
            performCheckoutToCommitId(factory.getCommitid(), gitConnection);
        } else if (!Strings.isNullOrEmpty(factory.getVcsbranch())) {
            performCheckoutToBranch(factory.getVcsbranch(), gitConnection);
        }

        if (factory.getVariables() != null && factory.getVariables().size() > 0) {
            performReplaceVariables(factory.getVariables(), absoluteProjectPath);
        }

        if (!factory.getVcsinfo()) {
            AbstractVirtualFileEntry gitFolder = projectFolder.getChild(".git");
            if (gitFolder != null && gitFolder.isFolder()) {
                pushClientNotification("Git information erased.");
                gitFolder.remove();
            }
        }

        return factory;
    }

    private void performCheckoutToCommitId(String commitId, GitConnection gitConnection) {
        BranchCheckoutRequest branchCheckoutRequest =
                DtoFactory.getInstance().createDto(BranchCheckoutRequest.class).withName(DEFAULT_BRANCH_NAME).withCreateNew(true)
                          .withStartPoint(commitId);
        try {
            gitConnection.branchCheckout(branchCheckoutRequest);
            pushClientNotification("Checkout to #" + commitId + " commit.");
        } catch (GitException e) {
            //inform user that checkout to commit id was unsuccessful
            final String format = "Failed to perform checkout to \"%s\" commit. You now switched to master branch.";
            pushClientNotification(String.format(format, commitId));
            LOG.info("Failed to checkout to commit id.", e);
        }
    }

    private void performCheckoutToBranch(String branchName, GitConnection gitConnection) {
        BranchCheckoutRequest branchCheckoutRequest = DtoFactory.getInstance().createDto(BranchCheckoutRequest.class).withName(branchName);
        try {
            gitConnection.branchCheckout(branchCheckoutRequest);
            pushClientNotification("Checkout to " + branchName + " branch.");
        } catch (GitException e) {
            //inform user that checkout to commit id was unsuccessful
            final String format = "Failed to perform checkout to \"%s\" branch. You now switched to master branch.";
            pushClientNotification(String.format(format, branchName));
        }
    }

    private void performReplaceVariables(List<Variable> variables, String absoluteProjectPath) {
        pushClientNotification("Replace variables.");
        new VariableReplacer(Paths.get(absoluteProjectPath)).performReplacement(variables);
    }

    private void pushClientNotification(String message) {
        ChannelBroadcastMessage broadcastMessage = new ChannelBroadcastMessage();
        broadcastMessage.setChannel("acceptFactoryEvents");
        broadcastMessage.setType(ChannelBroadcastMessage.Type.NONE);
        broadcastMessage.setBody(message);

        try {
            WSConnectionContext.sendMessage(broadcastMessage);
        } catch (MessageConversionException | IOException e) {
            LOG.error("Failed to send message via Websocket", e);
        }
    }

    private ProjectImporter getGitProjectImporter() {
        ProjectImporter importer = importers.getImporter(IMPORTER);
        if (importer == null) {
            throw halt(INTERNAL_SERVER_ERROR, String.format("Unable import sources project. Type '%s' is not supported.", IMPORTER));
        }

        return importer;
    }

    private WebApplicationException halt(Response.Status status, String message) {
        return halt(status, message, null);
    }

    private WebApplicationException halt(Response.Status status, String message, Throwable cause) {
        final ServiceError error = DtoFactory.getInstance().createDto(ServiceError.class).withMessage(message);
        LOG.warn(error.getMessage(), (cause != null) ? cause : null);
        return new WebApplicationException(
                Response.status(status).entity(error).type(MediaType.APPLICATION_JSON).build());
    }

    private void removeInvalidClonedFolder(FolderEntry folder) {
        folder.remove();
    }
}
