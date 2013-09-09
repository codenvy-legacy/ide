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
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.ItemType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 30, 2011 evgen $
 */
public class ItemUnmarshaller implements Unmarshallable<ItemWrapper> {

    private ItemWrapper wrapper;

    /** Item type */
    private static final String TYPE = "itemType";

    /** Item mime type */
    private static final String MIME_TYPE = "mimeType";

    /** @param item */
    public ItemUnmarshaller(ItemWrapper item) {
        this.wrapper = item;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            JSONValue val = JSONParser.parseLenient(response.getText());
            JSONObject object = val.isObject();
            ItemType type = ItemType.valueOf(object.get(TYPE).isString().stringValue());
            String mimeType = null;
            if (object.get(MIME_TYPE).isString() != null)
                mimeType = object.get(MIME_TYPE).isString().stringValue();

            if (type == ItemType.PROJECT) {
                wrapper.setItem(new ProjectModel(object));
            } else if (type == ItemType.FOLDER) {
                wrapper.setItem(new FolderModel(object));
            } else {
                FileModel file = new FileModel(object);
                if (wrapper.getItem() != null) {
                    FolderModel parent = ((FileModel)wrapper.getItem()).getParent();
                    ProjectModel project = ((FileModel)wrapper.getItem()).getProject();
                    file.setParent(parent);
                    file.setProject(project);
                }
                wrapper.setItem(file);
            }
        } catch (Exception e) {
            throw new UnmarshallerException("Can't parse item.");
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public ItemWrapper getPayload() {
        return wrapper;
    }

}
