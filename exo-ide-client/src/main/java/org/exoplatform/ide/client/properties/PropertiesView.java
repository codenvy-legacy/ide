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
package org.exoplatform.ide.client.properties;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.*;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PropertiesView extends ViewImpl implements
                                             org.exoplatform.ide.client.properties.PropertiesPresenter.Display {

    private static final String ID = "ideFilePropertiesView";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.propertiesTitle();

    private Grid propertiesGrid;

    public PropertiesView() {
        super(ID, ViewType.OPERATION, TITLE, new Image(IDEImageBundle.INSTANCE.properties()));

        propertiesGrid = new Grid();
        propertiesGrid.setSize("100%", "100%");
        propertiesGrid.setCellPadding(0);
        propertiesGrid.setCellSpacing(5);

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(propertiesGrid);
        add(scrollPanel);
    }

    @Override
    public void showProperties(FileModel file) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("Name", file.getName());
        properties.put("Path", file.getPath());
        properties.put("Mime Type", file.getMimeType());
        properties.put("Created",
                       DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(new Date(file.getCreationDate())));
        properties.put("Last modified",
                       DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(new Date(file.getLastModificationDate())));
        properties.put("Content length", "" + file.getLength());

        if (file.getProperties() != null && !file.getProperties().isEmpty()) {
            properties.putAll(getVisibleProperties(file.getProperties()));
        }

        propertiesGrid.resize(properties.size() + 1, 2);

        int row = 0;
        Set<String> keys = properties.keySet();
        for (String key : keys) {
            String value = properties.get(key);
            propertiesGrid.setHTML(row, 0, "<b>" + key + ":&nbsp;&nbsp;</b>");
            propertiesGrid.setHTML(row, 1, value);

            // add id to get access to field's text from Selenium tests
            DOM.setElementAttribute(propertiesGrid.getCellFormatter().getElement(row, 1), "propertyName", key);

            DOM.setStyleAttribute(propertiesGrid.getCellFormatter().getElement(row, 0), "fontFamily",
                                  "Verdana,Bitstream Vera Sans,sans-serif");
            DOM.setStyleAttribute(propertiesGrid.getCellFormatter().getElement(row, 0), "fontSize", "11px");
            DOM.setStyleAttribute(propertiesGrid.getCellFormatter().getElement(row, 1), "fontFamily",
                                  "Verdana,Bitstream Vera Sans,sans-serif");
            DOM.setStyleAttribute(propertiesGrid.getCellFormatter().getElement(row, 1), "fontSize", "11px");
            DOM.setStyleAttribute(propertiesGrid.getCellFormatter().getElement(row, 0), "textAlign", "right");
            DOM.setStyleAttribute(propertiesGrid.getCellFormatter().getElement(row, 1), "width", "100%");
            DOM.setElementAttribute(propertiesGrid.getCellFormatter().getElement(row, 0), "nowrap", "nowrap");
            DOM.setElementAttribute(propertiesGrid.getCellFormatter().getElement(row, 1), "nowrap", "nowrap");

            row++;
        }

        propertiesGrid.setHTML(row, 0, "<div style=\"width:1px; height:1px;\"></div>");
        propertiesGrid.setHTML(row, 1, "<div style=\"width:1px; height:1px;\"></div>");
        DOM.setStyleAttribute(propertiesGrid.getRowFormatter().getElement(row), "height", "100%");
    }

    public Map<String, String> getVisibleProperties(Collection<Property> properties) {
        Map<String, String> propertiesMap = new LinkedHashMap<String, String>();

        for (Property property : properties) {
            String propertyName = property.getName();
            propertiesMap.put(propertyName, property.getValue().get(0));
        }

        return propertiesMap;
    }

}