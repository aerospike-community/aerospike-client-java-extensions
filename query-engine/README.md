Aerospike Query Engine
======================

### This project is obsolete
The QueryEngine was previously used by Spring Data for Aerospike and Aerospike Connect for Spark. It has been replaced in those projects with direct access to the [predicate filter](https://www.aerospike.com/docs/guide/predicate.html) class [PredExp](https://www.aerospike.com/apidocs/java/com/aerospike/client/query/PredExp.html).

This is a deprecation notice. Please migrate your code, as this extension will be removed at a future date. Improvements to PredExp, Scan and Query are released regularly, and you should be leveraging them. 


###  Historical
This project contains the files necessary to build the Aerospike Query Engine library. 
Aerospike Query Engine will automatically choose an index if one is available to qualify
the results, and then use PredExp further qualify the results.

Query Engine uses a `Statement` and zero or more `Qualifier` objects and produces a closable `KeyRecordIterator` to iterate over the results of the query.

Java Package `com.aerospike.helper.query`

[Documentation](doc/query.md)

### Build

The source code can be imported into your IDE and/or built using Maven.

    mvn install 

### Test    

Tests are disabled by default.  To run tests, edit host arguments in 
src/test/java/com/aerospike/helper/query/TestQueryEngine.java and execute
script: 

    ./run_tests
