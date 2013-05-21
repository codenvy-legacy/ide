/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.extension.cloudfoundry.client.create;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link CreateApplicationPresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface CreateApplicationView extends View<CreateApplicationView.ActionDelegate> {
    /** Needs for delegate some function into CreateApplication view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Create button. */
        void onCreateClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed auto detect application's type value. */
        void onAutoDetectTypeChanged();

        /** Performs any actions appropriate in response to the user having changed custom url. */
        void onCustomUrlChanged();

        /** Performs any actions appropriate in response to the user having changed application's name. */
        void onApplicationNameChanged();

        /** Performs any actions appropriate in response to the user having changed application's type. */
        void onTypeChanged();

        /** Performs any actions appropriate in response to the user having changed server. */
        void onServerChanged();
    }

    /**
     * Returns application's type.
     *
     * @return type's name
     */
    String getType();

    /**
     * Returns whether need to auto detect type of project.
     *
     * @return <code>true</code> if need to auto detect type of project, and
     *         <code>false</code> otherwise
     */
    boolean isAutodetectType();

    /**
     * Sets whether need to auto detect project type.
     *
     * @param autodetected
     *         <code>true</code> need to auto detect project type, <code>false</code>
     *         otherwise
     */
    void setAutodetectType(boolean autodetected);

    /**
     * Returns CloudFoundry application's name.
     *
     * @return application name
     */
    String getName();

    /**
     * Sets CloudFoundry application's name.
     *
     * @param name
     *         application's name
     */
    void setName(String name);

    /**
     * Returns CloudFounry application's url.
     *
     * @return application's url
     */
    String getUrl();

    /**
     * Sets CloudFoundry application's url.
     *
     * @param url
     *         application's url
     */
    void setUrl(String url);

    /**
     * Returns whether use custom url.
     *
     * @return <code>true</code> if need to use custom url, and
     *         <code>false</code> otherwise
     */
    boolean isCustomUrl();

    /**
     * Returns amount of instances.
     *
     * @return instances
     */
    String getInstances();

    /**
     * Sets amount of instances.
     *
     * @param instances
     *         amount of instances
     */
    void setInstances(String instances);

    /**
     * Returns amount of memory.
     *
     * @return memory.
     */
    String getMemory();

    /**
     * Sets amount of memory.
     *
     * @param memory
     *         amount of memory
     */
    void setMemory(String memory);

    /**
     * Returns selected server.
     *
     * @return server
     */
    String getServer();

    /**
     * Select new server.
     *
     * @param server
     */
    void setServer(String server);

    /**
     * Returns whether need to start application after create.
     *
     * @return <code>true</code> if need to start application after create, and
     *         <code>false</code> otherwise
     */
    boolean isStartAfterCreation();

    /**
     * Sets whether need to start application after create.
     *
     * @param start
     *         <code>true</code> need to start, <code>false</code>
     *         otherwise
     */
    void setStartAfterCreation(boolean start);

    /**
     * Sets whether Create button is enabled.
     *
     * @param enable
     *         <code>true</code> to enable the button, <code>false</code>
     *         to disable it
     */
    void setEnableCreateButton(boolean enable);

    /** Sets focus in the name field. */
    void focusInNameField();

    /**
     * Sets available application's types.
     *
     * @param types
     *         available types.
     */
    void setTypeValues(JsonArray<String> types);

    /**
     * Sets whether Type field is enabled.
     *
     * @param enable
     *         <code>true</code> to enable the field, <code>false</code>
     *         to disable it
     */
    void setEnableTypeField(boolean enable);

    /**
     * Sets whether Url field is enabled.
     *
     * @param enable
     *         <code>true</code> to enable the field, <code>false</code>
     *         to disable it
     */
    void setEnableUrlField(boolean enable);

    /**
     * Sets whether Memory field is enabled.
     *
     * @param enable
     *         <code>true</code> to enable the field, <code>false</code>
     *         to disable it
     */
    void setEnableMemoryField(boolean enable);

    /**
     * Sets selected item into the application type field with index.
     *
     * @param index
     *         the index of the item to be selected
     */
    void setSelectedIndexForTypeSelectItem(int index);

    /** Sets focus in the url field. */
    void focusInUrlField();

    /**
     * Sets whether Auto detect type checkitem is enabled.
     *
     * @param enable
     *         <code>true</code> to enable the checkitem, <code>false</code>
     *         to disable it
     */
    void setEnableAutodetectTypeCheckItem(boolean enable);

    /**
     * Sets the list of servers.
     *
     * @param servers
     */
    void setServerValues(JsonArray<String> servers);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}