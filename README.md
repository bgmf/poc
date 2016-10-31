# The POC and Tests project

This is a simple project to collect tests I've done, either out of pure interest or in response to some forum (e.g. German [Java-Forum](http://www.java-forum.org/forum/awt-swing-javafx-swt.13/)) and/or [StackOverflow ](https://stackoverflow.com/) posts.

## Sub Modules

 * `shared` - interfaces and such stuff - __TODO: integrate into the other projects__
 * `simple-tests-fx` - simple projects to test some aspects of `JavaFX`
 * `guice-fx` - simple project to show, how to build a simple `JavaFX` project with [Guice](https://github.com/google/guice) ([DI](https://en.wikipedia.org/wiki/Dependency_injection))
 * `kotlin-java-mix` - while using `JavaFX` to start a [Stage](https://docs.oracle.com/javase/8/javafx/api/javafx/stage/Stage.html), this is just a showcase for a Maven project setup where both `Java` and [Kotlin](https://kotlinlang.org/) are set up together in one project

## Sub Modules - to do
 * `spring-boot-fx` - as the `guice-fx` module, this is intended to show how to use [Spring Boot](https://projects.spring.io/spring-boot/) ([DI](https://en.wikipedia.org/wiki/Dependency_injection)) with `JavaFX`
 * `kotlin-guice-fx` - simple [Kotlin](https://kotlinlang.org/) project with a bit of [Guice](https://github.com/google/guice) thrown onto it

## Sub Modules - ideas

 * `serviceloader-plugin-fx` - a poor man's plugin solution based on Java's [ServiceLoader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) (see [here](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html)).
 * OSGi based FX application (might be best to be contained within it's own repository)