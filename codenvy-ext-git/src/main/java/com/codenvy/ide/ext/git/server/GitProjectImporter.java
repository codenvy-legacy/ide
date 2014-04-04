package com.codenvy.ide.ext.git.server;

import com.codenvy.api.project.server.FolderEntry;
import com.codenvy.api.project.server.ProjectImporter;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.Constants;
import com.codenvy.ide.ext.git.server.nativegit.NativeGitConnectionFactory;
import com.codenvy.ide.ext.git.shared.CloneRequest;
import com.codenvy.vfs.impl.fs.LocalPathResolver;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author Vladyslav Zhukovskii
 */
@Singleton
public class GitProjectImporter implements ProjectImporter {

    private static final Logger LOG = LoggerFactory.getLogger(GitProjectImporter.class);

    private final NativeGitConnectionFactory nativeGitConnectionFactory;
    private final LocalPathResolver          localPathResolver;

    @Inject
    public GitProjectImporter(NativeGitConnectionFactory nativeGitConnectionFactory, LocalPathResolver localPathResolver) {
        this.nativeGitConnectionFactory = nativeGitConnectionFactory;
        this.localPathResolver = localPathResolver;
    }

    @Override
    public String getId() {
        return "git";
    }

    @Override
    public void importSources(FolderEntry baseFolder, String location) throws IOException {
        try {
            final String absoluteBaseFolderPath = localPathResolver.resolve(baseFolder.getVirtualFile());
            GitConnection gitConnection = nativeGitConnectionFactory.getConnection(absoluteBaseFolderPath);

            CloneRequest cloneRequest =
                    DtoFactory.getInstance().createDto(CloneRequest.class).withWorkingDir(absoluteBaseFolderPath).withRemoteName("origin")
                              .withRemoteUri(location);

            gitConnection.clone(cloneRequest);

            String propertyFileContent = "{\"type\":\"" + Constants.NAMELESS_ID + "\"}";
            FolderEntry projectMetaFolder = baseFolder.createFolder(".codenvy");
            projectMetaFolder.createFile("project", propertyFileContent.getBytes(), MediaType.APPLICATION_JSON_TYPE.getType());
        } catch (VirtualFileSystemException | GitException | URISyntaxException e) {
            throw new IOException("Selected project cannot be imported.", e);
        }
    }
}
