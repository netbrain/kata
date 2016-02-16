Rental-FUN (Exercise)
----------

Is a small partially implemented API for a rental video store. It was supposed to be developed with an existing framework for creating  REST API endpoints, such as dropwizard. However I have used dropwizard for the last couple of projects and really didn't feel like using something that familiar. So instead, i rolled my own microframework with a complete test harness to cover this application, just for the fun of it.

So let's take a look at the microframework first i have named "nicrofw", which is a horrendus name, please forgive me.  It uses the lightweight  [NanoHTTPD](https://github.com/NanoHttpd/nanohttpd) as the embedded http server and [Boon](https://github.com/boonproject/boon) for JSON.

So the main responsibility of "nicrofw" is to listen for HTTP requests, peek at the URL and map them to some business logic. Currently the framework have three main classes:


**Router**
Which keeps track of all the routes defined in the system and has some logic for scanning the classpath for java classes in order to look for @Endpoint annotations.

**UriMap**
Which links a given url (/example/:id) to a handler method.

**EndpointAnnotatedHandler**
Which does all the legwork in passing request data and path parameters to the handler method  which has been annotated with the @Endpoint annotation and additionally it handles the conversion from raw request/response with a JSON ObjectMapper defined through a ClassFactory.

## The application ##

The application Rental-FUN exposes a simple (minimal) REST-ish API and uses JSON for both request and response data. Additionally it assumes that authentication and authorization is handled somewhere else, and that the header HTTP header "X-Customer" is the currently logged in user. The application is missing proper access restrictions, data validation mechanisms and pretty error handling. If you try to make an invalid API call you might get an exception back in the response, this is intended.

The solution uses a "roll your own" in-memory persistence solution, so if you restart the application, the data will be reset and any changes lost. The persistence solution supports optimistic locking, so any data races will result in the first request to succeed while the last will fail with an optimistic locking exception.

The following is the exposed API:

    [GET] /customers
    [GET] /customers/:id
    [GET] /films
    [GET] /films/:id
    [GET] /rentals
    [GET] /rentals/:id
    [POST] /rentals
    [DELETE] /rentals/:id


Invoking any non-existent API endpoints will result in a 400 Bad Request text/plain response with a message detailing that the endpoint doesn't exist and gives a list of the available endpoints.

 **[GET] /customers**
 **[GET] /customers/:id**

 Returns either a list of customers or a single customer. A customer payload looks like this:


    $ curl -s 127.0.0.1:8080/customers/0 | prettyj
    {
        "bonus": 0,
        "created": "2016-02-15T20:17:05.160Z",
        "id": 0,
        "lastUpdated": "2016-02-15T20:17:05.160Z",
        "username": "john.smith",
        "version": 0
    }

**[GET] /films**
**[GET] /films/:id**

Returns either a list of films or a single film. A film payload looks like this:

    $ curl -s 127.0.0.1:8080/films/0 | prettyj
    {
        "created": "2016-02-15T20:17:05.210Z",
        "id": 0,
        "lastUpdated": "2016-02-15T20:17:05.210Z",
        "rented": false,
        "title": "Matrix 11",
        "type": "NEW",
        "version": 0
    }

**[GET] /rentals**
**[GET] /rentals/:id**
Returns either a list of rentals or a single rental. A rental is an object detailing which films are out on rent, by which customer. for how many days and the total price. A rental payload looks like this:

    $ curl -s 127.0.0.1:8080/rentals/0 | prettyj
    {
        "created": "2016-02-10T00:00:00.000Z",
        "customerId": 0,
        "films": [
            0,
            1
        ],
        "id": 0,
        "lastUpdated": "2016-02-15T20:17:05.250Z",
        "numDays": 3,
        "price": 150,
        "surcharge": null,
        "version": 0
    }


**[POST] /rentals**
This endpoint is used when a customer wants to rent one or more films for a given duration. This will create a new rental and use the X-Customer header to assign it to the customer. This will reserve the requested films if possible and add bonus points to the customer.

    $ curl -v -s -H "X-Customer: 0" -H "Content-Type: application/json" -X POST -d '{"films":[2,3], "numDays": 3}' 127.0.0.1:8080/rentals | prettyj

    > POST /rentals HTTP/1.1
    > User-Agent: curl/7.38.0
    > Host: 127.0.0.1:8080
    > Accept: */*
    > X-Customer: 0
    > Content-Type: application/json
    > Content-Length: 29
    >

    < HTTP/1.1 200 OK
    < Content-Type: application/json
    < Date: Mon, 15 Feb 2016 20:48:40 GMT
    < Connection: keep-alive
    < Content-Length: 167

    {
        "created": "2016-02-15T20:48:40.327Z",
        "customerId": 0,
        "films": [
            2,
            3
        ],
        "id": 1,
        "lastUpdated": "2016-02-15T20:48:40.327Z",
        "numDays": 3,
        "price": 60,
        "surcharge": null,
        "version": 0
    }


**[DELETE] /rentals/:id**
This endpoint is intended to be used for a customer returning movies rented. This doesn't actually delete the rental object, instead it updates it with surcharge data which details if the customer has to pay additional fees for being overdue. Additionally it releases the rented movies back into availability for other customers.

    $ curl -v -s -H "X-Customer: 0" -H "Content-Type: application/json" -X DELETE 127.0.0.1:8080/rentals/0 | prettyj

    > DELETE /rentals/0 HTTP/1.1
    > User-Agent: curl/7.38.0
    > Host: 127.0.0.1:8080
    > Accept: */*
    > X-Customer: 0
    > Content-Type: application/json

    < HTTP/1.1 200 OK
    < Content-Type: application/json
    < Date: Mon, 15 Feb 2016 20:51:39 GMT
    < Connection: keep-alive
    < Content-Length: 192

    {
        "created": "2016-02-10T00:00:00.000Z",
        "customerId": 0,
        "films": [
            0,
            1
        ],
        "id": 0,
        "lastUpdated": "2016-02-15T20:51:39.716Z",
        "numDays": 3,
        "price": 150,
        "surcharge": {
            "amount": 110,
            "extraDays": 2
        },
        "version": 1
    }

## Running the application ##

###The docker way###

    $ docker build -t rentalfun . && docker run -it --rm -p 8080:8080 rentalfun

### The docker-compose way ###

    $ docker-compose up

###The java/maven way###

    $ mvn exec:java -Dexec.mainClass="io.github.netbrain.rentalfun.App"

## Additional notes ##

Please keep in mind that the framework code is highly experimental and created only for this applications needs.

A minimalistic framework like this would be perfect in a microcontainer environment (if it weren't for java's huge memory footprint). Given the requirement to run this as a microcontainer I would preferably implemented this in golang instead.

