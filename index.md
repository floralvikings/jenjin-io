---
layout: default
title: Jenjin-IO by floralvikings
---

# Jenjin-IO
IO Utilities for Client/Server Connections

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/floralvikings/jenjin-io/master/LICENSE)
[![Build Status](https://travis-ci.org/floralvikings/jenjin-io.svg?branch=master)](https://travis-ci.org/floralvikings/jenjin-io)
[![Coverage Status](https://coveralls.io/repos/floralvikings/jenjin-io/badge.svg?branch=master&service=github)](https://coveralls.io/github/floralvikings/jenjin-io?branch=master)

## Usage

Jenjin-IO provides a library for creating simple Socket-based connections, utilizing a customizable serialization scheme
for messages. 

 * For a client-side or peer-to-peer connection:
   * Implementing (at minimum) the [Message](javadoc/com/jenjinstudios/io/Message.html) and 
     [```ExecutionContext```](javadoc/com/jenjinstudios/io/ExecutionContext.html) interfaces, then 
     [building](javadoc/com/jenjinstudios/io/connection/SingleConnectionBuilder.html) and starting a 
     [```Connection```](javadoc/com/jenjinstudios/io/connection/Connection.html)


 * For a server that accepts Jenjin-IO client-side connections:
   * Implementing (at minimum) the [Message](javadoc/com/jenjinstudios/io/Message.html), 
     [```ExecutionContext```](javadoc/com/jenjinstudios/io/ExecutionContext.html) and 
     [```ExecutionContextFactory```](javadoc/com/jenjinstudios/io/ExecutionContextFactory.html) interfaces, creating a  
     [```MultiConnectionBuider```](javadoc/com/jenjinstudios/io/connection/MultiConnectionBuilder.html),
     [building](javadoc/com/jenjinstudios/server/ServerBuilder.html) and starting a 
     [```Server```](javadoc/com/jenjinstudios/server/Server.html)

### [ExecutionContext](javadoc/com/jenjinstudios/io/ExecutionContext.html)

This interface should be implemented by any application using the Jenjin-IO; it can contain ```Connection```-specific data
that persists across incoming and outgoing ```Messages```.  Whenever a ```Connection``` executes a ```Message```, the 
```ExecutionContext``` belonging to that ```Connection``` is passed as a parameter to the ```Message#execute``` method.

> **Important:**  In the ```Message#execute``` method and in contextual callbacks passed to a ```Connection```,
modification of an ```ExecutionContext``` is thread safe; synchronization, locking, etc... are not required.  
**However**, if the ```ExecutionContext``` is accessed from threads other than these, care should be taken to make sure
that any access to it is made safe.

#### [ExecutionContextFactory](javadoc/com/jenjinstudios/io/ExecutionContextFactory.html)

This interface should be implemented by any Server-side application using the Jenjin-IO API; it has one method 
(```createInstance```) which should return a **new**, **distinct**, and **mutable** ```ExecutionContext```.  The 
```Server``` class must be passed an implementation of this interface (typically through a ```ServerBuilder```) so that
when new connections are created, they can be passed their own ```ExecutionContext``` instance without affecting those 
of existing or further new connections.

### [Message](javadoc/com/jenjinstudios/io/Message.html)

The ```Message``` interface is core to the Jenjin-IO API; it determines what data is received by a 
```Connection``` and what is done when data is received.  It contains a single (default) method called ```execute```
which takes a single parameter (the ```ExecutionContext``` belonging to the ```Connection``` that received the message)
and returns an optional ```Message``` that is queued up to be sent by the ```Connection```.

### [Connection](javadoc/com/jenjinstudios/io/connection/Connection.html)

The ```Connection``` class is the "glue" that ties the other components of the Jenjin-IO API together.  Its main 
responsibility is to spawn and maintain the threads responsible for reading, executing, and writing ```Messages```.

When a ```Connection``` is started (by calling the ```start``` method), it automatically begins retrieving any messages 
sent to it.  When it receives a message, the following process takes place:

1. The raw data of the message is deserialized into a ```Message``` object
    * The method of deserialization depends on the ```MessageReader``` owned by the Connection; Jenjin-IO provides an
    implementation using Gson for convenience.
    
2. The deserialized ```Message``` is placed into the "incoming" queue of the ```Connection```.

3. During the next message execution cycle, the ```Connection``` invokes the ```execute``` method of all messages in the 
"inbound" queue in the order in which they were received, passing in its ```ExecutionContext``` as a parameter and 
storing any non-null return values in the "outgoing" message queue.

4. During the next message broadcast cycle, any ```Messages``` in the "outgoing" queue are serialized into raw data and
sent.

```Connections``` can either be built manually (for client-side or peer-to-peer connections) using the 
```SingleConnectionBuilder``` class, or they can be built automatically by a ```Server``` using a 
```MultiConnectionBuilder```.

#### [SingleConnectionBuilder](javadoc/com/jenjinstudios/io/connection/SingleConnectionBuilder.html)

This class is used to build a single ```Connection```; there are a few different configurations that can be done when 
building a ```Connection``` that are of interest:

* ```withExecutionContext(ExecutionContext)```
  * This method is used to pass an ```ExecutionContext``` into the ```Connection``` when it is built.

* ```withSocket(Socket)```
  * This method is used to set the ```MessageReader``` and ```MessageWriter``` from the input and output streams 
  belonging to the given socket
  * This method will throw an ```IllegalStateException``` if the ```MessageIOFactory``` has not been set
  * This method will throw an ```IllegalStateException``` if the ```MessageReader``` or ```MessageWriter``` have already
  been set

* ```withInputStream(InputStream)``` and ```withOutputStream(OutputStream)```
  * These methods is used to set the ```MessageReader``` or ```MessageWriter``` (respectively) from the given 
  ```InputStream``` or ```OutputStream```
  * These methods will throw an ```IllegalStateException``` if the ```MessageIOFactory``` has not been set
  * These methods will throw an ```IllegalStateException``` if the ```MessageReader``` or ```MessageWriter``` 
  (respectively) has already been set

* ```withMessageIOFactory(MessageIOFactory)```
  * This method accepts a ```MessageIOFactory```, which is used to create a ```MessageReader``` and/or 
  ```MessageWriter``` from a raw ```InputStream``` and/or ```OutputStream```.
  * ```withSocket```, ```withInputStream``` and ```withOutputStream``` will all throw an ```IllegalStateException``` if
  this has not first been invoked.
  * [```GsonMessageIOFactory```](javadoc/com/jenjinstudios/io/serialization/GsonMessageIOFactory.html) is provided as a 
  convenience; implementing your own is not necessary (though it is encouraged to better suit the needs of your 
  application)

* ```withMessageReader(MessageReader)``` and ```withMessageWriter(MessageWriter)```
  * These methods directly set the ```MessageReader``` and ```MessageWriter``` to be used by the built ```Connection```.
  (for ```withMessageReader```) and ```withOutputStream``` (for ```MessageWriter```).
  * If the ```MessageReader``` or ```MessageWriter``` has already been set, these methods will throw an 
  ```IllegalStateException```.
  
> **Important:** These methods must both be invoked with non-null values (either explicitly, or by calling 
  ```withSocket``` or ```withInputStream``` and ```withOutputStream```) before the ```build``` method is called, or an
  ```IllegalStateException``` will be thrown.
  
> **Note:** These methods will be called internally by ```withSocket```, ```withInputStream``` 

* ```withErrorCallback(BiConsumer<Connection, Throwable>)```
  * This method will cause the built ```Connection``` to invoke the specified ```BiConsumer``` if it encounters an 
  error; it is recommended that the ```stop``` method be called on the ```Connection``` at the end of this callback
  so that the ```Connection``` closes as cleanly as possible.
  
* ```withContextualTasks(Consumer<ExecutionContext>...)``` 
  * This method takes in one or more ```Consumers``` that accept an ```ExecutionContext``` parameter, which will be 
  invoked by the built ```Connection``` after each incoming message is executed.  
  * This callback is useful when there are parts of your application that need to access the ```ExecutionContext``` of 
  a ```Connection``` but should not be accessible from a ```Message```.  (UI components updating based on the current 
  state of the context may be an example)

* ```withShutdownCallback(Consumer<Connection>)```
  * This method takes in a ```Consumer``` that accepts a ```Connection```, which is invoked after the built 
  ```Connection``` has halted its threads and attempted to close its backing streams.
  * This method can also take an ```Iterable<Connection>``` or be invoked multiple times if multiple callbacks are 
  desired.

Once you've configured your connection, you can build it with the ```build``` method:

> **Important:** If the ```MessageReader```, ```MessageWriter```, or ```ExecutionContext``` are not set, the ```build```
method will throw an IllegalStateException.  The ```MessageReader``` is set automatically if the ```withInputStream```
or ```getSocket``` methods are used; similarly, the ```MessageWriter``` is set automatically if the 
```withOutputStream``` or ```withSocket``` methods are used.

{% highlight java %}
Connection connection = builder.build();
{% endhighlight %}

> **Note:** The ```SingleConnectionBuilder``` class is fluent; it can be used like so:
> {% highlight java %}
private Connection getConn(Socket sock, ExecutionContext context) {
    return new SingleConnectionBuilder()
        .withMessageIOFactory(new GsonMessageIOFactory())
        .withSocket(sock)
        .withExecutionContext(context)
        .withErrorCallback((connection, throwable) -> connection.stop())
        .build();
}
{% endhighlight %}


#### [MultiConnectionBuilder](javadoc/com/jenjinstudios/io/connection/MultiConnectionBuilder.html)

The ```MultiConnectionBuilder``` class is very simliar to the ```SingleConnectionBuilder``` class, with a few key 
differences:

1. The ```build``` method accepts a ```Socket``` instead of having no parameters
  * Each time ```build``` is called, a **new** ```Connection``` will be created from the given Socket.
2. There is a new ```withExecutionContextFactory``` method, that accepts an ```ExecutionContextFactory``` that will be 
used to generate a new ```ExecutionContext``` for each ```Connection``` built with this builder.
3. The ```withMessageReader```, ```withMessageWriter```, ```withInputStream```, ```withOutputStream``` and 
```withExecutionContext``` methods are not present

> **Important:** The callbacks (```Consumers```, ```BiConsumers```) passed into a ```MultiConnectionBuilder``` should be
  **immutable**, as each callback will be passed into every connection rather than being copied.

### [Server](javadoc/com/jenjinstudios/io/server/Server.html)

The ```Server``` class is a convenience class provided by Jenjin-IO that is capable of accepting multiple client 
```Connections```.  It is not terribly robust, so it may be prudent to examine the source and create your own 
implementation that better suits your needs.

A ```Server``` requires the following objects (which are supplied from a ```ServerBuilder```:

* A ```ServerSocket```
  * This is necessary to listen for inbound socket connections
  * Use an ```SSLServerSocket``` if possible.
* A ```MultiConnectionBuilder```
  * Necessary for building a new ```Connection``` for each inbound socket.
  
This class has two ```broadcast``` methods, one which takes a single ```Message``` parameter, which is broadcast to
all existing ```Connections```, and a second which takes both a ```Message``` parameter and a 
```Predicate<Connection>``` parameter, which broadcasts to all ```Connections``` which fulfill the ```Predicate```.

#### [ServerBuilder](javadoc/com/jenjinstudios/io/server/ServerBuilder.html)

Much like the ```SingleConnectionBuilder``` and ```MultiConnectionBuilder``` classes, the ```ServerBuilder``` class is
responsible for configuring and building a ```Server```.

The ```ServerBuilder``` class has several methods that help with configuring a ```Server```:

* ```withServerSocket(ServerSocket)```
  * This method takes in a Java ```ServerSocket```, which will be used by the built ```Server``` to accept inbound 
  connections.

> **Important:** If the ```build``` method is called without the ```ServerSocket``` being set, an 
  ```IllegalStateException``` will be thrown.

* ```withMultiConnectionBuilder(MultiConnectionBuilder)```
  * This method takes in a ```MultiConnectionBuilder``` that is used by the built ```Server``` to generate new 
  ```Connections``` from inbound sockets.

> **Important:** If the ```build``` method is called without the ```MultiConectionBuilder``` being set, an
  ```IllegalStateException``` will be thrown.
  
* ```withContextualTasks(BiConsumer<Server, ExecutionContext>...)```
  * This method allows for contextual callbacks in a similar fashion to the 
  ```SingleConnectionBuilder#withContextualTasks``` method, but allows for access to the ```Server``` object.  
  * Use of this method is helpful for things like broadcasting a message to all connections, without giving the 
  ```Connection``` objects direct access to the ```Server```.
  
* ```withConnectionAddedCallbacks(Consumer<Connection>...)```
  * This method accepts one or more ```Consumer<Connection>``` parameters, which are invoked any time a new 
  ```Connection``` is added to the server.
  * These callbacks are useful for when you want to perform an action (such as logging) whenever a new ```Connection```
  is made.
  
* ```withConnectionRemovedCallbacks(Consumer<Connection>...)```
  * This method accepts one or more ```Consumer<Connection>``` parameters, which are invoked any time a ```Connection```
  is removed from the ```Server```
  * Once again, these are useful when you need to be notified of a ```Connection``` being removed from the ```Server```.
  
* ```withStartupCallbacks(Consumer<Server>...)```
  * This method accepts one or more ```Consumer<Server>``` parameters, which are invoked after the ```Server``` has been
  started.
  
* ```withShutdownCallbacks(Consumer<Server>...)```
  * This method accepts one or more ```Consumer<Server>``` parameters, which are invoked after the ```Server``` has 
  attempted to shut down and gracefully close all existing ```Connections```.
  
> **Note:** The ```ServerBuilder``` class is fluent; it can be used like so: 
> {% highlight java %}
private Server getServer(ServerSocket sock, MultiConnectionBuilder mcb) {
    return new ServerBuilder()
        .withServerSocket(sock)
        .withMultiConnectionBuilder(mcb)
        .withConnectionAddedCallback(this::doSomething)
        .build();
}
{% endhighlight %}

### Other important classes

These interfaces need not be explicitly implemented by your application; Jenjin-IO provides a few convenience classes
(relying on the Gson library) that implement working versions of them in the 
[com.jenjinstudios.io.serialization](javadoc/com/jenjinstudios/io/serialization/package-summary.html) package

However, these classes are not optimized for performance or bandwidth, and it may be prudent to implement your own 
versions that better cater to the particular needs of your application.

#### [MessageIOFactory](javadoc/com/jenjinstudios/io/MessageIOFactory)

This interface exposes methods to create a ```MessageReader``` and ```MessageWriter``` from an ```InputStream``` and 
```OutputStream``` respectively.

#### [MessageReader](javadoc/com/jenjinstudios/io/MessageReader)

This interface exposes a ```read``` method which returns a ```Message``` and a ```close``` method that should close 
backing streams and perform any necessary cleanup.

#### [MessageWriter](javadoc/com/jenjinstudios/io/MessageWriter)

This interface exposes a ```wrute``` method which accepts a ```Message``` and a ```close``` method that should close 
backing streams and perform any necessary cleanup.

- - -

## Building

> **Note:** To build Jenjin-IO from source, you must have Java 8 installed and your ```JAVA_HOME``` environment variable pointing at
the Java 8 installation.

Jenjin-IO is built using gradle; to build the library and run all tests, simply run this command in the Jenjin-IO directory:

{% highlight bash %}
./gradlew build
{% endhighlight %}


- - -

## License

Jenjin-IO is licensed under the [MIT license](https://github.com/floralvikings/jenjin-io/blob/master/LICENSE)