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
package org.exoplatform.ide.project;

import org.exoplatform.ide.commons.PomUtils;
import org.exoplatform.ide.commons.ProjectType;
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

    private boolean isMavenProject(ItemNode sourceFolderNode) {
        for (ItemNode nestedNode : sourceFolderNode.getChildren()) {
            if ("pom.xml".equals(nestedNode.getItem().getName())) {
                return true;
            }
        }
        return false;
    }

    private void parseNonMavenProject(ItemNode sourceFolderNode) throws VirtualFileSystemException, ProjectPrepareException {
        LinkedList<ItemNode> q = new LinkedList<ItemNode>();
        q.add(sourceFolderNode);

        while (!q.isEmpty()) {
            ItemNode node = q.pop();
            if (node.getItem() instanceof FolderImpl) {
                q.addAll(node.getChildren());
            } else if (node.getItem() instanceof FileImpl) {
                ProjectType type = null;

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

                if (type == null) {
                    throw new ProjectPrepareException(400, "autodetection:failed");
                }

                List<Property> props = new ArrayList<Property>();
                props.add(new PropertyImpl("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
                props.add(new PropertyImpl("vfs:projectType", type.toString()));

                vfs.updateItem(node.getItem().getParentId(), props, null);
            }
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
                props.add(new PropertyImpl("Maven Module", "true"));

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
