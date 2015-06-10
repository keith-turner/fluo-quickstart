fluo-quickstart
===============

A simple word count example using Fluo.  In order to run this example, you will
need [Git], [Java], and [Maven] installed.

First, clone this repo:

    git clone https://github.com/fluo-io/fluo-quickstart.git

Next, build the repo which will import all jars needed to run Fluo:

    cd fluo-quickstart
    mvn package

Finally, run the Fluo application using Maven:

    mvn exec:java -Dexec.mainClass=io.fluo.quickstart.Main -Dexec.cleanupDaemonThreads=false -Dexec.classpathScope=test

The quickstart [Main] class does all of the heavy lifting.  It starts a local Fluo instance (called MiniFluo),
adds documents, waits for the [DocumentObserver] to finish processing all documents, and then prints
out the word counts of the loaded documents.  It finally shuts down MiniFluo before exiting.  It may help to
reference the [API javadocs][api] while you are learning the Fluo API.

This example is intentionally not comprehensive to keep it short and provide you an opportunity to experiment.
Further improvements are suggested in the source code comments of [Main] & [DocumentObserver] if you are
interested. For a more comprehensive Fluo application, see the [phrasecount] example.

[Git]: http://git-scm.com/
[Java]: https://www.oracle.com/java/index.html
[Maven]: http://maven.apache.org/
[Main]: src/main/java/io/fluo/quickstart/Main.java
[DocumentObserver]: src/main/java/io/fluo/quickstart/DocumentObserver.java
[phrasecount]: https://github.com/fluo-io/phrasecount
[api]: http://fluo.io/apidocs/
