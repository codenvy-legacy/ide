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
package org.exoplatform.ide.client.framework.project.api;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.*;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class FolderTreeUnmarshaller implements Unmarshallable<Folder> {

    private static final String CHILDREN = "children";

    private static final String TYPE = "itemType";

    private static final String MIME_TYPE = "mimeType";

    private static final String ITEM = "item";

    private final FolderModel folder;

    public FolderTreeUnmarshaller(FolderModel folder) {
        this.folder = folder;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            JSONObject object = JSONParser.parseLenient(response.getText()).isObject();

            JSONValue item = object.get(ITEM);
            folder.init(item.isObject());

            ItemList<Item> children = getChildren(object.get(CHILDREN));
            folder.setChildren(children);
            
            updateChildren(folder);
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new UnmarshallerException("Can't parse JSON response.");
        }
    }

    private void updateChildren(FolderModel parent) {
        ProjectModel project = parent instanceof ProjectModel ? (ProjectModel)parent : parent.getProject();
        
        for (Item item : parent.getChildren().getItems()) {
            if (item instanceof FolderModel) {
                ((FolderModel)item).setProject(project);
                ((FolderModel)item).setParent(parent);
                updateChildren((FolderModel)item);
            } else if (item instanceof FileModel) {
                ((FileModel)item).setProject(project);
                ((FileModel)item).setParent(parent);
            }
        }

    }

    private ItemList<Item> getChildren(JSONValue children) {
        ItemList<Item> itemList = new ItemListImpl<Item>();

        if (children.isArray() == null) {
            return itemList;
        }

        if (children.isNull() != null) {
            return itemList;
        }

        JSONArray itemsArray = children.isArray();
        for (int i = 0; i < itemsArray.size(); i++) {
            JSONObject itemObject = itemsArray.get(i).isObject();

            JSONObject item = itemObject.get(ITEM).isObject();

            String mimeType = null;
            if (item.get(MIME_TYPE).isString() != null)
                mimeType = item.get(MIME_TYPE).isString().stringValue();

            ItemType type = null;

            if (item.get(TYPE).isNull() == null) {
                type = ItemType.valueOf(item.get(TYPE).isString().stringValue());
            }

            if (ItemType.PROJECT == type) {
                if (Project.PROJECT_MIME_TYPE.equals(mimeType)) {
                    try {
                        ProjectModel projectModel = new ProjectModel(item);
                        IDEProject project = ProjectBuilder.createProject(projectModel);

                        itemList.getItems().add(project);
                        project.setChildren(getChildren(itemObject.get(CHILDREN)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Invalid JSON " + item.toString());
                    }

                }
            } else if (ItemType.FOLDER == type) {
                try {
                    FolderModel folder = new FolderModel(item);
                    itemList.getItems().add(folder);
                    folder.setChildren(getChildren(itemObject.get(CHILDREN)));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Invalid JSON " + item.toString());
                }
            } else {
                try {
                    FileModel file = new FileModel(item);
                    itemList.getItems().add(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Invalid JSON " + item.toString());
                }

            }
        }

        return itemList;
    }

    @Override
    public Folder getPayload() {
        return folder;
    }

}
