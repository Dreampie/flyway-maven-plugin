package cn.dreampie;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by wangrenhui on 14-5-5.
 */
public class DbConfig {
  private Log log = LogKit.getLog();

  private String config = "application.properties";
  private Properties properties;

  public DbConfig() {
    properties = readProperties(config);
  }

  public DbConfig(String config) {
    this.config = config;
    properties = readProperties(config);
  }

  public Properties readProperties(String filePath) {
    Properties properties = new Properties();
    try {
      InputStream in = new BufferedInputStream(new FileInputStream(filePath));
      properties.load(in);
    } catch (Exception e) {
      throw new FlywayException(e.getMessage());
    }
    return properties;
  }

  public List<String> getAllDbNames() {
    Set<String> dbNames = Sets.newHashSet();
    Enumeration enums = properties.keys();
    String key = null;
    String dbName = null;
    while (enums.hasMoreElements()) {
      key = enums.nextElement() + "";
      if (key.startsWith("db.")) {
        dbName = key.split("\\.")[1];
        dbNames.add(dbName);
      }
    }
    return new ArrayList<String>(dbNames);
  }

  public Map<String, DbSource> getAllDbSources() {
    Map<String, DbSource> dbSourceMap = Maps.newHashMap();
    List<String> dbNames = getAllDbNames();
    for (String dbName : dbNames) {
      dbSourceMap.put(dbName, new DbSource(properties.getProperty("db." + dbName + ".url"), properties.getProperty("db." + dbName + ".user"), properties.getProperty("db." + dbName + ".password")));
    }
    return dbSourceMap;
  }


  public boolean initOnMigrate(String dbName) {
    return properties.getProperty("flyway." + dbName + ".migration.initOnMigrate", "false").equals("true");
  }

  public boolean migrateAuto(String dbName) {
    return properties.getProperty("flyway." + dbName + ".migration.auto", "false").equals("true");
  }

  public boolean isClean(String dbName) {
    return properties.getProperty("flyway." + dbName + ".valid.clean", "false").equals("true");
  }

  public boolean isDev() {
    return properties.getProperty("flyway.devMode", "false").equals("true");
  }
}
