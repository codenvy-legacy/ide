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
package org.exoplatform.ide.client.restdiscovery.ui;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TreeItem;

import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Request;
import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.ui.client.component.Tree;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.discovery.IRestService;
import org.exoplatform.ide.client.framework.discovery.RestService;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.restdiscovery.UntypedTreeGrid;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RestServiceTree Mar 18, 2011 10:11:26 AM evgen $
 */
public class RestServiceTree extends Tree<Object> implements UntypedTreeGrid {

    private Set<String> restClassPaths;

    public RestServiceTree() {
        getElement().getStyle().setZIndex(0);
    }

    /**
     * Set id attribute
     *
     * @param id
     */
    public void setTreeId(String id) {
        getElement().setId(id);
    }

    /**
     * @see org.exoplatform.ide.client.restdiscovery.UntypedTreeGrid#setRootValue(org.exoplatform.ide.client.framework.discovery
     * .RestService,
     *      java.util.Set)
     */
    @Override
    public void setRootValue(RestService item, Set<String> restClassPath) {

        this.restClassPaths = restClassPath;
        tree.removeItems();
        for (RestService rs : item.getChildServices().values()) {
            addRestService(null, rs);
        }
        if (tree.getItemCount() > 0) {
            tree.setSelectedItem(tree.getItem(0));
        }
    }

    /** @see org.exoplatform.gwtframework.ui.client.component.Tree#moveHighlight(com.google.gwt.user.client.ui.TreeItem) */
    @Override
    protected void moveHighlight(TreeItem currentItem) {
        super.moveHighlight(currentItem);
        if (currentItem.getElement().getFirstChildElement().getOffsetWidth() > tree.getOffsetWidth()) {
            highlighterPanel.setWidth(currentItem.getElement().getFirstChildElement().getOffsetWidth() + 10 + "px");
        } else {
            highlighterPanel.setWidth("100%");
        }
    }

    private void addRestService(TreeItem parentNode, RestService children) {

        RestService r = null;
        if (parentNode != null)
            r = (RestService)parentNode.getUserObject();

        if (r != null)
            children.setFullPath(r.getFullPath() + children.getPath());
        if (children.getPath().endsWith("/")) {
            children.setPath(children.getPath().substring(0, children.getPath().lastIndexOf('/')));
        }

        String icon = Images.MainMenu.GET_URL;
        if (restClassPaths.contains(children.getFullPath())) {
            icon = Images.RestService.CLASS;
        }

        TreeItem item = getServiceNode(children, icon);
        item.addItem("");
        if (parentNode == null) {
            tree.addItem(item);
        } else {
            parentNode.addItem(item);
            if (parentNode.getChild(0).getUserObject() == null) {
                parentNode.getChild(0).remove();
            }
        }

        for (RestService rs : children.getChildServices().values()) {
            addRestService(item, rs);
        }
    }

    private TreeItem getServiceNode(RestService rs, String icon) {
        TreeItem node = new TreeItem(createTreeNodeWidget(new Image(icon), rs.getPath()));
        node.setUserObject(rs);
        String path = rs.getFullPath();
        // note: agreement: to calculate the id of tree item
        // check, that path doesn't end on /
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        node.getElement().setId(Utils.md5(path));
        return node;
    }

    /**
     *
     */
    private void fillServiceTree(TreeItem parentNode, List<?> children) {
        for (Object o : children) {
            if (o instanceof Resource) {
                Resource r = (Resource)o;
                String title = r.getPath();
                RestService rs = (RestService)parentNode.getUserObject();
                if (rs != null) {
                    if (rs.getFullPath().endsWith("/"))
                        title = title.substring(rs.getFullPath().length() - 1);
                    else
                        title = title.substring(rs.getFullPath().length());
                }

                TreeItem newNode = new TreeItem(createTreeNodeWidget(new Image(Images.RestService.RESOURCE), title));
                newNode.setUserObject(r);
                String path = r.getPath();
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
                newNode.getElement().setId(Utils.md5(path));
                parentNode.addItem(newNode);

                if (r.getMethodOrResource() != null && !r.getMethodOrResource().isEmpty()) {
                    fillServiceTree(newNode, r.getMethodOrResource());
                }
            }
            if (o instanceof Method) {
                Method m = (Method)o;
                if (m.getRequest() == null)
                    m.setRequest(new Request());
                Object re = parentNode.getUserObject();
                if (re instanceof Resource) {
                    Resource res = (Resource)re;

                    if (res != null)
                        m.getRequest().getParam().addAll(res.getParam());
                    m.setHref(((Resource)re).getPath());
                } else if (re instanceof IRestService) {
                    m.setHref(((RestService)re).getFullPath());
                }
                TreeItem newNode = new TreeItem(createTreeNodeWidget(new Image(Images.RestService.METHOD), m.getName()));
                newNode.setUserObject(m);
                String path = m.getHref();
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
                newNode.getElement().setId(Utils.md5(path) + ":" + m.getName());
                parentNode.addItem(newNode);
            }

        }

    }

    /**
     * @see org.exoplatform.ide.client.restdiscovery.UntypedTreeGrid#setPaths(org.exoplatform.ide.client.framework.discovery.RestService,
     *      java.util.List)
     */
    @Override
    public void setPaths(RestService service, List<?> resources) {
        TreeItem node = null;
        for (int i = 0; i < tree.getItemCount(); i++) {
            TreeItem item = tree.getItem(i);
            node = getNode(item, service.getFullPath());
            if (node != null)
                break;
        }

        if (node == null)
            return;

        node.removeItems();

        fillServiceTree(node, resources);
        // sort();
    }

    private TreeItem getNode(TreeItem parent, String name) {

        Object o = parent.getUserObject();
        if (o != null && o instanceof IRestService) {
            if (((RestService)o).getFullPath().equals(name)) {
                return parent;
            }
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            TreeItem child = parent.getChild(i);
            if (child.getUserObject() == null)
                continue;
            if (!(child.getUserObject() instanceof IRestService))
                continue;
            if (((RestService)child.getUserObject()).getFullPath().equals(name)) {
                return child;
            }
            TreeItem item = getNode(child, name);
            if (item != null)
                return item;
        }

        return null;
    }

    @Override
    public void doUpdateValue() {
    }

}
