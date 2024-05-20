/**
 * From https://rajatgl17.github.io/blog/2020/06/24/complex-aggregation-operation-mongo-java.html
 */
package com.translate.demo.util;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;


public class JsonAggregationOperation implements AggregationOperation {
    private String jsonOperation;

     public JsonAggregationOperation(String jsonOperation) {
     this.jsonOperation = jsonOperation;
     }

     @Override
     public Document toDocument(AggregationOperationContext context) {
     return context.getMappedObject(Document.parse(jsonOperation));
     }
    
}
