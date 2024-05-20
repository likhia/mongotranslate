## Step to use mongotranslate in BI Connector
- Step 1 to install BI Connector in the primary node : https://www.mongodb.com/docs/bi-connector/current/tutorial/install-bi-connector-rhel/

- Step 2 to use Mongodrdl to get schema in MongoDB : https://www.mongodb.com/docs/bi-connector/current/reference/mongodrdl/.   Below is to get schema for all collections. 
```
mongodrdl --uri "mongodb://<username>:<password>@localhost:27011,localhost:27012,localhost:27013/<database name>?authSource=admin" --out "<name>.drdl"
```

- Step 3 to use Mongotranslate to translate SQL to MQL : https://www.mongodb.com/docs/bi-connector/current/reference/mongotranslate/
```
mongotranslate --query="select ledgerBalance from account_balance where earmarkAmount >  5" --schema="schema.drdl" --dbName=myDatabase
```

## run this code
URL: http://localhost:8080/translate
Set Header: 
- Content-Type : application/json
- Accept : application/json
Set Body: 
```
{
    "query" : "select ledgerBalance from account_balance where earmarkAmount >  5",
    "dbName": "myDatabase"
}
```