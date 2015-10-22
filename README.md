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

## Dependencies

Below is the entire set of dependencies (including **test dependencies** and  *transitive dependencies*):

| Dependency            | Version       | Link                                                  | License                                                                   | 
|-----------------------|---------------|-------------------------------------------------------|---------------------------------------------------------------------------|
| Gson                  | 2.3.1         | https://github.com/google/gson                        | [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)                  |
| Reflections           | 0.9.9-RC1     | https://github.com/ronmamo/reflections                | [WTFPL](http://www.wtfpl.net/)                                            |
| SLF4J                 | 1.7.12        | http://www.slf4j.org/                                 | [MIT](http://www.slf4j.org/license.html)                                  |
| **Mockito**           | 2.0.2-beta    | http://mockito.org/                                   | [MIT](https://github.com/mockito/mockito/blob/master/LICENSE)             |
| **TestNG**            | 6.8.7         | http://testng.org/doc/index.html                      | [Apache 2.0](http://testng.org/license/)                                  |
| **SLF4J JDK Binding** | 1.7.12        | http://www.slf4j.org/                                 | [MIT](http://www.slf4j.org/license.html)                                  |
| *DOM4J*               | 1.6.1         | https://dom4j.github.io/                              | [BSD](https://github.com/dom4j/dom4j/blob/master/LICENSE)                 |
| *xml-apis*            | 1.0.b2        | http://mvnrepository.com/artifact/xml-apis/xml-apis   | [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)                  |
| *findbugs*            | 1.3.9         | https://github.com/findbugsproject/findbugs           | [LGPL](http://www.gnu.org/licenses/lgpl.html)                             |
| *Guava*               | 11.0.2        | https://github.com/google/guava                       | [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)                  |
| *Javassist*           | 3.16.1-GA     | http://jboss-javassist.github.io/javassist/           | [MPL](https://www.mozilla.org/en-US/MPL/2.0/)                             |
| ***BeanShell***       | 2.0b4         | http://www.beanshell.org/                             | [Sun Public License/LGPL](http://www.beanshell.org/license.html)          |
| ***Hamcrest***        | 1.1           | https://github.com/hamcrest/JavaHamcrest              | [BSD](https://github.com/hamcrest/JavaHamcrest/blob/master/LICENSE.txt)   |
| ***JCommander***      | 1.27          | http://jcommander.org/                                | [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)                  |
| ***JUnit***           | 4.10          | http://junit.org/                                     | [EPL](http://junit.org/license.html)                                      |
| ***SnakeYAML***       | 1.12          | https://bitbucket.org/asomov/snakeyaml                | [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)                  |
