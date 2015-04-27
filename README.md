fluo-quickstart
===============

A simple word count example using Fluo.  In order to run this example, clone
this repo and then execute the following maven commands.

```bash
git clone https://github.com/fluo-io/fluo-quickstart.git
cd fluo-quickstart
mvn package
#use test scope to pick up log4j props and mini Fluo.  Want to avoid including
#log4j props in jar, so the log4j props were not placed in src/main/resources.
#Want to avoid mini Fluo as a regular dependency because it depends on server
#Accumulo and Hadoop artifacts.
mvn exec:java -Dexec.mainClass=io.fluo.quickstart.Main -Dexec.cleanupDaemonThreads=false -Dexec.classpathScope=test
```

The [Main][1] class does all of the heavy lifting.  It starts MiniFluo, adds
documents, waits for [DocumentObserver][2] to finish processing all documents,
and then prints out the word counts.

[Main][1] has some suggested modifications in its comments if you want to
experiment with Fluo.

This example is not comprehensive.  Further improvements are suggested in
source code comments, but were intentionally not done.  This leaves the example
really short and immediately gives you things to try.  For a more comprehensive
example, see  [phrasecount][3].

[1]: src/main/java/io/fluo/quickstart/Main.java
[2]: src/main/java/io/fluo/quickstart/DocumentObserver.java
[3]: https://github.com/fluo-io/phrasecount
