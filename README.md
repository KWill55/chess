# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

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



## Sequencing Diagram for Server Design 
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAA5Tp+1AgBXbDAAxGjAVJYwAEoo9kiqFnJIEGhBAO4AFkhgYoiopAC0AHzklDRQAFwwANoACgDyZAAqALowAPS+BlAAOmgA3gBEHZQhALYo-WX9MP0ANNO46inQHBNTs9MoI8BICKvTAL6YwqUwhazsXJQVA0NQo+OT03P9C6pLUCuP6-2b27tfhzYnG4sDOx1EFSg0ViWSgAAoojE4pQogBHXxqMAASiOJVEpyKsnkShU6gq9hQYAAqp04bd7jiiYplGpVASjDoygAxJCcGA0yhMmA6cL04BjTBMkms05gvEqCpoXwIBC4kQqdlSllkkBQuQoAXw25MxnaaXqdnGMoKDgcfmdJlqqj4s5a0mqMq6lD6hS+MBpOHAP1pE2Ss3atlnK02u2+-2O8EavJnIGXcqRaHIqBRVTKrCpkGyorHK4wG6de5TCprZ5B-0NCAAa3QVemBydlCL8GQ5gqACYnE4euXhuLxjBq09pnW0g3m2hW2tDugOJ5MN4-AFAtB2BSYAAZCAxZKBdKZbI9-LFkqlmr1ZptAzqRJoYfTMVjVbfV7vT79KaHCWoJFAWpa9O+FZjl+zw-ssX4wICFyFnK6ooBUCBHrycKHseaIYnEOKJoYrrhu65KUoadKQWMprEhGlqcjAPJ8oaQoijAH5iERmqkay5FgHGAacbRzLugxFTMbGwZsaK1FcfKxEpkhpY4byOZ5pgoGgihpTXIMcmLlO-QznOLYTm2-SATe2lFDkvYwAOQ59Ppo6fuZNbTsGpkLu57Yrp4G7+EE3goOgB5Hn4zCnhkWSYHZV7FNQt7SAAovuKUNClLStE+qgvj0MAztASAAF4JEkFQADwmU26AFB2wHnMCpZFVApXlWgVU1fO9VaeyRHoRFfrYUNYB4ZihEKTxdFkUYKDcFkgmBl5tVoCJ5qRkUVoyPNlKGIJMmFSt85Oi6SnNeUOGRepCD5spNmJbpZZWUlD3xWA-aDsOy6cAFPhBYEUJ2vuMIwAA4mObLReecWXswOm3mDGXZfYY49K17UEBVMDVcddUNeyWkVBjZVY51OPdfjfU6RCMDIHE2EwuNBGnUmhK8WSdMwhDYyqHC630VGjGSeDY5CgAZtAhUqjAqO86zikgfdFQg3EN13RdXZAXpctqBMVQDLrACS0j6wAjH2ADMAAszxnlkhqVpM3w6AgoCNo7UHO88usAHJjs7L0nMmtlw59TmG5D+uVJHYwm+bVu29M9sGgZ3vTK77ue5+6f9H7Af-vsMBND9q5-ZuQTYL4UDYNw8B6lkotjKkMUXrk8PXq9FR3o0KNo2guP1qtBRvnnY7+zn-4IQT51pl1eNoCPfTTPnk8AZp939Qp5JjnCXr6sz2IK9Nol8TA+9ZDzKBwqvKAC2JQsVDGTcoIdt-HyHTVz-X3pZOrG+a0-trMsY846mwqBbG2QdOyf3euHYcoCUDxwgYnUua5ApbksPNDCKQYAACkIC8hfkETOIBGyw3blvLuVRqhUgfK0XW6NgzFVJi+eeQ8eqj1rsALBUA4AQAwlAVYAB1FgRtMqtAAEL7gUHAAA0t8Y24CYCQOttPIChNlaD1nMPLhbteH8MESIsREjpGyIUT7McyCVGJ2ntTTuzoFQwAAFaELQHCAhakUDogmh-dmM0z4UjAFfG+VjpD3xlI-JivI7RXzfmEvxMgOYeg4tgLQl9d5KIiRaKJz84naGFOEJRH9Z4ggqJ4tA-9qYI3TL0aBb0w4OS+n0NB5cAbeB4d2b0sBgDYFroQDqLcYbvSoU9SoqV0qZWysYGA30Z5KwutcRCgCHEQjqYkt0Z8QDcDwPzMMASclbUYtIXajd8nyH2afQ5HIKgnIWoYVi2hLkbXEjte5MADpPO4p-Imz0AFpi1tZJZM8zhwKaRHNBQA
