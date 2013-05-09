/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.project;

import com.codenvy.ide.commons.server.PomUtils;
import com.codenvy.ide.commons.shared.ProjectType;

import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Preparing source directory with setting properties on folder to indicate them as Codenvy project based on sources.
 * For example if we find pom.xml than we sett type of folder as Java project, if ruby - Ruby project, etc.
 * If we can't determine with what sources we works it throws exception with message and client ask user to set
 * custom project type for source folder.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ProjectPrepare {
    private VirtualFileSystem vfs;

    public ProjectPrepare(VirtualFileSystem vfs) {
        this.vfs = vfs;
    }

    /**
     * Convert specified folder in file system to Codenvy project
     *
     * @param sourcePath
     *         - absolute project path where sources are stored
     * @throws ProjectPrepareException
     */
    public void doPrepare(String sourcePath, String folderId, List<Property> otherProperties)
            throws ProjectPrepareException, VirtualFileSystemException {
        //if pom.xml exist in the source directory it means that we have java project
        if (new File(sourcePath, "pom.xml").exists()) {
            try {
                Map<String, File> mavenModules = listMavenModulesByPath(new File(sourcePath));

                List<Property> properties = new ArrayList<Property>(2);
                properties.add(new PropertyImpl("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
                properties.add(new PropertyImpl("Maven Module", "true"));
                properties.addAll(otherProperties);

                //Just set for all modules where we find pom.xml that it is maven module and default project type
                for (Map.Entry<String, File> entry : mavenModules.entrySet()) {
                    vfs.updateItem(vfs.getItemByPath(entry.getKey(), null, PropertyFilter.ALL_FILTER).getId(), properties, null);
                }

                setJavaProjectTypesProperties(mavenModules);
            } catch (VirtualFileSystemException e) {
                throw new ProjectPrepareException(e.getMessage());
            }
        } else {
            ProjectType detectedType = detectNonJavaProjectType(sourcePath);
            if (detectedType != null) {
                writeNonJavaProjectProperty(folderId, detectedType);
            } else {
                throw new ProjectPrepareException(400, "autodetection:failed");
            }
        }
    }

    /**
     * Find all pom.xml in specified path. if we find pom.xml in directory it will be means that this directory is maven
     * module
     *
     * @param path
     *         - path to search all pom.xml
     * @return - map contains module relative path and physical path to maven module
     */
    private Map<String, File> listMavenModulesByPath(File path) {
        Map<String, File> modules = new HashMap<String, File>();
        LinkedList<File> q = new LinkedList<File>();
        q.add(path);
        while (!q.isEmpty()) {
            File current = q.pop();
            File[] list = current.listFiles();
            if (list != null) {
                for (File f : list) {
                    if (f.isDirectory()) {
                        q.push(f);
                        continue;
                    }

                    if ("pom.xml".equals(f.getName())) {
                        String filePath = f.getParent();
                        String concated = filePath.substring(path.getParent().length());

                        modules.put(concated, f.getParentFile());
                    }
                }
            }
        }
        return modules;
    }

    /**
     * Writing properties for each maven module. Properties contains information about project type
     *
     * @param mavenModules
     *         - path where maven module is placed
     * @throws VirtualFileSystemException
     *         - if writing properties is failed
     */
    private void setJavaProjectTypesProperties(Map<String, File> mavenModules) throws VirtualFileSystemException {
        for (Map.Entry<String, File> entry : mavenModules.entrySet()) {
            ProjectType detectedType = detectJavaProjectType(entry.getValue());

            //If detected project type is default it isn't necessary to set default project type, it already exist
            if (detectedType != ProjectType.DEFAULT) {
                List<Property> properties = Collections.<Property>singletonList(
                        new PropertyImpl("vfs:projectType", detectedType.toString()));
                vfs.updateItem(vfs.getItemByPath(entry.getKey(), null, PropertyFilter.ALL_FILTER).getId(), properties, null);
            }
        }
    }

    /**
     * Detecting project type of specified maven module path
     *
     * @param modulePath
     *         - path where maven module is placed
     * @return detected project type specified on source files
     */
    private ProjectType detectJavaProjectType(File modulePath) {
        File pomXML = new File(modulePath, "pom.xml");
        InputStream pomXMLStream = null;

        try {
            pomXMLStream = new FileInputStream(pomXML);
            PomUtils.Pom pomObject = PomUtils.parse(pomXMLStream);

            //Detecting multimodule project type
            if (pomObject.getModules().size() != 0) {
                return ProjectType.MULTI_MODULE;
            }

            //Detecting spring project type
            File webXML = findWebXML(pomObject, modulePath);
            if (webXML.exists() && detectSpringApp(webXML)) {
                return ProjectType.SPRING;
            }

            //Detecting GAE project type
            if (detectGAEApp(pomObject, modulePath)) {
                return ProjectType.WAR;
            }

            String pomPackaging = pomObject.getPackaging();

            //Detecting  simple jar lib project
            if ("jar".equals(pomPackaging)) {
                return ProjectType.JAR;
            }
            if ("war".equals(pomPackaging)) {
                return ProjectType.WAR;
            }
        } catch (Exception e) { //ignore this exception, at the end it will return default project type
        } finally {
            if (pomXMLStream != null) {
                try {
                    pomXMLStream.close();
                } catch (IOException e) {
                }
            }
        }

        return ProjectType.DEFAULT;
    }

    /**
     * Find WEB-INF/web.xml for feature parsing, e.g. to find declaration of spring framework servlets.
     *
     * @param pomObject
     *         - representing of pom.xml to find source path
     * @param modulePath
     *         - path where maven module is placed
     * @return web.xml file object
     */
    private File findWebXML(PomUtils.Pom pomObject, File modulePath) {
        //TODO not sure about this trick, learn about custom setting warSourceDirectory
        File baseModulePath = new File(modulePath, pomObject.getSourcePath()).getParentFile();
        File webXML = new File(baseModulePath, "webapp/WEB-INF/web.xml");

        return webXML;
    }

    /**
     * Detecting Google App Engine. Detect processing by finding WEB-INF/appengine-web.xml file. If it present it means
     * that our project is GAE.
     *
     * @param pomObject
     *         - representing of pom.xml to find source path
     * @param modulePath
     *         - path where maven module is placed
     * @return - true if file found, otherwise false
     */
    private boolean detectGAEApp(PomUtils.Pom pomObject, File modulePath) {
        File baseModulePath = new File(modulePath, pomObject.getSourcePath()).getParentFile();
        File appengineWebXML = new File(baseModulePath, "webapp/WEB-INF/appengine-web.xml");

        return appengineWebXML.exists();
    }

    /**
     * Detect Spring application. Detect processing by parsing WEB-INF/web.xml file by presenting of DispatcherServlet
     * declaration.
     *
     * @param webXML
     *         - path to webapp/WEB-INF/web.xml file for searching DispatcherServlet declaration
     * @return - true if specified declaration found, otherwise it returns false
     * @throws ParserConfigurationException
     *         - if parsing web.xml is failed
     * @throws IOException
     *         - if parsing web.xml is failed
     * @throws SAXException
     *         - if parsing web.xml is failed
     */
    private boolean detectSpringApp(File webXML) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(webXML);
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("servlet-class");

        if (nList.getLength() != 0) {
            for (int i = 0; i < nList.getLength(); i++) {
                if (nList.item(i).getTextContent().matches(".*DispatcherServlet.*")) {
                    return true;
                }
            }
        }
        return false;
    }

    private ProjectType detectNonJavaProjectType(String sourcePath) {
        LinkedList<File> q = new LinkedList<File>();
        q.add(new File(sourcePath));
        while (!q.isEmpty()) {
            File current = q.pop();
            File[] list = current.listFiles();
            if (list != null) {
                for (File f : list) {
                    if (f.isDirectory()) {
                        q.push(f);
                    } else {
                        if (f.getName().endsWith(".rb")) {
                            return ProjectType.RUBY_ON_RAILS;
                        } else if (f.getName().equals("package.json") || f.getName().equals("app.js") || f.getName().equals("server.js")) {
                            return ProjectType.NODE_JS;
                        } else if (f.getName().endsWith(".js")) {
                            return ProjectType.JAVASCRIPT;
                        } else if (f.getName().endsWith(".py")) {
                            return ProjectType.PYTHON;
                        } else if (f.getName().endsWith(".php")) {
                            return ProjectType.PHP;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void writeNonJavaProjectProperty(String folderId, ProjectType type) throws VirtualFileSystemException {
        List<Property> properties = new ArrayList<Property>(2);
        properties.add(new PropertyImpl("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
        properties.add(new PropertyImpl("vfs:projectType", type.toString()));

        vfs.updateItem(folderId, properties, null);
    }
}
