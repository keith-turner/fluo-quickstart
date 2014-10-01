fluo-quickstart
===============

A simple word count example using Fluo.  Inorder to run this example, clone
this repo and then execute the following maven command.

```
git clone https://github.com/fluo-io/fluo-quickstart.git
cd fluo-quickstart
mvn package
mvn exec:java -Dexec.mainClass=io.fluo.quickstart.Main -Dexec.cleanupDaemonThreads=false
```

The [Main][1] class does all of the heavy lifting.  It starts MiniFluo, adds
documents, waits for [DocumentObserver][2], and then prints out the word
counts.

[Main][1] has some suggested modifications in its comments if you want to
experiment with Fluo.

[1]: src/main/java/io/fluo/quickstart/Main.java
[2]: src/main/java/io/fluo/quickstart/DocumentObserver.java
