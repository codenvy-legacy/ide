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
package com.codenvy.builder.maven;

import com.codenvy.api.builder.dto.Dependency;
import com.codenvy.api.builder.internal.BuildLogger;
import com.codenvy.api.builder.internal.BuildResult;
import com.codenvy.api.builder.internal.Builder;
import com.codenvy.api.builder.internal.BuilderConfiguration;
import com.codenvy.api.builder.internal.BuilderException;
import com.codenvy.api.builder.internal.DelegateBuildLogger;
import com.codenvy.api.builder.internal.DependencyCollector;
import com.codenvy.api.core.util.CommandLine;
import com.codenvy.builder.maven.dto.MavenDependency;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.maven.tools.MavenUtils;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builder based on Maven.
 *
 * @author andrew00x
 * @author Eugene Voevodin
 */
@Singleton
public class MavenBuilder extends Builder {
    private static final Logger LOG = LoggerFactory.getLogger(MavenBuilder.class);

    /** Rules for builder assembly plugin. Use it for create jar with included dependencies */
    private static final String ASSEMBLY_DESCRIPTOR_FOR_JAR_WITH_DEPENDENCIES      = "<assembly>\n" +
                                                                                     "  <id>jar-with-dependencies</id>\n" +
                                                                                     "  <formats>\n" +
                                                                                     "    <format>jar</format>\n" +
                                                                                     "  </formats>\n" +
                                                                                     "  <includeBaseDirectory>true</includeBaseDirectory>\n" +
                                                                                     "  <dependencySets>\n" +
                                                                                     "    <dependencySet>\n" +
                                                                                     "      <outputDirectory>/</outputDirectory>\n" +
                                                                                     "      <unpack>true</unpack>\n" +
                                                                                     "      <scope>runtime</scope>\n" +
                                                                                     "    </dependencySet>\n" +
                                                                                     "  </dependencySets>\n" +
                                                                                     "</assembly>\n";
    private static final String ASSEMBLY_DESCRIPTOR_FOR_JAR_WITH_DEPENDENCIES_FILE = "jar-with-dependencies-assembly-descriptor.xml";

    /** Rules for builder assembly plugin. Use it for create zip of all project dependencies. */
    private static final String assemblyDescriptor       = "<assembly>\n" +
                                                           "  <id>dependencies</id>\n" +
                                                           "  <formats>\n" +
                                                           "    <format>zip</format>\n" +
                                                           "  </formats>\n" +
                                                           "  <includeBaseDirectory>false</includeBaseDirectory>\n" +
                                                           "  <fileSets>\n" +
                                                           "    <fileSet>\n" +
                                                           "      <directory>target/dependency</directory>\n" +
                                                           "      <outputDirectory>/</outputDirectory>\n" +
                                                           "    </fileSet>\n" +
                                                           "  </fileSets>\n" +
                                                           "</assembly>";
    private static final String DEPENDENCIES_JSON_FILE   = "dependencies.json";
    private static final String ASSEMBLY_DESCRIPTOR_FILE = "dependencies-zip-assembly-descriptor.xml";

    @Inject
    public MavenBuilder(@Named(REPOSITORY) java.io.File rootDirectory,
                        @Named(NUMBER_OF_WORKERS) int numberOfWorkers,
                        @Named(INTERNAL_QUEUE_SIZE) int queueSize,
                        @Named(CLEAN_RESULT_DELAY_TIME) int cleanBuildResultDelay) {
        super(rootDirectory, numberOfWorkers, queueSize, cleanBuildResultDelay);
    }

    @Override
    public String getName() {
        return "maven";
    }

    @Override
    public String getDescription() {
        return "Apache Maven based builder implementation";
    }

    @Override
    protected CommandLine createCommandLine(BuilderConfiguration config) throws BuilderException {
        final CommandLine commandLine = new CommandLine(mavenExecCommand());
        final List<String> targets = config.getTargets();
        switch (config.getTaskType()) {
            case DEFAULT:
                if (!targets.isEmpty()) {
                    commandLine.add(targets);
                } else {
                    commandLine.add("clean", "package");
                }
                if (config.getRequest().isDeployJarWithDependencies()) {
                    try {
                        Files.write(new java.io.File(config.getWorkDir(), ASSEMBLY_DESCRIPTOR_FOR_JAR_WITH_DEPENDENCIES_FILE).toPath(),
                                    ASSEMBLY_DESCRIPTOR_FOR_JAR_WITH_DEPENDENCIES.getBytes());
                    } catch (IOException e) {
                        throw new BuilderException(e);
                    }
                    commandLine.add("assembly:single");
                    commandLine.addPair("-Ddescriptor", ASSEMBLY_DESCRIPTOR_FOR_JAR_WITH_DEPENDENCIES_FILE);
                }
                break;
            case LIST_DEPS:
                if (!targets.isEmpty()) {
                    LOG.warn("Targets {} ignored when list dependencies", targets);
                }
                commandLine.add("clean", "dependency:list");
                break;
            case COPY_DEPS:
                if (!targets.isEmpty()) {
                    LOG.warn("Targets {} ignored when copy dependencies", targets);
                }
                // Prepare file for assembly plugin. Plugin create zip archive of all dependencies.
                try {
                    Files.write(new java.io.File(config.getWorkDir(), ASSEMBLY_DESCRIPTOR_FILE).toPath(),
                                assemblyDescriptor.getBytes());
                } catch (IOException e) {
                    throw new BuilderException(e);
                }
                commandLine.add("clean", "dependency:copy-dependencies", "assembly:single");
                commandLine.addPair("-Ddescriptor", ASSEMBLY_DESCRIPTOR_FILE);
                commandLine.addPair("-Dmdep.failOnMissingClassifierArtifact", "false");
                break;
        }
        commandLine.add(config.getOptions());
        return commandLine;
    }

    @Override
    protected BuildResult getTaskResult(FutureBuildTask task, boolean successful) throws BuilderException {
        if (!successful) {
            return new BuildResult(false, getBuildReport(task));
        }

        boolean mavenSuccess = false;
        BufferedReader logReader = null;
        try {
            logReader = new BufferedReader(task.getBuildLogger().getReader());
            String line;
            while ((line = logReader.readLine()) != null) {
                if ("BUILD SUCCESS".equals(removeLoggerPrefix(line))) {
                    mavenSuccess = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new BuilderException(e);
        } finally {
            if (logReader != null) {
                try {
                    logReader.close();
                } catch (IOException ignored) {
                }
            }
        }

        if (!mavenSuccess) {
            return new BuildResult(false, getBuildReport(task));
        }

        final BuilderConfiguration config = task.getConfiguration();
        final java.io.File workDir = config.getWorkDir();
        final BuildResult result = new BuildResult(true, getBuildReport(task));
        java.io.File[] files = null;
        switch (config.getTaskType()) {
            case DEFAULT:
                final Model mavenModel;
                try {
                    mavenModel = MavenUtils.getModel(workDir);
                } catch (IOException e) {
                    throw new BuilderException(e);
                }
                final String packaging = mavenModel.getPackaging();
                final String fileExt = packaging != null ? '.' + packaging : ".jar";
                files = new java.io.File(workDir, "target").listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(java.io.File dir, String name) {
                        return !name.endsWith("-sources.jar") && name.endsWith(fileExt);
                    }
                });
                break;
            case LIST_DEPS:
                files = workDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(java.io.File dir, String name) {
                        return name.equals(DEPENDENCIES_JSON_FILE);
                    }
                });
                break;
            case COPY_DEPS:
                files = new java.io.File(workDir, "target").listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(java.io.File dir, String name) {
                        return name.endsWith("-dependencies.zip");
                    }
                });
                break;
        }

        if (files != null && files.length > 0) {
            for (java.io.File file : files) {
                result.getResults().add(file);
            }
        }

        return result;
    }

    /**
     * Get build report. By default show link to the surefire reports.
     *
     * @param task
     *         task
     * @return report or {@code null} if surefire reports is not available
     */
    protected java.io.File getBuildReport(FutureBuildTask task) {
        final java.io.File dir = task.getConfiguration().getWorkDir();
        final String reports = "target" + java.io.File.separatorChar + "surefire-reports";
        final java.io.File reportsDir = new java.io.File(dir, reports);
        return reportsDir.exists() ? reportsDir : null;
    }

    @Override
    protected BuildLogger createBuildLogger(BuilderConfiguration configuration, java.io.File logFile) throws BuilderException {
        return new DependencyBuildLogger(super.createBuildLogger(configuration, logFile),
                                         new java.io.File(configuration.getWorkDir(), DEPENDENCIES_JSON_FILE));
    }

    private static final Pattern LOGGER_PREFIX_REMOVER = Pattern.compile("(\\[INFO\\]|\\[WARNING\\]|\\[DEBUG\\])\\s+(.*)");

    private static String removeLoggerPrefix(String origin) {
        final Matcher matcher = LOGGER_PREFIX_REMOVER.matcher(origin);
        if (matcher.matches()) {
            return origin.substring(matcher.start(2));
        }
        return origin;
    }

    private static class DependencyBuildLogger extends DelegateBuildLogger {
        final java.io.File jsonFile;

        boolean             dependencyStarted;
        DependencyCollector collector;

        DependencyBuildLogger(BuildLogger buildLogger, java.io.File jsonFile) {
            super(buildLogger);
            this.jsonFile = jsonFile;
        }

        @Override
        public void writeLine(String line) throws IOException {
            if (line != null) {
                final String trimmed = removeLoggerPrefix(line);
                if (dependencyStarted) {
                    if (trimmed.isEmpty()) {
                        dependencyStarted = false;
                        collector.writeJson(jsonFile);
                    } else {
                        final String[] segments = trimmed.split(":");
                        if (segments.length >= 5) {
                            final String groupId = segments[0];
                            final String artifactId = segments[1];
                            final String type = segments[2];
                            final String classifier;
                            final String version;
                            final String scope;
                            if (segments.length == 5) {
                                version = segments[3];
                                classifier = null;
                                scope = segments[4];
                            } else {
                                version = segments[4];
                                classifier = segments[3];
                                scope = segments[5];
                            }
                            final Dependency dep = DtoFactory.getInstance().createDto(MavenDependency.class)
                                                             .withGroupID(groupId)
                                                             .withArtifactID(artifactId)
                                                             .withType(type)
                                                             .withVersion(version)
                                                             .withClassifier(classifier)
                                                             .withScope(scope)
                                                             .withFullName(groupId + ':' + artifactId + ':' + version + ':' + type);
                            collector.addDependency(dep);
                        }
                    }
                } else if ("The following files have been resolved:".equals(trimmed)) {
                    dependencyStarted = true;
                    collector = new DependencyCollector();
                }
            }

            super.writeLine(line);
        }
    }
}
