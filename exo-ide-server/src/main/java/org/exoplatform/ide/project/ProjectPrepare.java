/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package org.exoplatform.ide.project;

import com.codenvy.ide.commons.server.PomUtils;
import com.codenvy.ide.commons.shared.ProjectType;

import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.*;
import org.exoplatform.ide.vfs.shared.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ProjectPrepare {
    private final Pattern SPRING_FRAMEWORK_PATTERN = Pattern.compile("spring");

    private VirtualFileSystem vfs;

    private boolean multiModuleProject = false;

    public ProjectPrepare(VirtualFileSystem vfs) {
        this.vfs = vfs;
    }

    /**
     * Detecting what kind of project we have, if pom.xml detected in children list that we have maven project,
     * after that we try to parse maven project. If maven project is multi-module - try to parse modules and set
     * their types. Otherwise if we can't found pom.xml - it seems that we have non-java project.
     *
     * @param sourcePathFolderId
     *         id for source folder
     */
    public void doPrepare(String sourcePathFolderId)
            throws VirtualFileSystemException, ParserConfigurationException, SAXException, XPathExpressionException, IOException,
                   ProjectPrepareException {
        ItemNode sourceFolderNode = vfs.getTree(sourcePathFolderId, -1, Boolean.FALSE, PropertyFilter.ALL_FILTER);

        if (isMavenProject(sourceFolderNode)) {
            parseMavenProject(sourceFolderNode);
        } else {
            parseNonMavenProject(sourceFolderNode);
        }
    }

    private boolean isMavenProject(ItemNode sourceFolderNode)
            throws VirtualFileSystemException, ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        for (ItemNode nestedNode : sourceFolderNode.getChildren()) {
            if ("pom.xml".equals(nestedNode.getItem().getName())) {
                ContentStream parentPom = vfs.getContent(nestedNode.getItem().getId());
                PomUtils.Pom pom = PomUtils.parse(parentPom.getStream());
                if (pom.getModules().size() > 0) {
                    multiModuleProject = true;
                }
                return true;
            }
        }
        return false;
    }

    private void parseNonMavenProject(ItemNode sourceFolderNode) throws VirtualFileSystemException, ProjectPrepareException {
        LinkedList<ItemNode> q = new LinkedList<ItemNode>();
        q.add(sourceFolderNode);

        ProjectType type = null;

        while (!q.isEmpty()) {
            ItemNode node = q.pop();
            if (node.getItem() instanceof FolderImpl) {
                q.addAll(node.getChildren());
            } else if (node.getItem() instanceof FileImpl) {
                if (node.getItem().getName().endsWith(".php")) {
                    type = ProjectType.PHP;
                } else if (node.getItem().getName().endsWith(".py")) {
                    type = ProjectType.PYTHON;
                } else if (node.getItem().getName().endsWith(".rb")) {
                    type = ProjectType.RUBY_ON_RAILS;
                } else if (node.getItem().getName().equals("server.js") || node.getItem().getName().equals("app.js") ||
                           node.getItem().getName().equals("package.json")) {
                    type = ProjectType.NODE_JS;
                }

                if (type != null) {
                    List<Property> props = new ArrayList<Property>();
                    props.add(new PropertyImpl("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
                    props.add(new PropertyImpl("vfs:projectType", type.toString()));

                    vfs.updateItem(node.getItem().getParentId(), props, null);
                    return;
                }
            }
        }

        if (type == null)
        {
            throw new ProjectPrepareException(400, "autodetection:failed");
        }
    }

    private void parseMavenProject(ItemNode sourceFolderNode)
            throws VirtualFileSystemException, ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        LinkedList<ItemNode> q = new LinkedList<ItemNode>();
        q.add(sourceFolderNode);

        while (!q.isEmpty()) {
            ItemNode node = q.pop();
            if (node.getItem() instanceof FolderImpl) {
                q.addAll(node.getChildren());
            } else if (node.getItem() instanceof FileImpl && "pom.xml".equals(node.getItem().getName())) {
                ProjectType type = detectMavenModuleProjectType(node);

                List<Property> props = new ArrayList<Property>();

                Item itemToUpdate = vfs.getItem(node.getItem().getParentId(), false, PropertyFilter.ALL_FILTER);
                props.addAll(itemToUpdate.getProperties());
                props.add(new PropertyImpl("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
                props.add(new PropertyImpl("vfs:projectType", type.toString()));

                if (multiModuleProject) {
                    props.add(new PropertyImpl("Maven Module", "true"));
                }

                vfs.updateItem(itemToUpdate.getId(), props, null);
            }
        }
    }

    private ProjectType detectMavenModuleProjectType(ItemNode pomXmlNode)
            throws VirtualFileSystemException, ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        ContentStream parentPom = vfs.getContent(pomXmlNode.getItem().getId());
        PomUtils.Pom pom = PomUtils.parse(parentPom.getStream());

        if (pom.getModules().size() > 0) {
            return ProjectType.MULTI_MODULE;
        }

        for (PomUtils.Dependency dependency : pom.getDependencies()) {
            if (SPRING_FRAMEWORK_PATTERN.matcher(dependency.getGroupId()).find()) {
                return ProjectType.SPRING;
            }
        }

        if ("war".equals(pom.getPackaging())) {
            return ProjectType.WAR;
        }
        if ("jar".equals(pom.getPackaging())) {
            return ProjectType.JAR;
        }

        return ProjectType.DEFAULT;
    }
}
