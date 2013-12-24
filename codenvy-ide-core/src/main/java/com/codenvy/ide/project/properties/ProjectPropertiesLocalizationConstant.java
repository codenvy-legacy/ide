/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.project.properties;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization for project properties.
 * 
 * @author Ann Shumilova
 */
public interface ProjectPropertiesLocalizationConstant extends Messages {

    @Key("project.properties.view.title")
    String projectPropertiesViewTitle();

    @Key("property.name.title")
    String propertyNameTitle();

    @Key("property.value.title")
    String propertyValueTitle();

    @Key("delete.button.title")
    String deleteButtonTitle();

    @Key("edit.button.title")
    String editButtonTitle();

    @Key("save.button.title")
    String saveButtonTitle();

    @Key("cancel.button.title")
    String cancelButtonTitle();
    
    @Key("ok.button.title")
    String okButtonTitle();

    @Key("edit.property.view.title")
    String editPropertyViewTitle();
    
    @Key("get.project.properties.failed")
    String getProjectPropertiesFailed();
    
    @Key("save.project.properties.failed")
    String saveProjectPropertiesFailed();
    
    @Key("delete.property.question")
    String deletePropertyQuestion(String property);
}
