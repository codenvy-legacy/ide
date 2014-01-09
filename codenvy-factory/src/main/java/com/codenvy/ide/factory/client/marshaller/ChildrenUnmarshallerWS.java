package com.codenvy.ide.factory.client.marshaller;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.websocket.rest.ResponseMessage;
import org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Project;

import java.util.List;

/**
 * Unmarshaller for list of Items which gets by websocket.
 * @author Vladyslav Zhukovskii
 */
public class ChildrenUnmarshallerWS implements Unmarshallable<List<Item>> {

    /** Item type */
    private static final String TYPE = "itemType";

    /** Item mime type */
    private static final String MIME_TYPE = "mimeType";

    private final List<Item> items;

    public ChildrenUnmarshallerWS(List<Item> items) {
        this.items = items;
        this.items.clear();
    }

    @Override
    public void unmarshal(ResponseMessage response) throws UnmarshallerException {
        try {
            JSONValue jsonValue = JSONParser.parseLenient(response.getBody());
            parseItems(jsonValue.isArray());
        } catch (Exception exc) {
            throw new UnmarshallerException(exc.getMessage());
        }
    }

    @Override
    public List<Item> getPayload() {
        return items;
    }

    /** Parse input json strings into Items */
    private void parseItems(JSONArray itemsArray) {
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
    }
}
