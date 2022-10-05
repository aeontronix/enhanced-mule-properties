package com.aeontronix.enhancedmule.propertiesprovider.property;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class ConfigPropertyDeserializer extends JsonDeserializer<ConfigProperty> {
    private JsonMapper objectMapper;

    public ConfigPropertyDeserializer(JsonMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConfigProperty deserialize(JsonParser jp, DeserializationContext context) throws IOException, JsonProcessingException {
        final TreeNode json = jp.readValueAsTree();
        if (json instanceof NullNode || json == null) {
            return null;
        } else if (json instanceof ObjectNode) {
            final JsonNode type = ((ObjectNode) json).get("type");
            if (type == null || type.isNull()) {
                return objectMapper.treeToValue(json, TextConfigProperty.class);
            } else {
                final String propType = type.textValue().toLowerCase();
                switch (propType) {
                    case "group":
                        return objectMapper.treeToValue(json, GroupProperty.class);
                    case "text":
                        return objectMapper.treeToValue(json, TextConfigProperty.class);
                    case "url":
                        return objectMapper.treeToValue(json, URLConfigProperty.class);
                    case "file":
                        return objectMapper.treeToValue(json, FileConfigProperty.class);
                    case "sscert":
                        return objectMapper.treeToValue(json, SelfSignedCertConfigProperty.class);
                    case "https":
                        return objectMapper.treeToValue(json, HttpsConfigProperty.class);
                    default:
                        throw new JsonMappingException(jp, "Invalid property type: " + propType);
                }
            }
        } else {
            throw new JsonMappingException(jp, "Invalid PropertyValue: " + json);
        }
    }
}
