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

import java.util.ArrayList;
import java.util.Map;

import io.fluo.api.data.Bytes;
import io.fluo.api.data.Column;
import io.fluo.api.types.TypedObserver;
import io.fluo.api.types.TypedSnapshotBase.Value;
import io.fluo.api.types.TypedTransactionBase;

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
    String content = tx.get().row(row).col(column).toString();

    ArrayList<String> wordRows = new ArrayList<>();
    for (String word : content.split("[ ]+"))
      wordRows.add("word:" + word);

    Map<String,Map<Column,Value>> counts = tx.get().rowsString(wordRows).columns(COUNT_COL).toStringMap();

    for (String wordRow : wordRows) {
      int count = counts.get(wordRow).get(COUNT_COL).toInteger(0);
      tx.mutate().row(wordRow).col(COUNT_COL).set(count + 1);
    }
  }

  @Override
  public ObservedColumn getObservedColumn() {
    return new ObservedColumn(CONTENT_COL, NotificationType.STRONG);
  }
}
