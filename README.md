# jenjin-io
IO Utilities for Client/Server Connections

## Usage

The core of the Jenjin-IO Framework is the ```Connection``` class.  A ```Connection``` requires four components to
operate:

1. An ```ExecutionContext``` implementation - the ```ExecutionContext``` is a object storing connection-specific data
   that is persistent across messages.  Since this object is accessed by multiple threads, you should take care that any
   data contained therein is thread-safe.
2. A ```MessageReader``` that will read data from an ```InputStream``` and attempt to deserialize it into an 
   implementation of the ```Message``` interface.
3. A ```MessageWriter``` that will attempt to serialize an implementation of the ```Message``` interface into a format
   that can be written into an ```OutputStream```.
4. An optional ```BiConsumer``` that will be used as a callback function if an error is encountered when reading,
   writing, or executing a ```Message``` implementation.
   

The best way to create a new Connection is with a ConnectionBuilder.

When the ```start``` method is called, four threads are started with the following periodic tasks:

1. **Read Thread**
  * Reads from the ```MessageReader```, storing the resulting ```Message``` object in a special, thread-safe queue that
    is accessed by the other ```Connection``` threads.
2. **Write Thread**
  * Writes any outgoing ```Message``` implementations created by the ```ExecutionThread``` to an ```OutputStream```
    using the ```MessageWriter```
3. **Execution Thread**
  * Processes ```Message``` objects which have been read by the **Read Thread** by calling ```execute``` on them, 
    passing in the ```ExecutionContext``` object.  The return value of ```execute``` is then stored for the 
    **Write Thread** to later write.
4. **Error Thread**
  * Continually monitors the other threads for any thrown exceptions (by examining the shared queue) and logs them, 
    then, if it exists, invokes the specified ```BiConsumer``` with the ```Connection``` and  ```Throwable```.
   

## License

Jenjin-IO is licensed under the [MIT license](https://github.com/floralvikings/jenjin-io/blob/master/LICENSE)
