package com.gainsight.bigdata.rulesengine.pojo.enums;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

/**
 * Created by vmenon on 9/23/2015.
 * An enum to represent the calculation type
 */
public enum CalculationType {
    COMPARISON("Comparison"), AGGREGATION("Aggregation"), SHOW_FIELD("Show Field");

    private String type;

    private CalculationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Deserializer class for Calculation type
     */
    public static class CalculationTypeDeserializer extends JsonDeserializer<CalculationType> {

        @Override
        public CalculationType deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
            final String jsonValue = parser.getText();
            for (final CalculationType enumValue : CalculationType.values())
            {
                if (enumValue.getType().equalsIgnoreCase(jsonValue))
                {
                    return enumValue;
                }
            }
            return null;
        }
    }
}
