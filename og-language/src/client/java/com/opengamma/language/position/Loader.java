/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.language.position;

import java.net.URI;

import net.sf.ehcache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.opengamma.core.position.impl.EHCachingPositionSource;
import com.opengamma.core.position.impl.RemotePositionSource;
import com.opengamma.language.config.Configuration;
import com.opengamma.language.context.ContextInitializationBean;
import com.opengamma.language.context.MutableGlobalContext;
import com.opengamma.language.function.FunctionProviderBean;
import com.opengamma.language.procedure.ProcedureProviderBean;
import com.opengamma.util.ArgumentChecker;

/**
 * Extends the contexts with support for positions, portfolios and trades (if available).
 */
public class Loader extends ContextInitializationBean {

  private static final Logger s_logger = LoggerFactory.getLogger(Loader.class);

  private String _configurationEntry = "positionSource";
  private Configuration _configuration;
  private Supplier<URI> _uri;
  private CacheManager _cacheManager = CacheManager.getInstance();

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

  public void setCacheManager(final CacheManager cacheManager) {
    ArgumentChecker.notNull(cacheManager, "cacheManager");
    _cacheManager = cacheManager;
  }

  public CacheManager getCacheManager() {
    return _cacheManager;
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
      s_logger.warn("Position support not available");
      return;
    }
    s_logger.info("Configuring position support");
    globalContext.setPositionSource(new EHCachingPositionSource(new RemotePositionSource(uri), getCacheManager()));
    globalContext.getFunctionProvider().addProvider(
        new FunctionProviderBean(FetchPortfolioFunction.INSTANCE, FetchPositionFunction.INSTANCE, GetPositionAttributeFunction.INSTANCE, GetPositionSecurityFunction.INSTANCE,
            PortfolioFunction.INSTANCE, PortfolioNodeFunction.INSTANCE, PortfoliosFunction.INSTANCE, PositionFunction.INSTANCE, SetPositionAttributeFunction.INSTANCE,
            PortfolioIdFunction.INSTANCE));
    globalContext.getProcedureProvider().addProvider(new ProcedureProviderBean(StorePortfolioProcedure.INSTANCE));
  }

}
