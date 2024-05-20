## Step to use mongotranslate in BI Connector
- Step 1 to install BI Connector in the primary node : https://www.mongodb.com/docs/bi-connector/current/tutorial/install-bi-connector-rhel/

- Step 2 to use Mongodrdl to get schema in MongoDB : https://www.mongodb.com/docs/bi-connector/current/reference/mongodrdl/.   Below is to get schema for all collections. 
```
mongodrdl --uri "mongodb://<username>:<password>@localhost:27011,localhost:27012,localhost:27013/<database name>?authSource=admin" --out "<name>.drdl"
```
Put the generated drdl in the `resources` folder of this project. 

- Step 3 to use Mongotranslate to translate SQL to MQL : https://www.mongodb.com/docs/bi-connector/current/reference/mongotranslate/
```
mongotranslate --query="select ledgerBalance from account_balance where earmarkAmount >  5" --schema="schema.drdl" --dbName=myDatabase
```

## Run /translate
Use mongotranslate to translate SQL to MQL and use MongoTemplate to run aggregation. 
- URL: http://localhost:8080/translate
- Set Header: 
   - Content-Type : application/json
   - Accept : application/json
- Set Body: 
```
{
    "query" : "select ledgerBalance from account_balance where earmarkAmount >  5",
    "dbName": "myDatabase"
}
```

## Run /invokefindsortproject
Use db.runCommand to execute find operation with filter MQL and sort / projection as optional.  
- URL: http://localhost:8080/invokefindsortproject
- Set Header: 
   - Content-Type : application/json
   - Accept : application/json
- Set Body: 
```
{
    "query" : "{ earmarkAmount : { $gt : 5}}",
    "sort" : "{ earmarkAmount : -1}",
    "projection" : "{ accountNumber : 1, productCode : 1, earmarkAmount : 1, _id : 0}",
    "collectionName": "account_balance"
}
```

## Run /invokefind
Only use find operation to execute basic find MQL. 
- URL: http://localhost:8080/invokefind
- Set Header: 
   - Content-Type : application/json
   - Accept : application/json
- Set Body: 
```
{
    "query" : "{ _id : ObjectId('660feec3ef2d9b4d723a94c6')}",
    "collectionName": "account_balance"
}
```

## Run /invokeagg
To db.runCommand to execute MQL using aggregation framework. 
- URL: http://localhost:8080/invokeagg
- Set Header: 
   - Content-Type : application/json
   - Accept : application/json
- Set Body: 
```
{
    "query" : "select ledgerBalance from account_balance where earmarkAmount >  5",
    "dbName": "myDatabase"
}
```