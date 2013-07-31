/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.extruntime.server;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.codehaus.plexus.util.xml.Xpp3DomBuilder.build;

/**
 * Utils to work with Maven POM and GWT module descriptor (gwt.xml) files.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: Utils.java Jul 31, 2013 11:30:14 AM azatsarynnyy $
 */
class Utils {
    /** Maven POM reader. */
    private static MavenXpp3Reader pomReader                = new MavenXpp3Reader();
    /** Maven POM writer. */
    private static MavenXpp3Writer pomWriter                = new MavenXpp3Writer();

    /** Directive for GWT-module descriptor to enable GWT SuperDevMode: use cross-site IFrame linker and enable using source maps. */
    // Set 'failIfScriptTag' property to FALSE, to avoid error messages that <script> tags exist in Commons.gwt.xml
    private static final String    SUPER_DEV_MODE_DIRECTIVE =
                                                              "\r\n\t<add-linker name='xsiframe' />"
                                                                  + "\r\n\t<set-configuration-property name='devModeRedirectEnabled' value='true' />"
                                                                  + "\r\n\t<set-configuration-property name='xsiframe.failIfScriptTag' value='false'/>"
                                                                  + "\r\n\t<set-property name='compiler.useSourceMaps' value='true' />";

    static Model readPom(Path path) {
        try {
            return readPom(Files.newInputStream(path));
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error occurred while reading content of file: %s.", path));
        }
    }

    static Model readPom(InputStream stream) {
        try {
            return pomReader.read(stream, true);
        } catch (IOException | XmlPullParserException e) {
            throw new IllegalStateException("Error occurred while parsing pom.xml.");
        }
    }

    static void writePom(Model pom, Path path) {
        try {
            pomWriter.write(Files.newOutputStream(path), pom);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error occurred while writing content to file: %s.", path));
        }
    }

    static void addDependencyToPom(Path pomPath, String groupId, String artifactId, String version) {
        Dependency dep = new Dependency();
        dep.setGroupId(groupId);
        dep.setArtifactId(artifactId);
        dep.setVersion(version);

        Model pom = readPom(pomPath);
        pom.getDependencies().add(dep);

        writePom(pom, pomPath);
    }

    /** Add the provided module to the specified reactor POM. */
    static void addModuleToReactorPom(Path reactorPomPath, String newModuleRelativePath) {
        addModuleToReactorPom(reactorPomPath, newModuleRelativePath, null);
    }

    /**
     * Add the provided module to the specified reactor POM. If moduleAfter isn't null - new module will be inserted before the moduleAfter.
     */
    static void addModuleToReactorPom(Path reactorPomPath, String newModuleRelativePath, String moduleAfter) {
        Model pom = readPom(reactorPomPath);
        List<String> modulesList = pom.getModules();
        if (moduleAfter == null) {
            modulesList.add(newModuleRelativePath);
        } else {
            int n = 0;
            for (String module : modulesList) {
                if (moduleAfter.equals(module)) {
                    pom.getModules().add(n, newModuleRelativePath);
                    break;
                }
                n++;
            }
        }
        writePom(pom, reactorPomPath);
    }

    /** Change GWT Maven plug-in configuration into the specified pom.xml file, to set a new code server's working directory. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static void changeCodeServerWorkDir(Path pomPath, Path dirPath) throws ExtensionLauncherException {
        final String configString = String.format("<configuration>"
                                                  + "<codeServerWorkDir>%s</codeServerWorkDir>"
                                                  + "</configuration>", dirPath);
        try {
            Xpp3Dom additionalConfiguration = build(new StringReader(configString));

            Model pom = readPom(pomPath);
            Build build = pom.getBuild();
            Map<String, Plugin> plugins = build.getPluginsAsMap();
            Plugin gwtPlugin = plugins.get("org.codehaus.mojo:gwt-maven-plugin");
            Xpp3Dom existingConfiguration = (Xpp3Dom)gwtPlugin.getConfiguration();
            Xpp3Dom mergedConfiguration = Xpp3DomUtils.mergeXpp3Dom(existingConfiguration, additionalConfiguration);
            gwtPlugin.setConfiguration(mergedConfiguration);
            build.setPlugins(new ArrayList(plugins.values()));

            writePom(pom, pomPath);
        } catch (IOException | XmlPullParserException e) {
            throw new IllegalStateException("Can't parse pom.xml.");
        }
    }

    /** Enable SuperDevMode for the specified GWT module descriptor. */
    static void enableSuperDevMode(Path gwtModuleDescriptorPath) throws ExtensionLauncherException {
        try {
            List<String> content = Files.readAllLines(gwtModuleDescriptorPath, UTF_8);
            int penultimateLine = 0;
            for (String str : content) {
                penultimateLine++;
                if (str.contains("</module>")) {
                    break;
                }
            }
            content.add(penultimateLine - 1, SUPER_DEV_MODE_DIRECTIVE);

            Files.write(gwtModuleDescriptorPath, content, UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error occurred while reading or writing file: %s.", gwtModuleDescriptorPath));
        }
    }

    /** Add the provided module name as a dependency to the specified GWT module descriptor. */
    static void inheritGwtModule(Path gwtModuleDescriptorPath, String inheritableGwtModuleLogicalName) throws ExtensionLauncherException {
        final String inheritsString = "\t<inherits name='" + inheritableGwtModuleLogicalName + "'/>";
        try {
            List<String> content = Files.readAllLines(gwtModuleDescriptorPath, UTF_8);
            // insert custom module as last 'inherits' entry
            int i = 0, lastInheritsLine = 0;
            for (String str : content) {
                i++;
                if (str.contains("<inherits")) {
                    lastInheritsLine = i;
                }
            }
            content.add(lastInheritsLine, inheritsString);

            Files.write(gwtModuleDescriptorPath, content, UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error occurred while reading or writing file: %s.", gwtModuleDescriptorPath));
        }
    }

    /** It's a workaround for known bug in GWT Maven plug-in. See the https://jira.codehaus.org/browse/MGWT-332 for details. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static void fixMGWT332Bug(Path pomPath, String extensionModuleName, String profileId) throws ExtensionLauncherException {
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

        final String confString = String.format("<configuration>"
                                                + "<sources><source>../%1$s/src/main/java</source></sources>"
                                                + "<resources><resource>../%1$s/src/main/resources</resource></resources>"
                                                + "</configuration>", extensionModuleName);

        try {
            Xpp3Dom configuration = build(new StringReader(confString));
            execution.setConfiguration(configuration);
            profile.getBuild().setPlugins(new ArrayList(plugins.values()));

            writePom(pom, pomPath);
        } catch (IOException | XmlPullParserException e) {
            throw new IllegalStateException("Can't parse pom.xml.");
        }
    }
}
