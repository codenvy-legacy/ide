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


// GENERATED SOURCE. DO NOT EDIT.
package com.codenvy.ide.dto.client;


@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

    private DtoClientImpls() {
    }

    public static final String CLIENT_SERVER_PROTOCOL_HASH = "2c2c48ff26a650fa8969d5b56dbdbe58ca9b6505";


    public static class DocOpImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.dto.DocOp {
        protected DocOpImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.dto.DocOpComponent> getComponents() /*-{
            return this["components"];
        }-*/;

        public final native DocOpImpl setComponents(com.codenvy.ide.json.JsonArray<com.codenvy.ide.dto.DocOpComponent> components) /*-{
            this["components"] = components;
            return this;
        }-*/;

        public final native boolean hasComponents() /*-{
            return this.hasOwnProperty("components");
        }-*/;

        public static native DocOpImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class DeleteImpl extends DocOpComponentImpl implements com.codenvy.ide.dto.DocOpComponent.Delete {
        protected DeleteImpl() {
        }

        @Override
        public final native java.lang.String getText() /*-{
            return this["text"];
        }-*/;

        public final native DeleteImpl setText(java.lang.String text) /*-{
            this["text"] = text;
            return this;
        }-*/;

        public final native boolean hasText() /*-{
            return this.hasOwnProperty("text");
        }-*/;

        public static native DeleteImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class InsertImpl extends DocOpComponentImpl implements com.codenvy.ide.dto.DocOpComponent.Insert {
        protected InsertImpl() {
        }

        @Override
        public final native java.lang.String getText() /*-{
            return this["text"];
        }-*/;

        public final native InsertImpl setText(java.lang.String text) /*-{
            this["text"] = text;
            return this;
        }-*/;

        public final native boolean hasText() /*-{
            return this.hasOwnProperty("text");
        }-*/;

        public static native InsertImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class RetainImpl extends DocOpComponentImpl implements com.codenvy.ide.dto.DocOpComponent.Retain {
        protected RetainImpl() {
        }

        @Override
        public final native boolean hasTrailingNewline() /*-{
            return this["hasTrailingNewline"];
        }-*/;

        public final native RetainImpl setHasTrailingNewline(boolean hasTrailingNewline) /*-{
            this["hasTrailingNewline"] = hasTrailingNewline;
            return this;
        }-*/;

        public final native boolean hasHasTrailingNewline() /*-{
            return this.hasOwnProperty("hasTrailingNewline");
        }-*/;

        @Override
        public final native int getCount() /*-{
            return this["count"];
        }-*/;

        public final native RetainImpl setCount(int count) /*-{
            this["count"] = count;
            return this;
        }-*/;

        public final native boolean hasCount() /*-{
            return this.hasOwnProperty("count");
        }-*/;

        public static native RetainImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class RetainLineImpl extends DocOpComponentImpl implements com.codenvy.ide.dto.DocOpComponent.RetainLine {
        protected RetainLineImpl() {
        }

        @Override
        public final native int getLineCount() /*-{
            return this["lineCount"];
        }-*/;

        public final native RetainLineImpl setLineCount(int lineCount) /*-{
            this["lineCount"] = lineCount;
            return this;
        }-*/;

        public final native boolean hasLineCount() /*-{
            return this.hasOwnProperty("lineCount");
        }-*/;

        public static native RetainLineImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class DocOpComponentImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.dto.DocOpComponent {
        protected DocOpComponentImpl() {
        }

        @Override
        public final native int getType() /*-{
            return this["type"];
        }-*/;

        public final native DocOpComponentImpl setType(int type) /*-{
            this["type"] = type;
            return this;
        }-*/;

        public final native boolean hasType() /*-{
            return this.hasOwnProperty("type");
        }-*/;

        public static native DocOpComponentImpl make() /*-{
            return {

            };
        }-*/;
    }

}