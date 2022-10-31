package com.example.sm.common.decorator;


import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;


public class CustomAggregationOperation implements AggregationOperation {

    private Document document;
    public CustomAggregationOperation(Document document) {
        this.document = document;
    }

    @Override
    public Document toDocument(AggregationOperationContext context) {
        return context.getMappedObject(document);
    }

    public static String getJson(JSONObject aggregationMap, String key, Object object) throws JSONException {
        String json = aggregationMap.getJSONObject(key).toString();
        //System.out.println("[getJson] : "+json);
        return new TemplateParser<>().compileTemplate(json,object);
    }

}
