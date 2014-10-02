fluo-quickstart
===============

A simple word count example using Fluo.  In order to run this example, clone
this repo and then execute the following maven commands.

```
git clone https://github.com/fluo-io/fluo-quickstart.git
cd fluo-quickstart
mvn package
mvn exec:java -Dexec.mainClass=io.fluo.quickstart.Main -Dexec.cleanupDaemonThreads=false
```

The [Main][1] class does all of the heavy lifting.  It starts MiniFluo, adds
documents, waits for [DocumentObserver][2] to finish processing all documents,
and then prints out the word counts.

[Main][1] has some suggested modifications in its comments if you want to
experiment with Fluo.

This example is not comprehensive.  Further improvements are suggested source
code comments, but were intentionally not done.  This leaves the example really
short and immediately gives you things to try.  For a more comprehensive
example, see  [phrasecount][3].

[1]: src/main/java/io/fluo/quickstart/Main.java
[2]: src/main/java/io/fluo/quickstart/DocumentObserver.java
[3]: https://github.com/fluo-io/phrasecount
