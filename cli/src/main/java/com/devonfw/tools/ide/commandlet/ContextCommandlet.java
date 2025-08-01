package com.devonfw.tools.ide.commandlet;

import com.devonfw.tools.ide.context.AbstractIdeContext;
import com.devonfw.tools.ide.context.IdeContext;
import com.devonfw.tools.ide.context.IdeStartContextImpl;
import com.devonfw.tools.ide.log.IdeLogLevel;
import com.devonfw.tools.ide.log.IdeLogListenerBuffer;
import com.devonfw.tools.ide.log.IdeSubLoggerOut;
import com.devonfw.tools.ide.property.FlagProperty;
import com.devonfw.tools.ide.property.LocaleProperty;

/**
 * An internal pseudo-commandlet to create the {@link IdeContext}. It shall not be registered in {@link CommandletManager}.
 */
public class ContextCommandlet extends Commandlet {

  private final FlagProperty batch;

  private final FlagProperty force;

  private final FlagProperty trace;

  private final FlagProperty debug;

  private final FlagProperty quiet;

  private final FlagProperty offline;

  private final FlagProperty privacy;

  private final FlagProperty skipUpdates;

  private final LocaleProperty locale;

  private IdeStartContextImpl startContext;

  /**
   * The constructor.
   */
  public ContextCommandlet() {

    super(null);
    this.batch = add(new FlagProperty("--batch", false, "-b"));
    this.force = add(new FlagProperty("--force", false, "-f"));
    this.trace = add(new FlagProperty("--trace", false, "-t"));
    this.debug = add(new FlagProperty("--debug", false, "-d"));
    this.quiet = add(new FlagProperty("--quiet", false, "-q"));
    this.privacy = add(new FlagProperty("--privacy", false, "-p"));
    this.offline = add(new FlagProperty("--offline", false, "-o"));
    this.skipUpdates = add(new FlagProperty("--skip-updates", false));
    this.locale = add(new LocaleProperty("--locale", false, null));
  }

  @Override
  public String getName() {

    return "context";
  }

  @Override
  public boolean isIdeHomeRequired() {

    return false;
  }

  @Override
  public void run() {

    IdeLogLevel logLevel = determineLogLevel();
    if (this.startContext == null) {
      IdeLogListenerBuffer listener = new IdeLogListenerBuffer();
      this.startContext = new IdeStartContextImpl(logLevel, level -> new IdeSubLoggerOut(level, null, true, logLevel, listener));
    } else if (this.context != null) {
      IdeStartContextImpl newStartContext = ((AbstractIdeContext) this.context).getStartContext();
      assert (this.startContext == newStartContext); // fast fail during development via assert
      this.startContext = newStartContext;
    }
    this.startContext.setBatchMode(this.batch.isTrue());
    this.startContext.setForceMode(this.force.isTrue());
    this.startContext.setQuietMode(this.quiet.isTrue());
    this.startContext.setOfflineMode(this.offline.isTrue());
    this.startContext.setPrivacyMode(this.privacy.isTrue());
    this.startContext.setSkipUpdatesMode(this.skipUpdates.isTrue());
    this.startContext.setLocale(this.locale.getValue());
  }

  private IdeLogLevel determineLogLevel() {
    IdeLogLevel logLevel = IdeLogLevel.INFO;
    if (this.trace.isTrue()) {
      logLevel = IdeLogLevel.TRACE;
    } else if (this.debug.isTrue()) {
      logLevel = IdeLogLevel.DEBUG;
    } else if (this.quiet.isTrue()) {
      logLevel = IdeLogLevel.WARNING;
    }
    return logLevel;
  }

  /**
   * @return the {@link IdeStartContextImpl} that has been created by {@link #run()}.
   */
  public IdeStartContextImpl getStartContext() {

    return this.startContext;
  }
}
