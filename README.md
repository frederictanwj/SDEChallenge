# PaytmLabs SDE Challenge

## Coding Question

Write an interface for a data structure that can provide the moving average of the last N elements added, add elements to the structure and get access to the elements. Provide an efficient implementation of the interface for the data structure.

### Minimum Requirements

1. Provide a separate interface (IE `interface`/`trait`) with documentation for the data structure
2. Provide an implementation for the interface
3. Provide any additional explanation about the interface and implementation in a README file.

## Design Question

Design A Google Analytic like Backend System.
We need to provide Google Analytic like services to our customers. Please provide a high level solution design for the backend system. Feel free to choose any open source tools as you want.

### Requirements

1. Handle large write volume: Billions of write events per day.
2. Handle large read/query volume: Millions of merchants wish to gain insight into their business. Read/Query patterns are time-series related metrics.
3. Provide metrics to customers with at most one hour delay.
4. Run with minimum downtime.
5. Have the ability to reprocess historical data in case of bugs in the processing logic.


## Coding Answer

1. Created a SimpleQueue trait to represent a queue with the ability to add and query elements.
2. Created a MovingAverage trait to represent the ability to calculate the moving average of last N elements.
3. Worked out a MovingAverageQueue implementation to implement above 2 traits. It is immutable and thread safe.


## Design Answer

### To design a Google Analyst like system, I will divide it into multiple aspects.

1. From the business requirement aspect. What features does the system have.<br>
   I will separate different features into their own microservices. We can decouple the features from code level by the microservices.<br>
   Different teams can take up different microservices, we can decouple the features from development perspective.

2. The analyst like system requires a lot of data. From the system input aspect, how to feed high volume data efficiently.<br>
   I will prefer to use Kafka or Pulsar, as they are high performance message system. If we write our own web services to receive data, then if our system crash, the upstream system can not send data to us. It will have to wait until our system recovers.<br>
   As Kafka/Pulsar are standalone system, we will not lose the message. Once our system restarts, we can continue to receive message.

3. Because it is a write heavy system, I will consider using a NoSql database rather the traditional RDBMS, because transaction impacts the performance.<br>
   I will prefer using Cassandra, because it has the best performance in the insertion scenario comparing other NoSql DB.

4. It is also a read heavy system, we have to avoid using join from RDBMS(We are not as we are using Cassandra). Because Cassandra is a wide column database, it can not represent data relationship. But in reality many customers data have relationship internally. Lke the shops-customers relationship, and the products-customers relationship.<br>
   I think MongoDB is a good choice. because it is a document store database. Take the example of customers-products relationship, we can store the products as a json array, into the customers table(called collection in MongoDB).<br>
   Then we can use just 1 query to get both the customers and the products they bought from database without join operation.

5. We need to transform the data from Cassandra into MongoDB. So I will use an ETL tool like Spark to do the data transformation.<br>
   If we only allow 1 hour delay, then Spark streaming should be the choice.<br>
   In case of error to reprocess the data, we can use Spark as batch to redo the data transformation.

6. To minimum system downtime, we can run different microservices into different nodes/VMs/machines, then the failed or crashed micro-service will not impact others. Basically we will use Docker + Kubernete to do that. 
