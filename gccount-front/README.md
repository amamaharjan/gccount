<b>CASHLESS PAYMENT SYSTEM</b>

[Run UnitTestCases](http://stackoverflow.com/a/2219029/432903)
==============
`$ grails test-app unit: TransactionControllerTests.testMethodName -echoOut`

[Run spock test cases](http://grails101.wordpress.com/2012/04/22/test-grails-application-with-spock/)
=====================
`$ grails test-app unit:spock CategorySpec -echoOut`


degug-app
=========

```
$ grails-debug run-app
```

run-app
=======

```
$ grails -Dserver.port=8080 run-app
```

or
```
$ bash run-app.sh
```

es report analytics
==============
hit `http://localhost:8080/gccount/transaction/transactionAnalytics?indexName=gccount&reportName=transaction`


<b>DEMO</b>

![Image Alt](https://github.com/iPrayag/gccount/gccount-front/raw/master/doc/main.png)





