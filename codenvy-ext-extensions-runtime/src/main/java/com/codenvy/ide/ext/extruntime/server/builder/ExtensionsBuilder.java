/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extruntime.server.builder;

import com.codenvy.ide.ext.extruntime.dto.server.DtoServerImpls;
import com.codenvy.ide.extension.builder.shared.BuildStatus;

import org.apache.maven.model.Model;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import static com.codenvy.ide.commons.ContainerUtils.readValueParam;
import static com.codenvy.ide.commons.FileUtils.*;
import static com.codenvy.ide.commons.ZipUtils.unzip;
import static com.codenvy.ide.commons.ZipUtils.zipDir;
import static com.codenvy.ide.ext.extruntime.server.Utils.*;

/**
 * Builds Codenvy extensions inside Codenvy Platform.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class ExtensionsBuilder {
    /** System property that contains build server URL. */
    public static final  String BUILD_SERVER_BASE_URL               = "exo.ide.builder.build-server-base-url";
    /** Default name of the client module directory. */
    public static final  String CLIENT_MODULE_DIR_NAME              = "codenvy-ide-client";
    public static final  String MAIN_GWT_MODULE_DESCRIPTOR_REL_PATH =
            "src/main/resources/com/codenvy/ide/IDEPlatform.gwt.xml";
    private static final Log    LOG                                 = ExoLogger.getLogger(ExtensionsBuilder.class);
    /** Maven build server client. */
    private MavenBuilderClient builderClient;

    public ExtensionsBuilder(InitParams initParams) {
        this(readValueParam(initParams, "build-server-base-url", System.getProperty(BUILD_SERVER_BASE_URL)));
    }

    /** Constructs a new {@link ExtensionsBuilder} with provided Maven build server URL. */
    protected ExtensionsBuilder(String buildServerBaseURL) {
        this.builderClient = new MavenBuilderClient(buildServerBaseURL);
    }

    /**
     * Build Codenvy extension project inside Codenvy Platform.
     * <p/>
     * If <code>tomcatBundle</code> is <code>false</code> then returns an URL for the unbundled Codenvy web application
     * (WAR file) that may be deployed to an existing app server environment.
     * <p/>
     * Otherwise, if <code>tomcatBundle</code> is <code>true</code>, returns an URL for Tomcat bundle that contains
     * pre-deployed Codenvy web application.
     *
     * @param vfs
     *         virtual file system
     * @param projectId
     *         identifier of a project we want to run
     * @param tomcatBundle
     *         whether to create Tomcat bundle or not
     * @return URL to download the WAR or Tomcat bundle
     *         URL for the unbundled Codenvy web application (WAR file). May be deployed to an existing app server
     *         environment.
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any error in VFS
     * @throws BuilderException
     *         if an error occurs while building Codenvy app
     */
    public String build(VirtualFileSystem vfs, String projectId, boolean tomcatBundle)
            throws VirtualFileSystemException, BuilderException {
        if (projectId == null || projectId.isEmpty()) {
            throw new IllegalArgumentException("Project id required.");
        }

        try {
            final String warUrl = buildWar(vfs, projectId);
            if (!tomcatBundle) {
                return warUrl;
            } else {
                return createBundle(warUrl);
            }
        } catch (Exception e) {
            LOG.warn("Codenvy extension {} failed to build, cause: {}", projectId, e);
            throw new BuilderException(e.getMessage(), e);
        }
    }

    private String buildWar(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException,
                                                                            BuilderException, IOException {
        Project project = (Project)vfs.getItem(projectId, false, PropertyFilter.NONE_FILTER);
        File tempDir = null;
        try {
            tempDir = createTempDirectory("sdk-war-");
            final Path buildDirPath = createTempDirectory(tempDir, "build-").toPath();
            final Path clientModuleDirPath = buildDirPath.resolve(CLIENT_MODULE_DIR_NAME);
            final Path clientModulePomPath = clientModuleDirPath.resolve("pom.xml");

            Item pomFile = vfs.getItemByPath(project.getName() + "/pom.xml", null, false, PropertyFilter.NONE_FILTER);
            InputStream extPomContent = vfs.getContent(pomFile.getId()).getStream();
            Model extensionPom = readPom(extPomContent);

            if (extensionPom.getGroupId() == null || extensionPom.getArtifactId() == null ||
                extensionPom.getVersion() == null) {
                throw new BuilderException("Missing Maven artifact coordinates.");
            }

            /*********************************** Preparing *****************************************/

            InputStream codenvyPlatformDistribution = getCodenvyPlatformBinaryDistribution().openStream();
            unzip(codenvyPlatformDistribution, buildDirPath.toFile());
            final Path customModulePath = buildDirPath.resolve(extensionPom.getArtifactId());
            unzip(vfs.exportZip(projectId).getStream(), customModulePath.toFile());

            addDependencyToPom(clientModulePomPath, extensionPom);

            // Detect DTO usage and add an appropriate sections to the codenvy-ide-client/pom.xml.
            copyDtoGeneratorInvocations(extensionPom, clientModulePomPath);

            // Inherit custom GWT module.
            Path mainGwtModuleDescriptor = clientModuleDirPath.resolve(MAIN_GWT_MODULE_DESCRIPTOR_REL_PATH);
            inheritGwtModule(mainGwtModuleDescriptor, detectGwtModuleLogicalName(customModulePath));

            /*********************************** Building ******************************************/

            // Deploy custom project to Maven repository.
            File zippedExtensionProjectFile = tempDir.toPath().resolve("extension-project.zip").toFile();
            zipDir(customModulePath.toString(), customModulePath.toFile(), zippedExtensionProjectFile, ANY_FILTER);
            final String deployId = builderClient.deploy(zippedExtensionProjectFile);
            DtoServerImpls.BuildStatusImpl deployStatus =
                    DtoServerImpls.BuildStatusImpl.fromJsonString(builderClient.checkStatus(deployId));

            if (deployStatus.getStatus() != BuildStatus.Status.SUCCESSFUL) {
                LOG.error("Unable to deploy Maven artifact: " + deployStatus.getError());
                throw new BuilderException(deployStatus.getError());
            }

            // Build Codenvy platform + custom project.
            File zippedProjectFile = tempDir.toPath().resolve("project.zip").toFile();
            zipDir(clientModuleDirPath.toString(), clientModuleDirPath.toFile(), zippedProjectFile, ANY_FILTER);
            final String buildId = builderClient.build(zippedProjectFile);
            DtoServerImpls.BuildStatusImpl buildStatus =
                    DtoServerImpls.BuildStatusImpl.fromJsonString(builderClient.checkStatus(buildId));
            if (buildStatus.getStatus() != BuildStatus.Status.SUCCESSFUL) {
                LOG.error("Unable to build project: " + buildStatus.getError());
                throw new BuilderException(buildStatus.getError());
            }

            return buildStatus.getDownloadUrl();
        } finally {
            if (tempDir != null && tempDir.exists()) {
                deleteRecursive(tempDir, false);
            }
        }
    }

    private String createBundle(String warUrl) throws IOException {
        InputStream tomcatDistribution = getTomcatBinaryDistribution().openStream();

        final File tempDir = createTempDirectory("sdk-tomcat-bundle-");
        final Path tempDirPath = tempDir.toPath();
        try {
            unzip(tomcatDistribution, tempDir);

            final File ideWar = downloadFile(new File(tempDir + "/webapps"), "app-", ".war", new URL(warUrl));
            ideWar.renameTo(tempDirPath.resolve("webapps/ide.war").toFile());

            final Path catalinaShPath = tempDirPath.resolve("bin/catalina.sh");
            Files.setPosixFilePermissions(catalinaShPath, PosixFilePermissions.fromString("rwxr--r--"));

            final File systemTempDir = new File(System.getProperty("java.io.tmpdir"));
            final File zippedTomcatBundle = systemTempDir.toPath().resolve("codenvy-sdk-tomcat-bundle.zip").toFile();
            zipDir(tempDir.toString(), tempDir, zippedTomcatBundle, ANY_FILTER);
            return zippedTomcatBundle.getAbsolutePath();
        } finally {
            if (tempDir != null && tempDir.exists()) {
                deleteRecursive(tempDir, false);
            }
        }
    }
}
