package org.anc.lapps.serialization.gate;

import gate.Gate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Keith Suderman
 */
public abstract class ConverterBase
{
   private static final Logger logger = LoggerFactory.getLogger(ConverterBase.class);
   private static Boolean initialized = false;

   protected Throwable savedException = null;
   protected static final Configuration K = new Configuration();

   public ConverterBase()
   {
      synchronized (initialized) {
         if (!initialized)
         {
            initialized = true;  // We only try this once.
            try
            {
               logger.info("Configuring Gate.");
               File gateHome = new File(K.GATE_HOME);
               if (!gateHome.exists())
               {
                  logger.error("Gate home not found: {}", gateHome.getPath());
                  savedException = new FileNotFoundException(K.GATE_HOME);
                  return;
               }
               logger.info("Gate home: {}", K.GATE_HOME);
               File plugins = new File(K.PLUGINS_HOME);
               if (!plugins.exists())
               {
                  logger.error("Gate plugins not found: {}", plugins.getPath());
                  savedException = new FileNotFoundException(K.PLUGINS_HOME);
                  return;
               }
               logger.info("Plugins home: {}", K.PLUGINS_HOME);
               File siteConfig = new File(K.SITE_CONFIG);
               if (!siteConfig.exists())
               {
                  logger.error("Site config not found: {}", siteConfig.getPath());
                  savedException = new FileNotFoundException(K.SITE_CONFIG);
                  return;
               }
               logger.info("Site config: {}", K.SITE_CONFIG);
               File userConfig = new File(K.USER_CONFIG);
               if (!userConfig.exists())
               {
                  logger.error("User config not found: {}", userConfig.getPath());
                  savedException = new FileNotFoundException(K.USER_CONFIG);
                  return;
               }
               logger.info("User config: {}", K.USER_CONFIG);
               Gate.setGateHome(gateHome);
               Gate.setSiteConfigFile(siteConfig);
               Gate.setPluginsHome(plugins);
               Gate.setUserConfigFile(userConfig);

               try
               {
                  logger.info("Initializing GATE");
                  Gate.init();
               }
               catch (Exception e)
               {
                  logger.error("Error initializing GATE.", e);
                  savedException = e;
                  return;
               }

               File[] files = plugins.listFiles();
               for (File directory : files)
               {
                  if (directory.isDirectory())
                  {
                     logger.info("Registering plugin: {}", directory.getPath());
                     Gate.getCreoleRegister().registerDirectories(directory.toURI().toURL());
                  }
               }
            }
            catch (Exception e)
            {
               logger.error("Unable to configure GATE.", e);
               logger.warn(e.getMessage());
            }

         }
      }
}
}
