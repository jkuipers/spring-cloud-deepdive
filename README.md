Instructions for running the demo
=================================

The demo requires a running [Consul](https://www.consul.io/) instance.
On Windows I'm starting Consul like this, other platforms should be similar 
(make sure the data directory exists):

`consul agent -server -bootstrap -advertise=127.0.0.1 -data-dir=c:\data\Consul\data -ui`

The demo expects certain configuration entries to be available in Consul.
You can import the keys from the `consul.keys` file in the root of this project as follows
once Consul is up and running:

`consul kv import @consul.keys`

The demo also requires a running [RabbitMQ](http://www.rabbitmq.com/) instance: if you don't care 
about the messaging parts, simply do not start the `ReviewApp` and skip the last section.

## The Talk service

After that, you can start the `TalkApp` application. It should get its configuration from Consul
and register itself as the `talk-service` with Consul: you can check this in the [Consul UI](http://localhost:8500/ui).

You can also look at the service's [/actuator/env](http://localhost:8881/actuator/env) endpoint to see that there are
two Consul-based property-sources: one for all applications, and one specifically for the service.

Try changing a value and then notice how the change will be picked up by the service by checking its console logging.

You can start another talk service instance by passing a command line argument `--server.port=8891` on startup.

## The Conference web application

Now first start the `ReviewApp` and then the `ConferenceApp`. Then navigate to [http://localhost:8080/]() to see the application.
If you started two instances of the talk service, then by refreshing the browser you'll see the client-side
load balancing in action: every other request will be routed to another service instance.

To understand what happens here, have a look at the `TalkService` class: it uses a `RestTemplate` qualified with 
`@LoadBalanced`. Also note that the main class (`ConferenceApp`) is annotated with `@SpringCloudApplication`, which
means that service discovery is enabled. These two things together allow us to make requests to a virtual URL
`http://talk-service/talks`. The `talk-service` here is not a host name: it's actually a service name that is resolved
against Consul, which returns both instances of that service, and then Netflix [Ribbon](https://github.com/Netflix/ribbon)
is used to load-balance the requests. 

Consul will use the `/health` endpoint of your services to determine if the service is healthy. If you stop one of the talk-service
instances, then the Conference web application won't know about the service instance going down immediately: for a couple of seconds
it will keep trying trying to call that service, and only after a while will it discover that that instance is no longer there.
Still, the application will keep on working: how is that possible?

## Hystrix and fallbacks

If you inspect the `TalkService`, you'll notice that its `allTalks` method is annotated with `@HystrixCommand(fallbackMethod = "cachedTalks")`.
This means that if the method throws an exception, the `cachedTalks` method will be called instead. This allows the
Conference application to deal with temporary failures of a talk-service instance. 
You can see for yourself if the application is using the service output or the fallback by checking the logging output.

## Hystrix dashboard and bulk heading support

Now start the `HystrixDashboardApp` application and navigate to [http://localhost:9000/hystrix/monitor?stream=http%3A%2F%2Flocalhost%3A8080%2Factuator%2Fhystrix.stream]().
This will show you the state of the two circuitbreakers and their thread pools present in the Conference application.

To create some load on the application, you can use [Apache Bench](https://httpd.apache.org/docs/2.4/programs/ab.html), which is included
with the Apache web server. This command will perform 1000 requests using 9 threads:

`ab -n 1000 -c 9 http://localhost:8080/`

Execute this and watch the Hystrix dashboard: you'll see that the number of requests per second increases, but that
everything works as expected. The log output should confirm this.

Now run the same thing with 15 threads:

`ab -n 1000 -c 15 http://localhost:8080/`

This you should see failures in the dashboard (the circuit will probably open): what you're seeing here is Hystrix's
[bulkhead](https://github.com/Netflix/Hystrix/wiki/How-it-Works#Threads) support. 

You could increase the size of the thread pool used by adding this attribute to the `@HystrixCommand` annotation:

`threadPoolProperties = @HystrixProperty(name = "coreSize", value = "20")`

## Spring Cloud Stream for async communication

Right now none of the talks have any reviews. If you inspect the `ReviewMessageListener` class you'll see that it can 
receive reviews from some input channel. The application uses a [RabbitMQ Binder](http://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/#_rabbitmq_binder)
to map a RabbitMQ queue to this Spring Integration channel automatically. The binding configuration can be seen
in Consul and consists of two entries under the `config/review-service` section.

So, how do you publish reviews that will be handled by the review-service? 
For that, you can start the `GeneratorApp` which will also use Spring Cloud Stream with a RabbitMQ binding, but in this
case as a _publisher_ rather than a _subscriber_. Check out the code and the configuration in Consul to understand how it
works exactly. When you start the application, it will publish a randomly generated review every second. 
It can do this even if the review-service is not up and running, because RabbitMQ will buffer the messages.
Once the review-service is available it will consume these messages and store the reviews (in an in-memory database). 
You can stop the generator after it has published several reviews, and then see the results in the conference-web application. 

If you have enabled the [management plugin](https://www.rabbitmq.com/management.html) in RabbitMQ, you can use it to browse the
exchange and queue that were created from the bindings by Spring Cloud Stream. 
