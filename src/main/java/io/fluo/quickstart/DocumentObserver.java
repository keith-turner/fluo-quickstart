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

import java.util.Map;

import io.fluo.api.data.Bytes;
import io.fluo.api.data.Column;
import io.fluo.api.types.TypedObserver;
import io.fluo.api.types.TypedSnapshotBase.Value;
import io.fluo.api.types.TypedTransactionBase;
import org.apache.commons.collections4.map.DefaultedMap;

/**
 * A simple observer that updates word counts when a documents contents are updated.
 */
public class DocumentObserver extends TypedObserver {

  public DocumentObserver() {
    super(new StringTypeLayer());

  }

  static final Column COUNT_COL = new Column("meta", "count");
  static final Column CONTENT_COL = new Column("doc", "content");

  @Override
  public void process(TypedTransactionBase tx, Bytes row, Column column) {

    // This method is called by Fluo whenever another transaction modifies the
    // 'doc':'content' column. This method will extract the words from the
    // document at the specified row, and update the global counts for those
    // words.

    // This code does not handle high cardinality words well. The fluo
    // phrasecount example has handling for this case.

    String docContent = tx.get().row(row).col(column).toString();

    // compute how many times each word occurs in document
    Map<String,Integer> docCounts = new DefaultedMap<>(Integer.valueOf(0));
    for (String word : docContent.split("[ ]+")) {
      String wordRow = "word: " + word;
      docCounts.put(wordRow, docCounts.get(wordRow) + 1);
    }

    Map<String,Map<Column,Value>> globalCounts = tx.get().rowsString(docCounts.keySet()).columns(COUNT_COL).toStringMap();

    // update global word counts
    for (String wordRow : docCounts.keySet()) {
      // Fluo TypeLayer returns defaulted maps, so no need to check if row exist
      // then check if column exist.
      int count = globalCounts.get(wordRow).get(COUNT_COL).toInteger(0);
      tx.mutate().row(wordRow).col(COUNT_COL).set(count + docCounts.get(wordRow));
    }
  }

  @Override
  public ObservedColumn getObservedColumn() {

    // This method is called by Fluo, during initialization, to determine which
    // column is being Observed.

    return new ObservedColumn(CONTENT_COL, NotificationType.STRONG);
  }
}
