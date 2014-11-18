package cn.dreampie;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

/**
 * Created by ice on 14-11-17.
 */
public class LogKit {
  private static Log log;


  public static void setLog(Log log) {
    LogKit.log = log;
  }

  public static Log getLog() {
    if (log == null) {
      log = new SystemStreamLog();
    }

    return log;
  }
}
