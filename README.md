# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Link to Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoARiqfJCIK-P8gK0eh8KVEB-rgeWKkwes+h-DsXzQo8wHiVQSIwAgQnihignCQSRJgKSb6GLuNL7gyTJTspXI3r5d5LsKMBihKboynKZbvBWjwwOpOwhbyYU2XZMVbl5grDvy9SHnIKDPvE56Xte+WLpUy4BmuAYVTlzreR+Vn+i54oZKoAGYHp8I2fphGGfMqnkZeVH1pCVygZhyCpjAuH4aMQ0JcRpHfBRE3ISRqENvRjHeH4-heCg6AxHEiQnWdLm+FgomCqB9QNNIEb8RG7QRt0PRyaoCnDJtiHoNpgp9fUANIb1bU6R2tkuvZQm3c5COnm5agedu3lUreBUwIyLJsuD6BzqFDrhfUUXrtl8iyvKpVbRjeXY-SRgoNwx6XuV8GA2gxPpaTNURRkMwQDQDUvk1nbtqDAnI2AXU9X1IHVPp03K-1lSiTheFZnRTaeIdAQouu-jYOKGr8WiMAAOJKho90DaWDRW+9X32Eq-3jdzwOtbC-qE4UiuZXDyA5DbOYYiHYBh2oqMkgzPl88zeOlZzlHc7zC4CmTkXik+nsQ7lCeZ-5Ue2xiGf7veEUUwGfbW7baWZ0rnb1NHGiFz7X71JHbfywggFQ83NQvGMbs5gWDTjGPKAAJLSAW-ggps8S6igbqcl8KzDMsySgGq6+QaNo9KgAckqU0wJ0qsSer8BzdhC3a8t0+qBPU9KnPC9Lyvk5ESNMHbzGLvEA+8-6b2WNPM+cwL5X0bAxfWzF-AcAAOxuCcCgJwMQIzBDgFxAAbPACchho4wCKPfMSvo1ZPVaB0V27tpj53QARSBh8tIzU7nCMGjC0BrHfnMKB-80KB0oS3GARV0TRwxOIlA0dY7o0LljEmScmQp39hXMKAtya5zFmnAuzVGZKNHGIohkiWFzHUfzIU9Q4BEPrvwpUjdK5JhEbDLsdjZ7SHjhw-00jZH-n7pDX2t9HowD4R4he19EzJnIVrJaYTP71H8LreBTEjqWFZg5TY50kAJDAOkvsEAskACkIDincTEYBaoyElAoTDSSTRmQyR6NPD2XMkIEWwAgYA6SoBwAgA5KA4Dj5zASWwtWIMoZcLaegXhPwuk9L6QMoZ09RlCMHkHNxAArUpaBJHbPFH4wkaNPL6KLn5XGKiOZqMcRoqxOcJR0y9gomQTMjF4ynNcqqWdNH3MpjObQMAABm3gZg6Ppl8oeWV-nyHjooxORj9m7KVBiFZ0g1idO6ZQRZ0A1hBQsdVO5j4YBNHmRhSA5T9DwH6dALxulJkwERX47qATA4OxeJEjCzi741NiTrOBB1EFeG6dk3JQr5SIGDLAYA2BOmEDyAUUh9sXGSRem9D6X1jDezpUE9lgSvwPWaoVbgeApHGqgHIk5kszkjiNZK8uNzLG1WkKzJkhhAwIA3FAW0ryq71GdWzN1dc24OoJU6l16Ja4eseUhdupzvGlhAGavuA8glDxVt7aJPLH5xOSZgIAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
