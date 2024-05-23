package com.translate.demo.services;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.BsonDocument;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.translate.demo.util.JsonAggregationOperation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvokeService {
    
    @Autowired
    MongoTemplate template; 

    @Autowired
    MongoClient client; 

    private String datePattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}";
    

    
    /**
     * Using MongoTemplate to run aggregation. 
     * From https://rajatgl17.github.io/blog/2020/06/24/complex-aggregation-operation-mongo-java.html
     * 
     * @param mqlStr  Converted MQL from the mongotranslate
     */
    public List<Document> invoke(String mqlStr, String collectionName) {
        mqlStr = " { \"pipeline\" : " + mqlStr + " }";
        
        Pattern p = Pattern.compile(datePattern);  // insert your pattern here
        Matcher m = p.matcher(mqlStr);
        
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            
            String newDate = mqlStr.substring(start, end) + "Z";

            mqlStr = mqlStr.substring(0, start) + newDate + mqlStr.substring(end);
        }

        System.out.println(mqlStr);   

        Document doc = Document.parse(mqlStr);
    
        //System.out.print(doc);
        List<Document> params = doc.getList("pipeline",   Document.class);
        
        List<AggregationOperation> aggOps = new ArrayList<>();
        for (Document d: params) {
            aggOps.add(new JsonAggregationOperation(d.toJson()));
        }
      
        Aggregation aggregation =  Aggregation.newAggregation(aggOps);
        AggregationResults result = template.aggregate(aggregation, collectionName, Document.class);

        if(result != null) {
            List<Document> mapResult = result.getMappedResults();
            for (Document res : mapResult) {
                System.out.println(res);
            }
            return mapResult;
        }
        return null;
    }

    /**
     * Only use find operation to execute basic find MQL. 
     * 
     * @param queryStr MQL for find operation for one collection
     * @param collectionName  collection name to execute this MQL.
     */
    public List<Document> invokeFind(String queryStr, String collectionName) {
        BasicQuery query = new BasicQuery(queryStr);
        
        List<Document> result = template.find(query, Document.class, collectionName);
        for (Document doc : result) {
            System.out.println(doc);
        }
        return result;
    }

    /**
     * Use db.runCommand to execute find operation with filter MQL and sort / projection as optional.    
     * 
     * @param queryStr  filter MQL for the collection. 
     * @param sortStr  sort for the collection.  Optional.
     * @param projectStr  projection for the collection.  Optional
     * @param collectionName collection name to execute this MQL.
     */
    public List<Document> invokeFindSortProject(String queryStr, String sortStr, String projectStr, String collectionName) {
        StringBuffer commandBuffer = new StringBuffer("{ \n");
        commandBuffer.append(" \"find\" :  \"" + collectionName + "\" , \n");
        commandBuffer.append(" \"filter\" : " + queryStr  + " , \n");
        
        if(!sortStr.trim().equals("")) {
            commandBuffer.append(" \"sort\" : " + sortStr  + " , \n");    
        }
        
        if(!projectStr.trim().equals("")) {
            commandBuffer.append(" \"projection\" : " + projectStr  + " , \n");    
        }

        commandBuffer.append("}");

        System.out.println(commandBuffer.toString());

        BsonDocument bson = BsonDocument.parse(commandBuffer.toString());
        MongoDatabase db = client.getDatabase("myDatabase");
        Document doc = db.runCommand(bson);

        System.out.println("===================== Output =============================");
        System.out.println(doc);
        System.out.println("==========================================================");


        List<Document> result = ((Document) doc.get("cursor")).getList("firstBatch", Document.class);

        for (Document d : result) {
            System.out.println(d);
        }

        return result;
    }


    /**
     * To db.runCommand to execute MQL using aggregation framework. 
     * 
     * @param queryStr Converted MQL from the mongotranslate
     * @param collectionName collection name to execute this MQL.
     */
    public List<Document> invokeAggregation(String queryStr, String dbName, String collectionName) {
        
        Pattern p = Pattern.compile(datePattern);  // insert your pattern here
        Matcher m = p.matcher(queryStr);
        
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            String newDate = queryStr.substring(start, end) + "Z";
            queryStr = queryStr.substring(0, start) + newDate + queryStr.substring(end);
        }

        StringBuffer commandBuffer = new StringBuffer("{ \"aggregate\":  \"" + collectionName + "\",");
        commandBuffer.append(" \"pipeline\" : " + queryStr + " ,\n");
        commandBuffer.append(" \"cursor\" : {} ");
        commandBuffer.append("}");

        System.out.println(commandBuffer.toString());

    
        BsonDocument bson = BsonDocument.parse(commandBuffer.toString());
        MongoDatabase db = client.getDatabase(dbName);
        Document doc  = db.runCommand(bson);

        System.out.println("===================== Output =============================");
        System.out.println(doc);
        System.out.println("==========================================================");

        List<Document> result = ((Document) doc.get("cursor")).getList("firstBatch", Document.class);

        for (Document d : result) {
            System.out.println(d);
        }

        return result;
    }
}
