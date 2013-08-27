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
package com.codenvy.ide.ext.extruntime.server;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;
import static org.codehaus.plexus.util.xml.Xpp3DomBuilder.build;

/**
 * A collection of utility methods that simplify editing of Maven POM and GWT module descriptor (gwt.xml) files.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: Utils.java Jul 31, 2013 11:30:14 AM azatsarynnyy $
 */
public class Utils {
    /** Maven POM reader. */
    private static MavenXpp3Reader pomReader = new MavenXpp3Reader();
    /** Maven POM writer. */
    private static MavenXpp3Writer pomWriter = new MavenXpp3Writer();

    /**
     * Read pom.xml.
     * 
     * @param path pom.xml path
     * @return a project object model
     * @throws IOException error occurred while reading content of file
     */
    public static Model readPom(Path path) throws IOException {
        return readPom(Files.newInputStream(path));
    }

    /**
     * Read pom.xml.
     * 
     * @param stream input stream that represents a pom.xml
     * @return a project object model
     * @throws IOException error occurred while reading content of file
     */
    static Model readPom(InputStream stream) throws IOException {
        try {
            return pomReader.read(stream, true);
        } catch (XmlPullParserException e) {
            throw new IllegalStateException("Error occurred while parsing pom.xml :" + e.getMessage(), e);
        }
    }

    /**
     * Write provided project object model to the specified path.
     * 
     * @param pom a project object model
     * @param path path to pom.xml
     * @throws IOException error occurred while writing content of file
     */
    public static void writePom(Model pom, Path path) throws IOException {
        pomWriter.write(Files.newOutputStream(path), pom);
    }

    /**
     * Add dependency to the specified pom.xml.
     * 
     * @param path pom.xml path
     * @param pom POM of artifact to add as dependency
     * @throws IOException error occurred while reading or writing content of file
     */
    static void addDependencyToPom(Path path, Model pom) throws IOException {
        addDependencyToPom(path, pom.getGroupId(), pom.getArtifactId(), pom.getVersion());
    }

    /**
     * Add dependency to the specified pom.xml.
     * 
     * @param path pom.xml path
     * @param groupId groupId
     * @param artifactId artifactId
     * @param version artifact version
     * @throws IOException error occurred while reading or writing content of file
     */
    static void addDependencyToPom(Path path, String groupId, String artifactId, String version) throws IOException {
        Dependency dep = new Dependency();
        dep.setGroupId(groupId);
        dep.setArtifactId(artifactId);
        dep.setVersion(version);

        Model pom = readPom(path);
        pom.getDependencies().add(dep);

        writePom(pom, path);
    }

    /**
     * Add the provided module to the specified reactor pom.xml.
     * 
     * @param path pom.xml path
     * @param moduleRelativePath relative path of module to add
     * @throws IOException error occurred while reading or writing content of file
     */
    static void addModuleToReactorPom(Path path, String moduleRelativePath) throws IOException {
        addModuleToReactorPom(path, moduleRelativePath, null);
    }

    /**
     * Add the provided module to the specified reactor pom.xml. If <code>moduleAfter</code> isn't null - new module will be inserted before
     * the <code>moduleAfter</code>.
     * 
     * @param path pom.xml path
     * @param moduleRelativePath relative path of module to add
     * @param moduleAfter relative path of module that should be after the inserted module
     * @throws IOException error occurred while reading or writing content of file
     */
    static void addModuleToReactorPom(Path path, String moduleRelativePath, String moduleAfter) throws IOException {
        Model pom = readPom(path);
        List<String> modulesList = pom.getModules();
        if (moduleAfter == null) {
            modulesList.add(moduleRelativePath);
        } else {
            int n = 0;
            for (String module : modulesList) {
                if (moduleAfter.equals(module)) {
                    pom.getModules().add(n, moduleRelativePath);
                    break;
                }
                n++;
            }
        }
        writePom(pom, path);
    }

    /**
     * Enable SuperDevMode for the specified GWT module descriptor.
     * 
     * @param path GWT module descriptor path
     * @throws IOException error occurred while reading or writing content of file
     */
    static void enableSuperDevMode(Path path) throws IOException {
        // Directive for GWT-module descriptor to enable GWT SuperDevMode.
        final String superDevModeDirective = "    <add-linker name='xsiframe'/>\r\n" +
                                             "    <set-configuration-property name='devModeRedirectEnabled' value='true'/>\r\n" +
                                             "    <set-property name='compiler.useSourceMaps' value='true'/>\r\n";

        List<String> content = Files.readAllLines(path, UTF_8);
        int penultimateLine = 0;
        for (String str : content) {
            penultimateLine++;
            if (str.contains("</module>")) {
                break;
            }
        }
        content.add(penultimateLine - 1, superDevModeDirective);

        Files.write(path, content, UTF_8);
    }

    /**
     * Add the specified module name as a dependency to the provided GWT module descriptor.
     * 
     * @param path GWT module descriptor
     * @param inheritableModuleLogicalName logical name of the GWT module to inherit
     * @throws IOException error occurred while reading or writing content of file
     */
    static void inheritGwtModule(Path path, String inheritableModuleLogicalName) throws IOException {
        final String inheritsString = "    <inherits name='" + inheritableModuleLogicalName + "'/>";
        List<String> content = Files.readAllLines(path, UTF_8);
        // insert custom module as last 'inherits' entry
        int i = 0, lastInheritsLine = 0;
        for (String str : content) {
            i++;
            if (str.contains("<inherits")) {
                lastInheritsLine = i;
            }
        }
        content.add(lastInheritsLine, inheritsString);

        Files.write(path, content, UTF_8);
    }

    static String detectGwtModuleLogicalName(Path folder) throws IOException {
        final String fileExtension = ".gwt.xml";
        final String resourcesDir = "/src/main/resources";

        Finder finder = new Finder("*" + fileExtension);
        Files.walkFileTree(folder, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, finder);
        if (finder.getFirstMatchedFile() == null) {
            throw new IllegalArgumentException("GWT module descriptor (gwt.xml) not found.");
        }

        String filePath = finder.getFirstMatchedFile().toString();
        filePath = filePath.substring(filePath.indexOf(resourcesDir) + resourcesDir.length() + 1,
                                      filePath.length() - fileExtension.length());
        return filePath.replaceAll("/", ".");
    }

    /**
     * It's a workaround for known bug in GWT Maven plug-in. See the https://jira.codehaus.org/browse/MGWT-332 for details.
     * 
     * @throws IOException error occurred while reading or writing content of file
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static void fixMGWT332Bug(Path pomPath, String extensionModuleName, String profileId) throws ExtensionLauncherException, IOException {
        Model pom = readPom(pomPath);
        List<Profile> profiles = pom.getProfiles();
        Profile profile = null;
        for (Profile curProfile : profiles) {
            if (curProfile.getId().equals(profileId)) {
                profile = curProfile;
            }
        }

        if (profile == null) {
            throw new IllegalStateException(String.format("Profile %s not found in %s.", profileId, pomPath));
        }

        Map<String, Plugin> plugins = profile.getBuild().getPluginsAsMap();
        Plugin buildHelperPlugin = plugins.get("org.codehaus.mojo:build-helper-maven-plugin");
        PluginExecution execution = buildHelperPlugin.getExecutionsAsMap().get("add-extension-sources");

        final String confString = String.format("<configuration>" +
                                                "  <sources>" +
                                                "    <source>../%1$s/src/main/java</source>" +
                                                "  </sources>" +
                                                "</configuration>", extensionModuleName);

        try {
            Xpp3Dom configuration = build(new StringReader(confString));
            execution.setConfiguration(configuration);
            profile.getBuild().setPlugins(new ArrayList(plugins.values()));

            writePom(pom, pomPath);
        } catch (XmlPullParserException e) {
            throw new IllegalStateException("Error occurred while parsing pom.xml :" + e.getMessage(), e);
        }
    }

    /**
     * Change the default ports of Tomcat server.
     * 
     * @param tomcatRootPath Tomcat root path
     * @param shutdownPort server shutdown port
     * @param httpPort HTTP-connector port
     * @param ajpPort AJP-connector port
     * @throws IllegalStateException if any error occurred while reading or writing a file
     */
    static void configureTomcatPorts(Path tomcatRootPath, int shutdownPort, int httpPort, int ajpPort) {
        File serverXml = tomcatRootPath.resolve("conf/server.xml").toFile();

        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(serverXml);

            Node serverElement = doc.getElementsByTagName("Server").item(0);
            Node serverShutdownPortNode = serverElement.getAttributes().getNamedItem("port");
            serverShutdownPortNode.setNodeValue(String.valueOf(shutdownPort));

            NodeList serverChildNodes = serverElement.getChildNodes();
            for (int i = 0; i < serverChildNodes.getLength(); i++) {
                Node serverChildNode = serverChildNodes.item(i);
                if ("Service".equals(serverChildNode.getNodeName())) {
                    NodeList serviceChildNodes = serverChildNode.getChildNodes();
                    for (int n = 0; n < serviceChildNodes.getLength(); n++) {
                        Node serviceChildNode = serviceChildNodes.item(n);
                        if ("Connector".equals(serviceChildNode.getNodeName())) {
                            Node portNode = serviceChildNode.getAttributes().getNamedItem("port");
                            if ("8080".equals(portNode.getNodeValue())) {
                                portNode.setNodeValue(String.valueOf(httpPort));
                            } else if ("8009".equals(portNode.getNodeValue())) {
                                portNode.setNodeValue(String.valueOf(ajpPort));
                            }
                        }
                    }

                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(serverXml);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            throw new IllegalStateException(String.format("Error occurred while reading or writing file: %s.", tomcatRootPath)
                                            + e.getMessage(), e);
        }
    }

    /** Returns IPv4 address of this machine or loopback address. */
    static String getLocalIPv4Address() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface current = interfaces.nextElement();
                if (!current.isUp() || current.isLoopback() || current.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = current.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress current_addr = addresses.nextElement();
                    if (current_addr.isLoopbackAddress()) {
                        continue;
                    }

                    if (current_addr instanceof Inet4Address) {
                        return current_addr.getHostAddress();
                    }
                }
            }
            return "127.0.0.1";
        } catch (SocketException e) {
            return "127.0.0.1";
        }
    }

    /** A {@code FileVisitor} that finds first file that match the specified pattern. */
    private static class Finder extends SimpleFileVisitor<Path> {
        private final PathMatcher matcher;
        private Path              firstMatchedFile;

        Finder(String pattern) {
            matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        }

        /** {@inheritDoc} */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Path fileName = file.getFileName();
            if (fileName != null && matcher.matches(fileName)) {
                firstMatchedFile = file;
                return TERMINATE;
            }
            return CONTINUE;
        }

        /** Returns the first matched {@link Path}. */
        Path getFirstMatchedFile() {
            return firstMatchedFile;
        }
    }
}
