# Lexicographic-Closure
Lexicographic Closure implementation was built using Java 16 owing to the TweetyProject and JMH dependency.

[maven](https://maven.apache.org/users/index.html) is required to build the project.

To compile, from the main directory, run

```sh
mvn compile
```

A **jar** file will be compiled to the `target` directory.

To run the base rank algorithm and store it as a json file, from the main directory, run

```sh
java -cp target/mytweetyapp-1.0-SNAPSHOT-jar-with-dependencies.jar mytweety.mytweetyapp.App fileWriter
```

This allows for the user to enter in the file name for the knowledge base.

To run the reasoner, run

```sh
java -cp target/mytweetyapp-1.0-SNAPSHOT-jar-with-dependencies.jar mytweety.mytweetyapp.App Reasoner
```

To run the tester, run

```sh
java -cp target/mytweetyapp-1.0-SNAPSHOT-jar-with-dependencies.jar mytweety.mytweetyapp.App Tester
```
