package com.gainsight.bigdata.rulesengine.pojo.setupaction;

import com.gainsight.bigdata.rulesengine.pojo.enums.ActionType;
import com.google.gson.JsonObject;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vmenon on 9/13/2015.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class RuleAction {

    @JsonDeserialize(using = ActionTypeDeserializer.class )
    private ActionType actionType;
    private boolean upsert;
    private JsonNode action ;
    private List<Criteria> criterias = new ArrayList<>();
    

    public boolean isUpsert() {
		return upsert;
	}

	public void setUpsert(boolean upsert) {
		this.upsert = upsert;
	}

	public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public JsonNode getAction() {
        return action;
    }

    public void setAction(JsonNode action) {
        this.action = action;
    }

    public List<Criteria> getCriterias() {
        return criterias;
    }

    public void setCriterias(List<Criteria> criterias) {
        this.criterias = criterias;
    }

    public static class ActionTypeDeserializer extends JsonDeserializer<ActionType> {

        @Override
        public ActionType deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
            final String jsonValue = parser.getText();
            for (final ActionType enumValue : ActionType.values())
            {
                if (enumValue.getType().equals(jsonValue))
                {
                    return enumValue;
                }
            }
            return null;
        }
    }

}
