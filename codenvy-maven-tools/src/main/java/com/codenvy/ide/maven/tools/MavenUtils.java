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
package com.codenvy.ide.maven.tools;

import com.codenvy.api.core.ForbiddenException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.vfs.server.VirtualFile;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Resource;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A smattering of useful methods to work with the Maven POM.
 *
 * @author Artem Zatsarynnyy
 * @author andrew00x
 */
public class MavenUtils {
    /** Internal Maven POM reader. */
    private static MavenXpp3Reader pomReader = new MavenXpp3Reader();
    /** Internal Maven POM writer. */
    private static MavenXpp3Writer pomWriter = new MavenXpp3Writer();

    /** Not instantiable. */
    private MavenUtils() {
    }

    /**
     * Get description of maven project.
     *
     * @param sources
     *         maven project directory. Note: Must contains pom.xml file.
     * @return description of maven project
     * @throws IOException
     *         if an i/o error occurs
     */
    public static Model getModel(java.io.File sources) throws IOException {
        return doReadModel(new java.io.File(sources, "pom.xml"));
    }

    /**
     * Read description of maven project.
     *
     * @param pom
     *         path to pom.xml file
     * @return description of maven project
     * @throws IOException
     *         if an i/o error occurs
     */
    public static Model readModel(java.io.File pom) throws IOException {
        return doReadModel(pom);
    }

    /**
     * Read description of maven project.
     *
     * @param reader
     *         {@link Reader} to read content of pom.xml file.
     * @return description of maven project
     * @throws IOException
     *         if an i/o error occurs
     */
    public static Model readModel(Reader reader) throws IOException {
        try {
            return pomReader.read(reader, true);
        } catch (XmlPullParserException e) {
            throw new IOException(e);
        }
    }

    /**
     * Read description of maven project.
     *
     * @param stream
     *         {@link InputStream} to read content of pom.xml file.
     * @return description of maven project
     * @throws IOException
     *         if an i/o error occurs
     */
    public static Model readModel(InputStream stream) throws IOException {
        try {
            return pomReader.read(stream, true);
        } catch (XmlPullParserException e) {
            throw new IOException(e);
        }
    }

    /**
     * Read description of maven project.
     *
     * @param pom
     *         {@link VirtualFile} to read content of pom.xml file.
     * @return description of maven project
     * @throws IOException
     *         if an i/o error occurs
     * @throws ForbiddenException
     *         if {@code pom} isn't a file
     * @throws ServerException
     *         if other error occurs
     */
    public static Model readModel(VirtualFile pom) throws IOException, ForbiddenException, ServerException {
        try (InputStream stream = pom.getContent().getStream()) {
            return pomReader.read(stream, true);
        } catch (XmlPullParserException e) {
            throw new IOException(e);
        }
    }

    /**
     * Writes a specified {@link Model} to the path from which this model has been read.
     *
     * @param model
     *         model to write
     * @throws IOException
     *         if an i/o error occurs
     * @throws IllegalStateException
     *         if method {@code model.getPomFile()} returns {@code null}
     */
    public static void writeModel(Model model) throws IOException {
        final java.io.File pom = model.getPomFile();
        if (pom == null) {
            throw new IllegalStateException("Unable to write a model. Unknown path.");
        }
        writeModel(model, pom);
    }

    /**
     * Writes a specified {@link Model} to the specified {@link java.io.File}.
     *
     * @param model
     *         model to write
     * @param pom
     *         path to the file to write a model
     * @throws IOException
     *         if an i/o error occurs
     */
    public static void writeModel(Model model, java.io.File pom) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(pom.toPath(), Charset.forName("UTF-8"))) {
            pomWriter.write(writer, model);
        }
    }

    /**
     * Writes a specified {@link Model} to the specified {@link OutputStream}.
     *
     * @param model
     *         model to write
     * @param output
     *         {@link OutputStream} to write a model
     * @throws IOException
     *         if an i/o error occurs
     */
    public static void writeModel(Model model, OutputStream output) throws IOException {
        pomWriter.write(output, model);
    }

    /**
     * Writes a specified {@link Model} to the specified {@link Writer}.
     *
     * @param model
     *         model to write
     * @param output
     *         {@link Writer} to write a model
     * @throws IOException
     *         if an i/o error occurs
     */
    public static void writeModel(Model model, Writer output) throws IOException {
        pomWriter.write(output, model);
    }

    /**
     * Writes a specified {@link Model} to the specified {@link VirtualFile}.
     *
     * @param model
     *         model to write
     * @param output
     *         {@link VirtualFile} to write a model
     * @throws IOException
     *         if an i/o error occurs
     * @throws ForbiddenException
     *         if {@code pom} isn't a file
     * @throws ServerException
     *         if other error occurs
     */
    public static void writeModel(Model model, VirtualFile output) throws IOException, ForbiddenException, ServerException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        pomWriter.write(bout, model);
        output.updateContent(output.getMediaType(), new ByteArrayInputStream(bout.toByteArray()), null);
    }

    /**
     * Add dependency to the specified pom.xml.
     *
     * @param pom
     *         pom.xml path
     * @param dependency
     *         POM of artifact to add as dependency
     * @throws IOException
     *         if an i/o error occurs
     */
    public static void addDependency(java.io.File pom, Model dependency) throws IOException {
        addDependency(pom, dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), null);
    }

    /**
     * Add dependency to the specified pom.xml.
     *
     * @param pom
     *         pom.xml path
     * @param dependency
     *         POM of artifact to add as dependency
     * @throws IOException
     *         if an i/o error occurs
     */
    public static void addDependency(java.io.File pom, Dependency dependency) throws IOException {
        final Model model = doReadModel(pom);
        model.getDependencies().add(dependency);
        try (BufferedWriter writer = Files.newBufferedWriter(pom.toPath(), Charset.forName("UTF-8"))) {
            pomWriter.write(writer, model);
        }
    }

    /**
     * Add dependency to the specified pom.xml.
     *
     * @param pom
     *         pom.xml file
     * @param dependency
     *         POM of artifact to add as dependency
     * @throws IOException
     *         if an i/o error occurs
     * @throws ForbiddenException
     *         if {@code pom} isn't a file
     * @throws ServerException
     *         if other error occurs
     */
    public static void addDependency(VirtualFile pom, Dependency dependency) throws IOException, ForbiddenException, ServerException {
        final Model model = readModel(pom);
        model.getDependencies().add(dependency);
        writeModel(model, pom);
    }

    /**
     * Add set of dependencies to the specified pom.xml.
     *
     * @param pom
     *         pom.xml path
     * @param dependencies
     *         POM of artifact to add as dependency
     * @throws IOException
     *         if an i/o error occurs
     */
    public static void addDependencies(java.io.File pom, Dependency... dependencies) throws IOException {
        final Model model = doReadModel(pom);
        model.getDependencies().addAll(Arrays.asList(dependencies));
        try (BufferedWriter writer = Files.newBufferedWriter(pom.toPath(), Charset.forName("UTF-8"))) {
            pomWriter.write(writer, model);
        }
    }

    /**
     * Add set of dependencies to the specified pom.xml.
     *
     * @param pom
     *         pom.xml file
     * @param dependencies
     *         POM of artifact to add as dependency
     * @throws IOException
     *         if an i/o error occurs
     * @throws ForbiddenException
     *         if {@code pom} isn't a file
     * @throws ServerException
     *         if other error occurs
     */
    public static void addDependencies(VirtualFile pom, Dependency... dependencies)
            throws IOException, ForbiddenException, ServerException {
        final Model model = readModel(pom);
        model.getDependencies().addAll(Arrays.asList(dependencies));
        writeModel(model, pom);
    }

    /**
     * Add dependency to the specified pom.xml.
     *
     * @param pom
     *         pom.xml path
     * @param groupId
     *         groupId
     * @param artifactId
     *         artifactId
     * @param version
     *         artifact version
     * @throws IOException
     *         if an i/o error occurs
     */
    public static void addDependency(java.io.File pom, String groupId, String artifactId, String version, String scope) throws IOException {
        addDependency(pom, newDependency(groupId, artifactId, version, scope));
    }

    /**
     * Add dependency to the specified pom.xml.
     *
     * @param pom
     *         pom.xml file
     * @param groupId
     *         groupId
     * @param artifactId
     *         artifactId
     * @param version
     *         artifact version
     * @throws IOException
     *         if an i/o error occurs
     * @throws ForbiddenException
     *         if {@code pom} isn't a file
     * @throws ServerException
     *         if other error occurs
     */
    public static void addDependency(VirtualFile pom, String groupId, String artifactId, String version, String scope)
            throws IOException, ForbiddenException, ServerException {
        addDependency(pom, newDependency(groupId, artifactId, version, scope));
    }

    /**
     * Returns an execution command to launch Maven. If Maven home
     * environment variable isn't set then 'mvn' will be returned
     * since it's assumed that 'mvn' should be in PATH variable.
     *
     * @return an execution command to launch Maven
     */
    public static String getMavenExecCommand() {
        final java.io.File mvnHome = getMavenHome();
        if (mvnHome != null) {
            final String mvn = "bin" + java.io.File.separatorChar + "mvn";
            return new java.io.File(mvnHome, mvn).getAbsolutePath(); // use Maven home directory if it's set
        } else {
            return "mvn"; // otherwise 'mvn' should be in PATH variable
        }
    }

    /**
     * Returns Maven home directory.
     *
     * @return Maven home directory
     */
    public static java.io.File getMavenHome() {
        final String m2HomeEnv = System.getenv("M2_HOME");
        if (m2HomeEnv == null) {
            return null;
        }
        final java.io.File m2Home = new java.io.File(m2HomeEnv);
        return m2Home.exists() ? m2Home : null;
    }

    /** Get groupId of artifact. If artifact doesn't have groupId this method checks parent artifact for groupId. */
    public static String getGroupId(Model model) {
        String groupId = model.getGroupId();
        if (groupId == null) {
            final Parent parent = model.getParent();
            if (parent != null) {
                groupId = parent.getGroupId();
            }
        }
        return groupId;
    }

    /** Get version of artifact. If artifact doesn't have version this method checks parent artifact for version. */
    public static String getVersion(Model model) {
        String version = model.getVersion();
        if (version == null) {
            final Parent parent = model.getParent();
            if (parent != null) {
                version = parent.getVersion();
            }
        }
        return version;
    }

    /** Get source directories. */
    public static List<String> getSourceDirectories(Model model) {
        List<String> list = new LinkedList<>();
        Build build = model.getBuild();
        if (build != null) {
            if (build.getSourceDirectory() != null) {
                list.add(build.getSourceDirectory());
            } else if (build.getTestSourceDirectory() != null) {
                list.add(build.getTestSourceDirectory());
            }
        }
        if (list.isEmpty()) {
            list.add("src/main/java");
            list.add("src/test/java");
        }
        return list;
    }

    /** Get source directories. */
    public static List<String> getSourceDirectories(VirtualFile pom) throws ServerException, IOException, ForbiddenException {
        return getSourceDirectories(readModel(pom));
    }

    /** Get source directories. */
    public static List<String> getSourceDirectories(java.io.File pom) throws IOException {
        return getSourceDirectories(readModel(pom));
    }

    /** Get resource directories. */
    public static List<String> getResourceDirectories(Model model) {
        List<String> list = new LinkedList<>();
        Build build = model.getBuild();

        if (build != null) {
            if (build.getResources() != null && !build.getResources().isEmpty()) {
                for (Resource resource : build.getResources())
                    list.add(resource.getDirectory());
            }
        }
        if (list.isEmpty()) {
            list.add("src/main/resources");
            list.add("src/test/resources");
        }
        return list;
    }

    /** Get resource directories. */
    public static List<String> getResourceDirectories(VirtualFile pom) throws ServerException, IOException, ForbiddenException {
        return getResourceDirectories(readModel(pom));
    }

    /** Get resource directories. */
    public static List<String> getResourceDirectories(java.io.File pom) throws IOException {
        return getResourceDirectories(readModel(pom));
    }

    /** Creates new {@link Dependency} instance. */
    public static Dependency newDependency(String groupId, String artifactId, String version, String scope) {
        final Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        dependency.setScope(scope);
        return dependency;
    }

    /** Creates new {@link Model} instance. */
    public static Model newModel(Parent parent, String groupId, String artifactId, String version, String packaging) {
        final Model model = new Model();
        model.setParent(parent);
        model.setGroupId(groupId);
        model.setArtifactId(artifactId);
        model.setVersion(version);
        model.setPackaging(packaging);
        return model;
    }

    /** Creates new {@link Parent} instance. */
    public static Parent newParent(String groupId, String artifactId, String version) {
        final Parent parent = new Parent();
        parent.setGroupId(groupId);
        parent.setArtifactId(artifactId);
        parent.setVersion(version);
        return parent;
    }

    private static Model doReadModel(java.io.File pom) throws IOException {
        final Model model;
        try (Reader reader = Files.newBufferedReader(pom.toPath(), Charset.forName("UTF-8"))) {
            model = pomReader.read(reader, true);
        } catch (XmlPullParserException e) {
            throw new IOException(e);
        }
        model.setPomFile(pom);
        return model;
    }
}
