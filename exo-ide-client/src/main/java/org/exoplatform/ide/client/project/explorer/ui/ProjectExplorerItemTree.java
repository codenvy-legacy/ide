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
package org.exoplatform.ide.client.project.explorer.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TreeItem;

import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ProjectExplorerItemTree extends org.exoplatform.gwtframework.ui.client.component.Tree<Item>
    implements OpenHandler<TreeItem> {

    private String id;
    
    private IDEProject project;
    
    public ProjectExplorerItemTree()
    {
        sinkEvents(Event.ONCONTEXTMENU);
        tree.addOpenHandler(this);        
    }

    public void setTreeGridId(String id) {
        this.id = id;
        getElement().setId(id);
    }

    public String getId() {
        return id;
    }
    
    /** @see com.google.gwt.user.client.ui.Composite#onBrowserEvent(com.google.gwt.user.client.Event) */
    @Override
    public void onBrowserEvent(Event event) {
        if (Event.ONCONTEXTMENU == DOM.eventGetType(event)) {
            NativeEvent nativeEvent = Document.get().createMouseDownEvent(
                    -1, event.getScreenX(), event.getScreenY(), event.getClientX(),
                    event.getClientY(), event.getCtrlKey(), event.getAltKey(),
                    event.getShiftKey(), event.getMetaKey(), NativeEvent.BUTTON_LEFT);
            DOM.eventGetTarget(event).dispatchEvent(nativeEvent);
        }
        super.onBrowserEvent(event);
    }    
    
    @Override
    public void doUpdateValue() {
        System.out.println("Does not use setValue() anymore !!!");
    }

    @Override
    public void onOpen(OpenEvent<TreeItem> event) {
        System.out.println("ProjectExplorerItemTree.onOpen()");
//        ProjectExplorerTreeItem treeItem = (ProjectExplorerTreeItem)event.getTarget();
//        System.out.println("tree item > " + ((Item)treeItem.getUserObject()).getPath());
//        treeItem.refresh(false);
    }
    
    public void setProject(IDEProject project) {
//        if (this.project != null && project != null && this.project.getId().equals(project.getId())) {
//            return;
//        }
//        tree.removeItems();
//
//        this.project = project;
//        if (project == null) {
//            return;
//        }
//
//        ProjectTreeItem treeItem = new ProjectTreeItem(project);
//        tree.addItem(treeItem);
//        treeItem.setState(true);
//        updateHighlighter();
    }
    
//    private void updateHighlighter() {
//        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//            @Override
//            public void execute() {
//                if (tree.getSelectedItem() != null) {
//                    moveHighlight(tree.getSelectedItem());
//                } else {
//                    hideHighlighter();
//                }
//            }
//        });
//    }
    
}