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
package com.codenvy.ide;

import com.google.gwt.i18n.client.Messages;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public interface CoreLocalizationConstant extends Messages {
    @Key("createProjectFromTemplate.nameField")
    String createProjectFromTemplateName();

    @Key("createProjectFromTemplate.selectTemplate")
    String createProjectFromTemplateSelectTemplate();

    @Key("noIncorrectProjectNameMessage")
    String noIncorrectProjectNameMessage();

    @Key("createProjectFromTemplate.project.exists")
    String createProjectFromTemplateProjectExists(String projectName);

    @Key("chooseTechnology")
    String chooseTechnology();

    @Key("chooseTechnologyTooltip")
    String chooseTechnologyTooltip();

    @Key("enteringProjectName")
    String enteringProjectName();

    @Key("checkingProjectsList")
    String checkingProjectsList();

    @Key("choosePaas")
    String choosePaaS();

    @Key("choosePaasTooltip")
    String choosePaaSTooltip();

    @Key("noTechnologyTitle")
    String noTechnologyTitle();

    @Key("noTechnologyMessage")
    String noTechnologyMessage();

    @Key("extension.title")
    String extensionTitle();

    @Key("enteringResourceName")
    String enteringResourceName();

    @Key("noIncorrectResourceName")
    String noIncorrectResourceName();

    @Key("resourceExists")
    String resourceExists(String resourceName);

    @Key("chooseResourceType")
    String chooseResourceType();
}