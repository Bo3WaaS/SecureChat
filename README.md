Project Structure:

├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── eissa
│   │   │           └── chat
│   │   │               ├── client
│   │   │               │   └── Client.java
│   │   │               ├── config
│   │   │               │   └── SSLContextConfigurator.java
│   │   │               └── server
│   │   │                   ├── Server.java
│   │   │                   ├── ServerThread.java
│   │   │                   └── ServerWithClientAuth.java
│   │   └── resources
│   │       ├── client_truststore.jks
│   │       ├── server-keystore.jks
│   │       └── server_truststore.jks
│   └── test
│       ├── java
│       │   └── com
│       │       └── eissa
│       │           └── test
│       │               ├── TestOneWayHandshake.java
│       │               └── TestTwoWayHandshake.java
│       └── resources
│           ├── client_keystore.jks
│           └── client_truststore.jks


Credit:

http://www.robinhowlett.com/blog/2016/01/05/everything-you-ever-wanted-to-know-about-ssl-but-were-afraid-to-ask
