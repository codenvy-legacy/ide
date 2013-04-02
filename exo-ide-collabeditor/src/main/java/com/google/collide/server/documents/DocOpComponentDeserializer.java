// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.collide.server.documents;

import com.google.collide.dto.DocOpComponent.Delete;
import com.google.collide.dto.DocOpComponent.Insert;
import com.google.collide.dto.DocOpComponent.Retain;
import com.google.collide.dto.DocOpComponent.RetainLine;
import com.google.collide.dto.server.DtoServerImpls.DocOpComponentImpl;
import com.google.collide.dto.server.ServerDocOpFactory;
import com.google.gson.*;

import java.lang.reflect.Type;

import static com.google.collide.dto.DocOpComponent.Type.*;

/*
 * TODO(jasonparekh): consider creating custom deserializers for subclasses as a
 * feature in our DTO generator, or at minimum constants for the JSON property
 * keys.
 */

/**
 * Custom {@link com.google.gson.Gson} deserializer for {@link com.google.collide.dto.DocOpComponent} subclasses.
 *
 * @author jasonparekh@google.com (Jason Parekh)
 */
public class DocOpComponentDeserializer
        implements
        JsonDeserializer<DocOpComponentImpl>,
        JsonSerializer<DocOpComponentImpl> {

    @Override
    public DocOpComponentImpl deserialize(JsonElement json, Type typeOfT,
                                          JsonDeserializationContext context) throws JsonParseException {

        JsonObject componentJo = json.getAsJsonObject();

        switch (componentJo.get("type").getAsInt()) {
            case DELETE:
                return (DocOpComponentImpl)ServerDocOpFactory.INSTANCE.createDelete(componentJo
                                                                                            .get("text")
                                                                                            .getAsString());

            case INSERT:
                return (DocOpComponentImpl)ServerDocOpFactory.INSTANCE.createInsert(componentJo
                                                                                            .get("text")
                                                                                            .getAsString());

            case RETAIN:
                return (DocOpComponentImpl)ServerDocOpFactory.INSTANCE.createRetain(
                        componentJo.get("count").getAsInt(),
                        componentJo.get("hasTrailingNewline").getAsBoolean());

            case RETAIN_LINE:
                return (DocOpComponentImpl)ServerDocOpFactory.INSTANCE.createRetainLine(componentJo.get(
                        "lineCount").getAsInt());

            default:
                throw new IllegalArgumentException("Could not deserialize DocOpComponent: "
                                                   + componentJo);
        }
    }

    @Override
    public JsonElement serialize(DocOpComponentImpl component, Type typeOfSrc,
                                 JsonSerializationContext context) {

        JsonObject componentJo = new JsonObject();
        componentJo.addProperty("type", component.getType());

        switch (component.getType()) {
            case DELETE:
                componentJo.addProperty("text", ((Delete)component).getText());
                break;

            case INSERT:
                componentJo.addProperty("text", ((Insert)component).getText());
                break;

            case RETAIN:
                componentJo.addProperty("count", ((Retain)component).getCount());
                componentJo.addProperty("hasTrailingNewline", ((Retain)component).hasTrailingNewline());
                break;

            case RETAIN_LINE:
                componentJo.addProperty("lineCount", ((RetainLine)component).getLineCount());
                break;

            default:
                throw new IllegalArgumentException("Could not serialize DocOpComponent: " + component);
        }

        return componentJo;
    }
}