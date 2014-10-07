package org.anc.lapps.converters.gate;

import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import org.anc.lapps.gate.serialization.GateSerializer;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.discriminator.Uri;
import org.lappsgrid.experimental.annotations.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Keith Suderman
 */
@ServiceMetadata(
		  vendor = "http://www.anc.org",
		  license = "apache2",
        description = "Converts GATE documents to the LAPPS JSON/LD format.",
        requires_format = "lapps",
        produces_format = "gate"
)
public class GateToJsonConverter extends ConverterBase implements WebService
{
   private static Logger logger = LoggerFactory.getLogger(GateToJsonConverter.class);

   public GateToJsonConverter()
   {
      super(GateToJsonConverter.class);
   }

   public Data configure(Data input)
   {
      return DataFactory.error("Unsupported operation.");
   }

   public Data execute(Data input)
   {
      Data result;
      Document document = null;
      try
      {
         logger.info("Converting document to JSON");
         document = Factory.newDocument(input.getPayload());
         logger.debug("Gate document created.");
         String json = GateSerializer.toJson(document);
         logger.debug("Document serialized to JSON.");
         result = new Data(Uri.JSON, json);
      }
      catch (ResourceInstantiationException e)
      {
         logger.error("Unable to convert document.", e);
         result = DataFactory.error(e.getMessage());
      }
      finally
      {
         Factory.deleteResource(document);
      }
      return result;
   }
}
