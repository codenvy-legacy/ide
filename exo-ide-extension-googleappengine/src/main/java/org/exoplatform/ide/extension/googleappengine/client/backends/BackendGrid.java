/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.googleappengine.client.backends;

import com.google.gwt.cell.client.*;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.model.Backend;
import org.exoplatform.ide.extension.googleappengine.client.model.State;

/**
 * Grid for displaying list of backends.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 29, 2012 3:07:12 PM anya $
 */
public class BackendGrid extends ListGrid<Backend> implements HasBackendActions {
    private final String ID = "ideBackendGrid";

    /** Name column title. */
    private final String NAME = GoogleAppEngineExtension.GAE_LOCALIZATION.backendNameTitle();

    /** State column title. */
    private final String STATE = GoogleAppEngineExtension.GAE_LOCALIZATION.backendStateTitle();

    /** Class column title. */
    private final String CLASS = GoogleAppEngineExtension.GAE_LOCALIZATION.backendClassTitle();

    /** Number of instances column title. */
    private final String INSTANCES = GoogleAppEngineExtension.GAE_LOCALIZATION.backendInstancesTitle();

    /** Dynamic option column title. */
    private final String DYNAMIC = GoogleAppEngineExtension.GAE_LOCALIZATION.backendDynamicTitle();

    /** Public option column title. */
    private final String PUBLIC = GoogleAppEngineExtension.GAE_LOCALIZATION.backendPublicTitle();

    /** State column. */
    private Column<Backend, String> stateColumn;

    public BackendGrid() {
        super();
        setID(ID);

        Column<Backend, String> nameColumn = new Column<Backend, String>(new TextCell()) {
            @Override
            public String getValue(Backend backend) {
                return backend.getName();
            }
        };

        stateColumn = new Column<Backend, String>(new ButtonCell()) {

            @Override
            public String getValue(Backend object) {
                return State.START.equals(object.getState()) ? "Stop" : "Start";
            }
        };

        Column<Backend, String> classColumn = new Column<Backend, String>(new TextCell()) {
            @Override
            public String getValue(Backend backend) {
                return backend.getInstanceClass();
            }
        };

        Column<Backend, Number> instancesColumn = new Column<Backend, Number>(new NumberCell()) {

            @Override
            public Number getValue(Backend object) {
                return object.getInstances();
            }
        };

        Column<Backend, SafeHtml> dynamicColumn = new Column<Backend, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final Backend object) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        if (object.isDynamic()) {
                            return "<input type=\"checkbox\" checked=checked readonly=\"true\"/>";
                        } else {
                            return "<input type=\"checkbox\" readonly=\"true\"/>";
                        }
                    }
                };
                return html;
            }
        };

        Column<Backend, SafeHtml> publicColumn = new Column<Backend, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final Backend object) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        if (object.isPublic()) {
                            return "<input type=\"checkbox\" checked=checked readonly=\"true\"/>";
                        } else {
                            return "<input type=\"checkbox\" readonly=\"true\"/>";
                        }
                    }
                };
                return html;
            }
        };

        getCellTable().addColumn(nameColumn, NAME);
        getCellTable().setColumnWidth(nameColumn, "25%");

        getCellTable().addColumn(stateColumn, STATE);
        getCellTable().setColumnWidth(stateColumn, "15%");
        stateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        getCellTable().addColumn(classColumn, CLASS);
        getCellTable().setColumnWidth(classColumn, "15%");
        classColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        getCellTable().addColumn(instancesColumn, INSTANCES);
        getCellTable().setColumnWidth(instancesColumn, "15%");
        instancesColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        getCellTable().addColumn(dynamicColumn, DYNAMIC);
        getCellTable().setColumnWidth(dynamicColumn, "15%");
        dynamicColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        getCellTable().addColumn(publicColumn, PUBLIC);
        getCellTable().setColumnWidth(publicColumn, "15%");
        publicColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    }

    private class SelectionEventImpl extends SelectionEvent<Backend> {
        /** @param selectedItem */
        protected SelectionEventImpl(Backend selectedItem) {
            super(selectedItem);
        }
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.backends.HasBackendActions#addChangeStateHandler(com.google.gwt.event
     * .logical.shared.SelectionHandler) */
    @Override
    public void addChangeStateHandler(final SelectionHandler<Backend> handler) {
        stateColumn.setFieldUpdater(new FieldUpdater<Backend, String>() {
            @Override
            public void update(int index, Backend object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }
}
