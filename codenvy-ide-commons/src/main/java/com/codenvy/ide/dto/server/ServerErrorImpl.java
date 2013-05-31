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
package com.codenvy.ide.dto.server;

import com.codenvy.ide.dto.shared.RoutableDto;
import com.codenvy.ide.dto.shared.ServerError;
import com.codenvy.ide.dto.shared.RoutableDto;
import com.codenvy.ide.dto.shared.ServerError;
import com.google.gson.*;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ServerErrorImpl extends RoutableDtoServerImpl implements ServerError, JsonSerializable {

    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    protected ServerErrorImpl() {
        super(RoutableDto.SERVER_ERROR);
    }

    protected ServerErrorImpl(int type) {
        super(type);
    }

    public static ServerErrorImpl make() {
        return new ServerErrorImpl();
    }

    protected String details;

    private boolean _hasDetails;

    protected FailureReason failureReason;

    private boolean _hasFailureReason;

    public boolean hasDetails() {
        return _hasDetails;
    }

    @Override
    public String getDetails() {
        return details;
    }

    public ServerErrorImpl setDetails(String v) {
        _hasDetails = true;
        details = v;
        return this;
    }

    public boolean hasFailureReason() {
        return _hasFailureReason;
    }

    @Override
    public FailureReason getFailureReason() {
        return failureReason;
    }

    public ServerErrorImpl setFailureReason(FailureReason v) {
        _hasFailureReason = true;
        failureReason = v;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof ServerErrorImpl)) {
            return false;
        }
        ServerErrorImpl other = (ServerErrorImpl)o;
        if (this._hasDetails != other._hasDetails) {
            return false;
        }
        if (this._hasDetails) {
            if (!this.details.equals(other.details)) {
                return false;
            }
        }
        if (this._hasFailureReason != other._hasFailureReason) {
            return false;
        }
        if (this._hasFailureReason) {
            if (!this.failureReason.equals(other.failureReason)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31 + (_hasDetails ? details.hashCode() : 0);
        hash = hash * 31 + (_hasFailureReason ? failureReason.hashCode() : 0);
        return hash;
    }

    @Override
    public JsonElement toJsonElement() {
        JsonObject result = new JsonObject();

        JsonElement detailsOut = (details == null) ? JsonNull.INSTANCE : new JsonPrimitive(details);
        result.add("details", detailsOut);

        JsonElement failureReasonOut = (failureReason == null) ? JsonNull.INSTANCE : new JsonPrimitive(
                failureReason.name());
        result.add("failureReason", failureReasonOut);
        result.add("_type", new JsonPrimitive(getType()));
        return result;
    }

    @Override
    public String toJson() {
        return gson.toJson(toJsonElement());
    }

    @Override
    public String toString() {
        return toJson();
    }

    public static ServerErrorImpl fromJsonElement(JsonElement jsonElem) {
        if (jsonElem == null || jsonElem.isJsonNull()) {
            return null;
        }

        ServerErrorImpl dto = new ServerErrorImpl();
        JsonObject json = jsonElem.getAsJsonObject();

        if (json.has("details")) {
            JsonElement detailsIn = json.get("details");
            String detailsOut = gson.fromJson(detailsIn, String.class);
            dto.setDetails(detailsOut);
        }

        if (json.has("failureReason")) {
            JsonElement failureReasonIn = json.get("failureReason");
            FailureReason failureReasonOut = gson.fromJson(failureReasonIn, FailureReason.class);
            dto.setFailureReason(failureReasonOut);
        }

        return dto;
    }

    public static ServerErrorImpl fromJsonString(String jsonString) {
        if (jsonString == null) {
            return null;
        }

        return fromJsonElement(new JsonParser().parse(jsonString));
    }
}