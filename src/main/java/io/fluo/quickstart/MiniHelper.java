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
import java.util.List;

import com.google.common.io.Files;
import io.fluo.api.client.FluoFactory;
import io.fluo.api.client.MiniFluo;
import io.fluo.api.config.FluoConfiguration;
import io.fluo.api.config.ObserverConfiguration;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.apache.accumulo.minicluster.MiniAccumuloConfig;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Some utility code to start {@link MiniAccumuloCluster} and {@link MiniFluo}
 */
public class MiniHelper implements AutoCloseable {

  private File dir;
  private PropertiesConfiguration connectionProps;
  private MiniAccumuloCluster cluster;
  private MiniFluo miniFluo;

  public MiniHelper(File dir, List<ObserverConfiguration> observers) throws Exception {
    this.dir = dir;

    // lets tone down the noise from logging
    Logger.getLogger("org.apache.zookeeper").setLevel(Level.ERROR);
    Logger.getLogger("org.apache.curator").setLevel(Level.WARN);
    Logger.getLogger("io.fluo").setLevel(Level.WARN);

    MiniAccumuloConfig cfg = new MiniAccumuloConfig(dir, new String("secret"));

    cluster = new MiniAccumuloCluster(cfg);
    cluster.start();

    FluoConfiguration fluoConfig = new FluoConfiguration();
    fluoConfig.setAccumuloInstance(cluster.getInstanceName());
    fluoConfig.setAccumuloUser("root");
    fluoConfig.setAccumuloPassword("secret");
    fluoConfig.setZookeeperRoot("/fluo");
    fluoConfig.setZookeepers(cluster.getZooKeepers());

    connectionProps = new PropertiesConfiguration();
    connectionProps.copy(fluoConfig);

    fluoConfig.setAccumuloTable("data");
    fluoConfig.setObservers(observers);

    FluoFactory.newAdmin(fluoConfig).initialize();

    miniFluo = FluoFactory.newMiniFluo(fluoConfig);
    miniFluo.start();
  }

  public MiniHelper(List<ObserverConfiguration> observers) throws Exception {
    this(Files.createTempDir(), observers);
  }

  public Configuration getConnectionConfig() {
    return connectionProps;
  }

  public MiniFluo getMiniFluo() {
    return miniFluo;
  }

  @Override
  public void close() throws Exception {
    miniFluo.stop();
    cluster.stop();
    FileUtils.deleteQuietly(dir);
  }
}
