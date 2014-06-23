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
package com.codenvy.builder.ant;

import com.codenvy.api.builder.BuilderException;
import com.codenvy.api.builder.dto.Dependency;
import com.codenvy.api.builder.internal.BuildListener;
import com.codenvy.api.builder.internal.BuildResult;
import com.codenvy.api.builder.internal.BuildTask;
import com.codenvy.api.builder.internal.Builder;
import com.codenvy.api.builder.internal.BuilderConfiguration;
import com.codenvy.api.builder.internal.BuilderTaskType;
import com.codenvy.api.builder.internal.Constants;
import com.codenvy.api.builder.internal.DependencyCollector;
import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.util.CommandLine;
import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ant.tools.AntBuildListener;
import com.codenvy.ide.ant.tools.AntMessage;
import com.codenvy.ide.ant.tools.AntUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.EOFException;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Builder based on Ant.
 *
 * @author andrew00x
 */
@Singleton
public class AntBuilder extends Builder {
    private static final Logger LOG = LoggerFactory.getLogger(AntBuilder.class);

    private static final String DEPENDENCIES_JSON_FILE = "dependencies.json";
    private static final String DEPENDENCIES_ZIP_FILE  = "dependencies.zip";
    private static final String BUILD_LISTENER_CLASS;
    private static final String BUILD_LISTENER_CLASS_PORT;
    private static final String BUILD_LISTENER_CLASS_PATH;
    private static final String LINE_SEPARATOR                     = System.getProperty("line.separator");
    private static final String CLASSPATH_SEPARATOR                = System.getProperty("path.separator");
    private static final String DEFAULT_JAR_WITH_DEPENDENCIES_NAME = "jar-with-dependencies.zip";

    private static interface AntEventFilter {
        boolean accept(AntEvent event);
    }

    private static final AntEventFilter DEFAULT_ANT_EVENT_FILTER = new AntEventFilter() {
        @Override
        public boolean accept(AntEvent event) {
            return event.isStart() || event.isSuccessful() || event.isError() || event.isClasspath() || event.isPack();
        }
    };

    static {
        final Class<AntBuildListener> myBuildListenerClass = AntBuildListener.class;
        BUILD_LISTENER_CLASS = myBuildListenerClass.getName();
        try {
            BUILD_LISTENER_CLASS_PATH =
                    new java.io.File(myBuildListenerClass.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            // Not expected to be thrown
            throw new IllegalStateException(e);
        }
        BUILD_LISTENER_CLASS_PORT = "-D" + BUILD_LISTENER_CLASS + ".port";
    }

    private final Map<Long, AntMessageServer> antMessageServers;
    private final CustomPortService           portService;

    @Inject
    public AntBuilder(@Named(Constants.BASE_DIRECTORY) java.io.File rootDirectory,
                      @Named(Constants.NUMBER_OF_WORKERS) int numberOfWorkers,
                      @Named(Constants.QUEUE_SIZE) int queueSize,
                      @Named(Constants.KEEP_RESULT_TIME) int cleanupTime,
                      CustomPortService portService,
                      EventService eventService) {
        super(rootDirectory, numberOfWorkers, queueSize, cleanupTime, eventService);
        this.portService = portService;
        antMessageServers = new ConcurrentHashMap<>();
    }

    @Override
    public String getName() {
        return "ant";
    }

    @Override
    public String getDescription() {
        return "Apache Ant based builder implementation";
    }

    @Override
    protected CommandLine createCommandLine(BuilderConfiguration config) {
        final CommandLine commandLine = new CommandLine(AntUtils.getAntExecCommand());
        commandLine.add(config.getTargets());
        switch (config.getTaskType()) {
            case LIST_DEPS:
            case COPY_DEPS:
                commandLine.add("-keep-going"); // Not care about compilation errors, etc. We need classpath only.
                break;
        }
        commandLine.add("-listener", BUILD_LISTENER_CLASS, "-lib", BUILD_LISTENER_CLASS_PATH);
        commandLine.add(config.getOptions());
        return commandLine;
    }

    @Override
    protected BuildResult getTaskResult(FutureBuildTask task, boolean successful) throws BuilderException {
        if (!successful) {
            return new BuildResult(false);
        }

        final AntMessageServer server = antMessageServers.get(task.getId());
        if (server != null) {
            try {
                server.await(5000); // Give some time to the server to receive last messages from the AntBuildListener
            } catch (InterruptedException e) {
                // not expected to be thrown
                LOG.warn(e.getMessage(), e);
            } finally {
                server.stop = true; // force stop if server is not stopped yet
            }
            boolean antSuccessful = false;
            for (AntEvent event : server.receiver.events) {
                if (event.isSuccessful()) {
                    antSuccessful = true;
                    break;
                }
            }

            final BuilderConfiguration config = task.getConfiguration();
            final java.io.File workDir = config.getWorkDir();
            if (config.getTaskType() == BuilderTaskType.DEFAULT) {
                // Need successful status to continue.
                if (!antSuccessful) {
                    return new BuildResult(false);
                }
                final BuildResult result = new BuildResult(true);
                boolean isJar = false;
                for (AntEvent event : server.receiver.events) {
                    if (event.isPack()) {
                        final java.io.File file = event.getPack();
                        if (file.exists()) {
                            isJar = "jar".equals(event.getPacking());
                            result.getResults().add(file);
                        }
                    }
                }
                if (isJar && config.getRequest().isIncludeDependencies()) {
                    result.getResults().clear();
                    // Get all needed dependencies from classpath. Do that only for jar files. We need to have single jar that contains all
                    // dependencies of project otherwise we won't be able to run application on runner side.
                    java.io.File withDependencies = new java.io.File(workDir, DEFAULT_JAR_WITH_DEPENDENCIES_NAME);
                    final FileFilter filter = newSystemFileFilter();
                    final UniqueNameChecker uniqueNameChecker = new UniqueNameChecker();
                    try {
                        FileOutputStream fOut = null;
                        ZipOutputStream zipOut = null;
                        try {
                            fOut = new FileOutputStream(withDependencies);
                            zipOut = new ZipOutputStream(fOut);

                            for (AntEvent event : server.receiver.events) {
                                if (event.isPack()) {
                                    zipOut.putNextEntry(new ZipEntry("application.jar"));
                                    Files.copy(event.getPack().toPath(), zipOut);
                                    zipOut.closeEntry();
                                }
                                if (event.isClasspath()) {
                                    for (java.io.File item : event.getClasspath()) {
                                        if (!item.isFile()) {
                                            continue;
                                        }
                                        if (item.exists() && filter.accept(item)) {
                                            zipOut.putNextEntry(new ZipEntry("lib/" + uniqueNameChecker.maybeAddIndex(item.getName())));
                                            Files.copy(item.toPath(), zipOut);
                                            zipOut.closeEntry();
                                        }
                                    }
                                }
                            }
                        } finally {
                            if (zipOut != null) {
                                zipOut.close();
                            }
                            if (fOut != null) {
                                fOut.close();
                            }
                        }
                    } catch (IOException e) {
                        throw new BuilderException(e);
                    }
                    result.getResults().add(withDependencies);
                }
                return result;
            } else if (config.getTaskType() == BuilderTaskType.LIST_DEPS || config.getTaskType() == BuilderTaskType.COPY_DEPS) {
                // Status may be unsuccessful we are not care about it, we just need to get classpath.
                final BuildResult result = new BuildResult(true);
                final Set<java.io.File> classpath = new LinkedHashSet<>();
                final FileFilter filter = newSystemFileFilter();
                for (AntEvent event : server.receiver.events) {
                    if (event.isClasspath()) {
                        for (java.io.File item : event.getClasspath()) {
                            if (item.exists() && filter.accept(item)) {
                                classpath.add(item);
                            }
                        }
                    }
                }
                if (config.getTaskType() == BuilderTaskType.LIST_DEPS) {
                    try {
                        final java.io.File file = new java.io.File(workDir, DEPENDENCIES_JSON_FILE);
                        writeDependenciesJson(classpath, workDir, file);
                        result.getResults().add(file);
                    } catch (IOException e) {
                        throw new BuilderException(e);
                    }
                } else {
                    try {
                        final java.io.File file = new java.io.File(workDir, DEPENDENCIES_ZIP_FILE);
                        writeDependenciesZip(classpath, file);
                        result.getResults().add(file);
                    } catch (IOException e) {
                        throw new BuilderException(e);
                    }
                }
                return result;
            }
            antMessageServers.remove(task.getId());
        }
        throw new BuilderException("Failed to get build result.");
    }

    @PostConstruct
    @Override
    public void start() {
        super.start();
        addBuildListener(new AntMessageServerStarter());
    }

    @Override
    protected void cleanup(BuildTask task) {
        super.cleanup(task);
        // If nobody asked about build results AntMessageServer may be still in the Map.
        final AntMessageServer server = antMessageServers.remove(task.getId());
        if (server != null) {
            portService.release(server.port);
        }
    }

    private int getPort() {
        final int port = portService.acquire();
        if (port < 0) {
            throw new IllegalStateException("Cannot start build process, there are no free ports. ");
        }
        return port;
    }

    /* Ant may add two tools.jar in classpath. It uses two JavaHome locations. One from java system property and one from OS environment
    variable. Ant sources: org.apache.tools.ant.launch.Locator.getToolsJar */

    private java.io.File getJavaHome() {
        final String javaHomeEnv = System.getenv("JAVA_HOME");
        if (javaHomeEnv == null) {
            return null;
        }
        java.io.File javaHome = new java.io.File(javaHomeEnv);
        return javaHome.exists() ? javaHome : null;
    }

    private java.io.File getJavaHome2() {
        String javaHomeSys = System.getProperty("java.home");
        if (javaHomeSys == null) {
            return null;
        }
        java.io.File javaHome = new java.io.File(javaHomeSys);
        if (!javaHome.exists()) {
            return null;
        }
        final String toolsJar = "lib" + java.io.File.separatorChar + "tools.jar";
        if (new java.io.File(javaHome, toolsJar).exists()) {
            return javaHome;
        }
        if (javaHomeSys.endsWith("jre")) {
            javaHomeSys = javaHomeSys.substring(0, javaHomeSys.length() - 4); // remove "/jre"
        }
        javaHome = new java.io.File(javaHomeSys);
        if (!javaHome.exists()) {
            return null;
        }
        if (new java.io.File(javaHome, toolsJar).exists()) {
            return javaHome;
        }
        return null;
    }

    /* ~ */

    private FileFilter newSystemFileFilter() {
        final java.io.File antHome = AntUtils.getAntHome();
        final java.io.File javaHome = getJavaHome();
        final java.io.File javaHome2 = getJavaHome2();
        final Path antHomePath = antHome == null ? null : antHome.toPath();
        final Path javaHomePath = javaHome == null ? null : javaHome.toPath();
        final Path javaHomePath2 = javaHome2 == null ? null : javaHome2.toPath();
        final Path myToolsPath = new java.io.File(BUILD_LISTENER_CLASS_PATH).toPath();
        return new FileFilter() {
            @Override
            public boolean accept(java.io.File file) {
                final Path path = file.toPath();
                // Skip ant and system jars
                return !(javaHomePath != null && path.startsWith(javaHomePath)
                         || javaHomePath2 != null && path.startsWith(javaHomePath2)
                         || antHomePath != null && path.startsWith(antHomePath)
                         || path.equals(myToolsPath));
            }
        };
    }

    private void writeDependenciesJson(Set<java.io.File> classpath, java.io.File workDir, java.io.File jsonFile) throws IOException {
        final Path workDirPath = workDir.toPath();
        final DependencyCollector collector = new DependencyCollector();
        final UniqueNameChecker uniqueNameChecker = new UniqueNameChecker();
        for (java.io.File file : classpath) {
            final Path path = file.toPath();
            if (path.startsWith(workDirPath)) {
                // If library included in project show relative path to it.
                collector.addDependency(
                        DtoFactory.getInstance().createDto(Dependency.class).withFullName(workDirPath.relativize(path).toString()));
            } else {
                // otherwise show just name of library.
                // Typically it may means that dependency is obtained with some dependency manager,
                // e.g. with builder over builder-ant-task.
                collector.addDependency(
                        DtoFactory.getInstance().createDto(Dependency.class).withFullName(uniqueNameChecker.maybeAddIndex(file.getName())));
            }
        }
        collector.writeJson(jsonFile);
    }

    private void writeDependenciesZip(Set<java.io.File> classpath, java.io.File zipFile) throws IOException {
        final UniqueNameChecker uniqueNameChecker = new UniqueNameChecker();
        FileOutputStream fOut = null;
        ZipOutputStream zipOut = null;
        try {
            fOut = new FileOutputStream(zipFile);
            zipOut = new ZipOutputStream(fOut);
            for (java.io.File file : classpath) {
                if (!file.isFile()) {
                    continue; // Skip directory with compiled sources of this project
                }
                zipOut.putNextEntry(new ZipEntry(uniqueNameChecker.maybeAddIndex(file.getName())));
                Files.copy(file.toPath(), zipOut);
                zipOut.closeEntry();
            }
        } finally {
            if (zipOut != null) {
                zipOut.close();
            }
            if (fOut != null) {
                fOut.close();
            }
        }
    }

    private class AntMessageServerStarter implements BuildListener {
        @Override
        public void begin(BuildTask task) {
            final int myPort = getPort();
            antMessageServers.put(task.getId(), new AntMessageServer(myPort, new AntMessageReceiver(DEFAULT_ANT_EVENT_FILTER)).start());
            task.getCommandLine().addPair(BUILD_LISTENER_CLASS_PORT, String.valueOf(myPort));
        }

        @Override
        public void end(BuildTask task) {
            final AntMessageServer server = antMessageServers.get(task.getId());
            if (server != null) {
                server.stop = true; // force stop if server is not stopped yet
                portService.release(server.port);
            }
        }
    }

    private static class UniqueNameChecker {
        final Map<String, Integer> indexes;

        UniqueNameChecker() {
            indexes = new HashMap<>();
        }

        String maybeAddIndex(String str) {
            Integer index = indexes.get(str);
            if (index == null) {
                indexes.put(str, 1);
                return str;
            }
            indexes.put(str, index + 1);
            str = str + '(' + index + ')';
            return str;
        }
    }

    private static class AntMessageReceiver {
        final List<AntEvent> events;
        final AntEventFilter filter;

        AntMessageReceiver(AntEventFilter filter) {
            this.filter = filter;
            events = new ArrayList<>();
        }

        void receive(AntMessage message) {
            final AntEvent event = new AntEvent(message);
            if (filter.accept(event)) {
                events.add(event);
            }
        }
    }

    private static class AntEvent {
        final AntMessage message;

        java.io.File[] classpath;
        java.io.File   pack;
        String         packaging;

        AntEvent(AntMessage message) {
            this.message = message;
            final String antTask = message.getTask();
            final String text = message.getText();
            if (message.getType() == AntMessage.BUILD_LOG) {
                if ("javac".equals(antTask) && text != null && text.startsWith("Compilation argument")) {
                    classpath = parseClasspath(text);
                } else if ("jar".equals(antTask) || "war".equals(antTask) || "ear".equals(antTask) || "zip".equals(antTask)) {
                    packaging = antTask;
                    // Ant send messages in format: Building jar|war|ear|zip: <absolute path to file>. Try to get this path.
                    if (text != null && (text.startsWith("Building jar: ") || text.startsWith("Building war: ")
                                         || text.startsWith("Building ear: ") || text.startsWith("Building zip: "))) {
                        pack = new java.io.File(text.substring(14));
                    }
                }
            }
        }

        boolean isClasspath() {
            return classpath != null;
        }

        boolean isPack() {
            return pack != null;
        }

        boolean isStart() {
            return message.getType() == AntMessage.BUILD_STARTED;
        }

        boolean isSuccessful() {
            return message.getType() == AntMessage.BUILD_SUCCESSFUL;
        }

        boolean isError() {
            return message.getType() == AntMessage.BUILD_ERROR;
        }

        // Get jar|war|ear|zip file if this event related to pack ant-task
        java.io.File getPack() {
            return pack;
        }

        String getPacking() {
            return packaging;
        }

        // Get classpath if this event related to compile ant-task
        java.io.File[] getClasspath() {
            return classpath;
        }

        String getText() {
            return message.getText();
        }
    }

    private static java.io.File[] parseClasspath(String cmd) {
        final Scanner s1 = new Scanner(cmd);
        s1.useDelimiter(LINE_SEPARATOR);
        while (s1.hasNext()) {
            if ("'-classpath'".equals(s1.next())) {
                if (s1.hasNext()) {
                    String str = s1.next();
                    if (!str.startsWith("-")) {
                        List<java.io.File> classpath = new ArrayList<>();
                        final Scanner s2 = new Scanner(removeQuote(str));
                        s2.useDelimiter(CLASSPATH_SEPARATOR);
                        while (s2.hasNext()) {
                            String str2 = s2.next();
                            java.io.File file = new java.io.File(str2);
                            if (file.exists()) {
                                classpath.add(file);
                            }
                        }
                        s2.close();
                        return classpath.toArray(new java.io.File[classpath.size()]);
                    }
                }
                break;
            }
        }
        s1.close();
        return new java.io.File[0];
    }

    private static String removeQuote(String str) {
        if (str.charAt(0) == '\'') {
            str = str.substring(1);
        }
        final int len = str.length();
        if (str.charAt(len - 1) == '\'') {
            str = str.substring(0, len - 1);
        }
        return str;
    }

    private class AntMessageServer {
        final AntMessageReceiver receiver;
        final int                port;
        final CountDownLatch     latch;

        volatile boolean stop;

        AntMessageServer(int port, AntMessageReceiver receiver) {
            this.port = port;
            this.receiver = receiver;
            latch = new CountDownLatch(1);
        }

        AntMessageServer start() {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        if (stop) {
                            return; // If stop requested don't do anything
                        }
                        ServerSocket serverSocket = null;
                        Socket mySocket = null;
                        ObjectInputStream in = null;
                        try {
                            serverSocket = new ServerSocket(port);
                            serverSocket.setSoTimeout(10000);
                            mySocket = serverSocket.accept();
                            in = new ObjectInputStream(mySocket.getInputStream());
                            while (!stop) {
                                AntMessage message;
                                try {
                                    message = (AntMessage)in.readObject();
                                } catch (EOFException e) {
                                    message = null;
                                }
                                if (message == null) {
                                    stop = true;
                                } else {
                                    receiver.receive(message);
                                }
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            throw new IllegalStateException(e);
                        } finally {
                            if (in != null) {
                                try {
                                    in.close();
                                } catch (IOException ignored) {
                                }
                            }
                            if (mySocket != null) {
                                try {
                                    mySocket.close();
                                } catch (IOException ignored) {
                                }
                            }
                            if (serverSocket != null) {
                                try {
                                    serverSocket.close();
                                } catch (IOException ignored) {
                                }
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
            return this;
        }

        void await(long time) throws InterruptedException {
            latch.await(time, TimeUnit.MILLISECONDS);
        }
    }
}
