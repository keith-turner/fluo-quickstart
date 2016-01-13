/*
 * Copyright 2014 Fluo authors (see AUTHORS)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fluo.quickstart;

import java.io.File;
import java.util.Map.Entry;

import com.google.common.io.Files;
import io.fluo.api.client.FluoClient;
import io.fluo.api.client.FluoFactory;
import io.fluo.api.client.Snapshot;
import io.fluo.api.config.FluoConfiguration;
import io.fluo.api.config.ObserverConfiguration;
import io.fluo.api.config.ScannerConfiguration;
import io.fluo.api.data.Bytes;
import io.fluo.api.data.Column;
import io.fluo.api.data.Span;
import io.fluo.api.iterator.ColumnIterator;
import io.fluo.api.iterator.RowIterator;
import io.fluo.api.mini.MiniFluo;
import io.fluo.api.types.TypedSnapshot;
import io.fluo.api.types.TypedTransaction;
import org.apache.commons.io.FileUtils;

import static io.fluo.quickstart.DocumentObserver.CONTENT_COL;

/**
 * A word count example using Fluo
 */

public class Main {

  static StringTypeLayer stl = new StringTypeLayer();

  private static void addDocument(FluoClient fluoClient, String id, String content) {
    // Use try with resource to ensure transaction is closed.
    try (TypedTransaction tx1 = stl.wrap(fluoClient.newTransaction())) {
      String rowId = "doc:" + id;

      if (tx1.get().row(rowId).col(CONTENT_COL).toString() == null) {
        tx1.mutate().row(rowId).col(CONTENT_COL).set(content);
        tx1.commit();
      }
    }

  }

  private static void printDocument(FluoClient fluoClient, String id) {
    // Use try with resource to ensure snapshot is closed.
    try (TypedSnapshot tx1 = stl.wrap(fluoClient.newSnapshot())) {
      String content = tx1.get().row("doc:" + id).col(CONTENT_COL).toString();
      System.out.printf("Document %s : %s\n", id, content);
    }

  }

  private static void printWordCounts(FluoClient fluoClient) throws Exception {
    // Use try with resource to ensure snapshot is closed.
    try (Snapshot snapshot = fluoClient.newSnapshot()) {
      ScannerConfiguration scanConfig = new ScannerConfiguration();
      scanConfig.setSpan(Span.prefix("word:"));

      RowIterator rowIter = snapshot.get(scanConfig);

      while (rowIter.hasNext()) {
        Entry<Bytes,ColumnIterator> row = rowIter.next();
        ColumnIterator colIter = row.getValue();
        while (colIter.hasNext()) {
          Entry<Column,Bytes> column = colIter.next();
          System.out.println(row.getKey() + " " + column.getValue());
        }
      }

    }
  }

  public static void main(String[] args) throws Exception {

    File miniAccumuloDir = Files.createTempDir();

    FluoConfiguration config = new FluoConfiguration();
    config.addObserver(new ObserverConfiguration(DocumentObserver.class.getName()));
    config.setApplicationName("quick-start");
    config.setMiniDataDir(miniAccumuloDir.getAbsolutePath());


    System.out.println("\nStarting Mini ...");
    // Use try with resources to ensure that FluoClient is closed.
    try (MiniFluo mini = FluoFactory.newMiniFluo(config); FluoClient fluoClient = FluoFactory.newClient(mini.getClientConfiguration())) {
      // TODO could use a LoaderExecutor to load documents using multiple
      // threads. Left as an exercise to reader.

      System.out.println("Adding documents ...");

      addDocument(fluoClient, "00001", "hello world welcome to the fluo quickstart the first one in the entire world");
      addDocument(fluoClient, "00001", "hola world welcome to the fluo quickstart the first one in the entire world");
      addDocument(fluoClient, "00002", "hola world");

      System.out.println("Reading documents ...");

      printDocument(fluoClient, "00001");
      printDocument(fluoClient, "00002");
      System.out.println();

      System.out.println("Waiting for observer ...");

      // wait for observer to run and update counts
      mini.waitForObservers();

      System.out.println("Printing word counts...");
      printWordCounts(fluoClient);

      // TODO : Add ability to delete document and decrement counts, left as an
      // exercise for reader. Suggest adding a column which indicates a documents
      // should be deleted by observer. For bonus points, handle the case where
      // document not yet processed by observer is deleted, need to keep info about
      // document status. Also, updating documents when contents change is not
      // handled.

      // deleteDocument("00001");
      // addDocument(fluoClient, "00003", "ciao world");
      // deleteDocument("00003");
      // miniHelper.getMiniFluo().waitForObservers();
      // printWordCounts(fluoClient);

      System.out.println("\nStopping Mini ...\n");
    }

    FileUtils.deleteQuietly(miniAccumuloDir);
  }


}
