/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.language.holiday;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.opengamma.core.holiday.impl.CachedHolidaySource;
import com.opengamma.core.holiday.impl.RemoteHolidaySource;
import com.opengamma.language.config.Configuration;
import com.opengamma.language.context.ContextInitializationBean;
import com.opengamma.language.context.MutableGlobalContext;
import com.opengamma.util.ArgumentChecker;

/**
 * Extends the contexts with holiday support (if available).
 */
public class Loader extends ContextInitializationBean {

  private static final Logger s_logger = LoggerFactory.getLogger(Loader.class);

  private String _configurationEntry = "holidaySource";
  private Configuration _configuration;
  private Supplier<URI> _uri;

  public void setConfiguration(final Configuration configuration) {
    ArgumentChecker.notNull(configuration, "configuration");
    _configuration = configuration;
  }

  public Configuration getConfiguration() {
    return _configuration;
  }

  public void setConfigurationEntry(final String configurationEntry) {
    ArgumentChecker.notNull(configurationEntry, "configurationEntry");
    _configurationEntry = configurationEntry;
  }

  public String getConfigurationEntry() {
    return _configurationEntry;
  }

  // ContextInitializationBean

  @Override
  protected void assertPropertiesSet() {
    ArgumentChecker.notNull(getConfiguration(), "configuration");
    _uri = getConfiguration().getURIConfiguration(getConfigurationEntry());
  }

  @Override
  protected void initContext(final MutableGlobalContext globalContext) {
    final URI uri = _uri.get();
    if (uri == null) {
      s_logger.warn("Holiday support not available");
      return;
    }
    s_logger.info("Configuring holiday support");
    globalContext.setHolidaySource(new CachedHolidaySource(new RemoteHolidaySource(uri)));
  }

}
