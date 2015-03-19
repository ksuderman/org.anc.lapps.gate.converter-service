package org.anc.lapps.converters.gate;

import gate.Gate;
import org.anc.io.UTF8Reader;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.experimental.annotations.CommonMetadata;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.anc.lapps.logging.*;

import java.io.*;

/**
 * @author Keith Suderman
 */
@CommonMetadata(
	vendor = "http://www.anc.org",
	license = "apache2"
)
public abstract class ConverterBase implements WebService
{
   private static final Logger logger = LoggerFactory.getLogger(ConverterBase.class);
   private static Boolean initialized = false;

   protected Throwable savedException = null;
   protected static final Configuration K = new Configuration();

   // The metadata will be loaded from the classpath at runtime. The metadata
   // itself is generated at compile time.
   protected String metadata;

   public ConverterBase(Class<?> converterClass)
   {
      synchronized (initialized) {
         if (!initialized)
         {
            logger.info("Initializing the GATE subsystem.");
            initialized = true;  // We only try this once.
            try
            {
               File gateHome = new File(K.GATE_HOME);
               if (!gateHome.exists())
               {
                  logger.error("Gate home not found: " + gateHome.getPath());
                  savedException = new FileNotFoundException(K.GATE_HOME);
                  return;
               }
               logger.info("Gate home: " + K.GATE_HOME);
               File plugins = new File(K.PLUGINS_HOME);
               if (!plugins.exists())
               {
                  logger.error("Gate plugins not found: " + plugins.getPath());
                  savedException = new FileNotFoundException(K.PLUGINS_HOME);
                  return;
               }
               logger.info("Plugins home: " + K.PLUGINS_HOME);
               File siteConfig = new File(K.SITE_CONFIG);
               if (!siteConfig.exists())
               {
                  logger.error("Site config not found: " + siteConfig.getPath());
                  savedException = new FileNotFoundException(K.SITE_CONFIG);
                  return;
               }
               logger.info("Site config: " + K.SITE_CONFIG);
               File userConfig = new File(K.USER_CONFIG);
               if (!userConfig.exists())
               {
                  logger.error("User config not found: " + userConfig.getPath());
                  savedException = new FileNotFoundException(K.USER_CONFIG);
                  return;
               }
               logger.info("User config: " + K.USER_CONFIG);
               Gate.setGateHome(gateHome);
               Gate.setSiteConfigFile(siteConfig);
               Gate.setPluginsHome(plugins);
               Gate.setUserConfigFile(userConfig);

               try
               {
                  logger.info("Initializing GATE");
                  Gate.runInSandbox(true);
                  Gate.init();
                  logger.info("GATE has been initialized.");
               }
               catch (RuntimeException e)
               {
                  logger.error("Error initializing GATE.", e);
                  savedException = e;
                  return;
               }
               catch (Exception e)
               {
                  logger.error("Error initializing GATE.", e);
                  savedException = e;
                  return;
               }

//               File[] files = plugins.listFiles();
//               for (File directory : files)
//               {
//                  if (directory.isDirectory())
//                  {
//                     logger.info("Registering plugin: " + directory.getPath());
//                     Gate.getCreoleRegister().registerDirectories(directory.toURI().toURL());
//                  }
//               }
            }
            catch (Exception e)
            {
               logger.error("Unable to configure GATE.", e);
               logger.warn(e.getMessage());
            }

         }
         logger.info("GATE has been initialized.");

         // Load the metadata for the service.
         String resourceName = "/metadata/" + converterClass.getName() + ".json";
         InputStream stream = this.getClass().getResourceAsStream(resourceName);
         if (stream == null)
         {
            metadata = DataFactory.error("Unable to locate service metadata: " + resourceName);
            return;
         }
         UTF8Reader reader = null;
         try
         {
            reader = new UTF8Reader(stream);
            String json = reader.readString();
            ServiceMetadata metadata = Serializer.parse(json, ServiceMetadata.class);
//            metadata = DataFactory.meta(json);
            this.metadata = new Data<ServiceMetadata>(Discriminators.Uri.META, metadata).asJson();
         }
         catch (IOException e)
         {
            metadata = DataFactory.error("Unable to load service metadata.", e);
         }

      }
   }

   @Override
   public String getMetadata()
   {
      return metadata;
   }
}
