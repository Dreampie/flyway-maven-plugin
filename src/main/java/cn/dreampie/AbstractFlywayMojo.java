package cn.dreampie;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.collect.Maps;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.internal.info.MigrationInfoDumper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Goal which touches a timestamp file.
 */
public abstract class AbstractFlywayMojo extends AbstractMojo {

  private Log log = getLog();

  @Parameter(defaultValue = "${project.build.directory}/src/main/resources/application.properties", required = true)
  private String config;

  @Parameter(defaultValue = "filesystem:${project.build.directory}/db/migration/", required = true)
  private String location;

  @Parameter(defaultValue = "false")
  private boolean skip;

  private DbConfig dbConfig;

  public void info() {
    dbConfig = new DbConfig(config);
    Map<String, Flyway> flywayMap = flyways();
    for (Flyway flyway : flywayMap.values()) {
      log.info("\n" + MigrationInfoDumper.dumpToAsciiTable(flyway.info().all()));
    }
  }

  public void init() {
    dbConfig = new DbConfig(config);
    log.info("flyway init begin!");
    Map<String, Flyway> flywayMap = flyways();
    for (Flyway flyway : flywayMap.values()) {
      flyway.init();
    }
    log.info("flyway init end!");
  }

  public void repair() {
    dbConfig = new DbConfig(config);
    log.info("flyway repair begin!");
    Map<String, Flyway> flywayMap = flyways();
    for (Flyway flyway : flywayMap.values()) {
      flyway.repair();
    }
    log.info("flyway repair end!");
  }

  public void validate() {
    dbConfig = new DbConfig(config);
    log.info("flyway validate begin!");
    Map<String, Flyway> flywayMap = flyways();
    for (Flyway flyway : flywayMap.values()) {
      flyway.validate();
    }
    log.info("flyway validate end!");
  }


  public void migrate() {
    dbConfig = new DbConfig(config);
    log.info("flyway migrate begin!");
    List<String> dbNames = dbConfig.getAllDbNames();
    boolean dev = dbConfig.isDev();
    for (String dbName : dbNames) {
      boolean auto = dbConfig.migrateAuto(dbName);
      if (dev || auto) {
        migrateAutomatically(dbName);
      } else {
        checkState(dbName);
      }
    }
    log.info("flyway migrate end!");
  }

  public void clean() {
    dbConfig = new DbConfig(config);
    log.info("flyway clean begin!");
    Map<String, Flyway> flywayMap = flyways();
    for (Flyway flyway : flywayMap.values()) {
      flyway.clean();
    }
    log.info("flyway clean end!");
  }

  private Map<String, Flyway> flyways() {
    Map<String, Flyway> flywayMap = Maps.newHashMap();
    Map<String, DbSource> dbSourceMap = dbConfig.getAllDbSources();
    DbSource dbSource = null;
    String migrationFilesLocation = null;
    Flyway flyway = null;
    for (String dbName : dbSourceMap.keySet()) {
      migrationFilesLocation = location + dbName;
      log.info("location:" + migrationFilesLocation);
      dbSource = dbSourceMap.get(dbName);
      flyway = new Flyway();
      flyway.setDataSource(dbSource.url, dbSource.user, dbSource.password);
      flyway.setLocations(migrationFilesLocation);
      if (dbConfig.isClean(dbName)) {
        flyway.setCleanOnValidationError(true);
      }
      if (dbConfig.initOnMigrate(dbName)) {
        flyway.setInitOnMigrate(true);
      }
      flywayMap.put(dbName, flyway);
    }
    return flywayMap;
  }

  private void migrateAutomatically(String dbName) {
    Map<String, Flyway> flywayMap = flyways();
    flywayMap.get(dbName).migrate();
  }

  private void cleanAutomatically(String dbName) {
    Map<String, Flyway> flywayMap = flyways();
    flywayMap.get(dbName).clean();
  }

  private void checkState(String dbName) {
    Map<String, Flyway> flywayMap = flyways();

    MigrationInfo[] pendingMigrations = flywayMap.get(dbName).info().pending();

    if (pendingMigrations != null) {
      throw new RuntimeException(dbName + "-" + Arrays.toString(pendingMigrations));
    }
  }
}
