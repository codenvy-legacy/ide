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
package org.exoplatform.ide.extension.java.client.datasource;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.ide.extension.java.shared.DataSourceOption;
import org.exoplatform.ide.extension.java.shared.DataSourceOptions;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class DatasourceOptionsEditor extends VerticalPanel {
    
    private DataSourceOptions datasource;
    
    private DatasourceChangedHandler datasourceChangedHandler;

    public static String getHumanReadableName(String name) {
        String result = "";

        if (name.indexOf(":") >= 0) {
            name = name.substring(name.indexOf(":") + 1);
        }

        String upper = name.toUpperCase();
        String lower = name.toLowerCase();

        boolean up = true;
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') {
                result += " ";
                up = true;
            } else if (name.charAt(i) == '-') {
                result += " ";
                up = true;
                continue;
            }

            if (up) {
                result += upper.charAt(i);
                up = false;
            } else {
                result += lower.charAt(i);
            }
        }

        return result;
    }
    
    private FlexTable table;

    public void setDatasource(DataSourceOptions datasource) {
        this.datasource = datasource;
        clear();

        if (datasource == null || datasource.getOptions() == null || datasource.getOptions().isEmpty()) {
            return;
        }

        table = new FlexTable();
        
        table.setHTML(0, 0, "Name");
        TextBox nameTextBox = new TextBox();
        nameTextBox.setText(datasource.getName());
        nameTextBox.getElement().getStyle().setWidth(100, Unit.PCT);
        nameTextBox.getElement().setAttribute("datasource-name", datasource.getName());
        table.setWidget(0, 1, nameTextBox);

        nameTextBox.addChangeHandler(textBoxChangeHandler);
        nameTextBox.addKeyUpHandler(textBoxKeyUpHandler);
        
        nameTextBox.setTitle("Configuration name");

        table.getCellFormatter().getElement(0, 0).getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
        table.getCellFormatter().getElement(0, 1).getStyle().setWidth(100, Unit.PCT);

        table.setHTML(1, 0, "&nbsp;");
        table.getCellFormatter().getElement(1, 0).setAttribute("colspan", "2");
        
        int index = 2;
        
        for (int i = 0; i < datasource.getOptions().size(); i++) {
            DataSourceOption option = datasource.getOptions().get(i);
            
            String titleHTML = getHumanReadableName(option.getName());
            if (option.isRequired()) {
                titleHTML += "&nbsp;<span style=\"color:red; font-weight: bold;\">*</span>";
            }
            table.setHTML(index + i, 0, titleHTML);

            TextBox textBox = new TextBox();
            textBox.setText(option.getValue());
            textBox.getElement().getStyle().setWidth(100, Unit.PCT);
            textBox.getElement().setAttribute("datasource-option-name", option.getName());
            table.setWidget(index + i, 1, textBox);

            textBox.addChangeHandler(textBoxChangeHandler);
            textBox.addKeyUpHandler(textBoxKeyUpHandler);
            
            textBox.setTitle(option.getDescription());

            table.getCellFormatter().getElement(index + i, 0).getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
            table.getCellFormatter().getElement(index + i, 1).getStyle().setWidth(100, Unit.PCT);
        }

        add(table);
    }
    
    private ChangeHandler textBoxChangeHandler = new ChangeHandler() {
           @Override
           public void onChange(ChangeEvent event) {
               if (event.getSource() instanceof TextBox) {
                   checkValueChanged((TextBox)event.getSource());
                   return;
               }
           }
       };

    private KeyUpHandler  textBoxKeyUpHandler  = new KeyUpHandler() {
           @Override
           public void onKeyUp(KeyUpEvent event) {
               if (event.getSource() instanceof TextBox) {
                   checkValueChanged((TextBox)event.getSource());
                   return;
               }
           }
       };

    private void checkValueChanged(TextBox textBox) {
        String text = textBox.getText();
        
        if (textBox.getElement().hasAttribute("datasource-name")) {
            if (!text.equals(datasource.getName())) {
                datasource.setName(text);
                if (datasourceChangedHandler != null) {
                    datasourceChangedHandler.onDatasourceNameChanged(datasource);
                }
            }
        } else if (textBox.getElement().hasAttribute("datasource-option-name")) {
            String optionName = textBox.getElement().getAttribute("datasource-option-name");
            DataSourceOption option = getOption(optionName);
            if (option != null && !text.equals(option.getValue())) {
                option.setValue(text);
                if (datasourceChangedHandler != null) {
                    datasourceChangedHandler.onDatasourceChanged(datasource);
                }
            }
        }
    }
    
    private DataSourceOption getOption(String optionName) {
        if (optionName != null) {
            for (DataSourceOption option : datasource.getOptions()) {
                if (option.getName().equals(optionName)) {
                    return option;
                }
            }
        }
        
        return null;
    }

    public void setDatasourceChangedHandler(DatasourceChangedHandler datasourceChangedHandler) {
        this.datasourceChangedHandler = datasourceChangedHandler;
    }

}
