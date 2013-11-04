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
package com.codenvy.ide.resources.marshal;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;


/**
 * Recursively traverses the JSon Response to build tree Folder model
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class FolderTreeUnmarshaller implements Unmarshallable<Folder> {

    private static final String CHILDREN = "children";

    private static final String TYPE = "itemType";

    private static final String ITEM = "item";

    private static final String ID = "id";

    private final Folder parentFolder;

    private final Project parentProject;

    /**
     * @param parentFolder
     * @param parentProject
     */
    public FolderTreeUnmarshaller(Folder parentFolder, Project parentProject) {
        this.parentFolder = parentFolder;
        this.parentFolder.setChildren(JsonCollections.<Resource>createArray());
        this.parentProject = parentProject;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            JSONObject object = JSONParser.parseLenient(response.getText()).isObject();
            getChildren(object.get(CHILDREN), parentFolder, parentProject);

        } catch (Exception exc) {
            String message = "Can't parse response " + response.getText();
            throw new UnmarshallerException(message, exc);
        }
    }

    /**
     * @param children
     * @param parentFolder
     * @param parentProject
     */
    private void getChildren(JSONValue children, Folder parentFolder, Project parentProject) {
        JSONArray itemsArray = children.isArray();

        for (int i = 0; i < itemsArray.size(); i++) {
            JSONObject itemObject = itemsArray.get(i).isObject();
            // Get item
            JSONObject item = itemObject.get(ITEM).isObject();

            String id = item.get(ID).isString().stringValue();

            String type = null;
            if (item.get(TYPE).isNull() == null) {
                type = item.get(TYPE).isString().stringValue();
            }

            // Project found in JSON Response
            if (Project.TYPE.equalsIgnoreCase(type)) {
                Log.error(this.getClass(), "Unsupported operation. Unmarshalling a child projects is not supported");
            }
            // Folder
            else if (Folder.TYPE.equalsIgnoreCase(type)) {
                Folder folder;

                // find if Folder Object already exists. This is a refresh usecase.
                Resource existingFolder = parentFolder.findChildById(id);
                // Make sure found resource is Folder
                if (existingFolder != null && Folder.TYPE.equalsIgnoreCase(existingFolder.getResourceType())) {
                    // use existing folder instance as
                    folder = (Folder)existingFolder;
                } else {
                    folder = new Folder(item);
                    parentFolder.addChild(folder);
                    folder.setProject(parentProject);
                }
                // recursively get project
                getChildren(itemObject.get(CHILDREN), folder, parentProject);
            }
            // File
            else if (File.TYPE.equalsIgnoreCase(type)) {
                File file = new File(item);
                parentFolder.addChild(file);
                file.setProject(parentProject);
            } else {
                Log.error(this.getClass(), "Unsupported Resource type: " + type);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Folder getPayload() {
        return parentFolder;
    }

}
