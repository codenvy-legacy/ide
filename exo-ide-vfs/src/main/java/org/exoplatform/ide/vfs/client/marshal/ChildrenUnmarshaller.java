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
package org.exoplatform.ide.vfs.client.marshal;

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
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Project;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: FilderContentUnmarshaller Feb 2, 2011 2:59:31 PM evgen $
 */
public class ChildrenUnmarshaller implements Unmarshallable<List<Item>> {

    /** Item type */
    private static final String TYPE = "itemType";

    /** Item mime type */
    private static final String MIME_TYPE = "mimeType";

    private final List<Item> items;

    /** @param items */
    public ChildrenUnmarshaller(final List<Item> items) {
        super();
        this.items = items;
        this.items.clear();
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            JSONValue jsonValue = JSONParser.parseLenient(response.getText());
            parseItems(jsonValue.isObject().get("items").isArray());
        } catch (Exception exc) {
            String message = "Can't parse folder content at <b>" + "id" + "</b>! ";
            throw new UnmarshallerException(message);
        }
    }

    @Override
    public List<Item> getPayload() {
        return this.items;
    }

    /**
     * Parse JSON Array to List of Item
     *
     * @param itemsArray
     *         JSON array
     * @return list of children items
     */
    private void parseItems(JSONArray itemsArray) {
        // ArrayList<Item> items = new ArrayList<Item>();
        // items.clear();

        for (int i = 0; i < itemsArray.size(); i++) {
            JSONObject object = itemsArray.get(i).isObject();
            ItemType type = ItemType.valueOf(object.get(TYPE).isString().stringValue());
            String mimeType = null;
            if (object.get(MIME_TYPE).isString() != null)
                mimeType = object.get(MIME_TYPE).isString().stringValue();

            if (type == ItemType.PROJECT) {
                if (Project.PROJECT_MIME_TYPE.equals(mimeType)) {
                    items.add(new ProjectModel(object));
                }
            } else if (type == ItemType.FOLDER) {
                items.add(new FolderModel(object));
            } else
                items.add(new FileModel(object));
        }

        // this.folder.getChildren().setItems(items);
    }

}
