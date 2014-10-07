package org.anc.lapps.converters.gate;

import gate.Document;
import gate.Factory;
import org.anc.lapps.gate.serialization.GateSerializer;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Discriminator;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.discriminator.Uri;
import org.lappsgrid.experimental.annotations.ServiceMetadata;
import org.lappsgrid.serialization.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Keith Suderman
 */
@ServiceMetadata(
	description = "Converts LAPPS JSON/LD into GATE documents.",
	requires_format = "lapps",
	produces_format = "gate"
)
public class JsonToGateConverter extends ConverterBase implements WebService
{
   protected static Logger logger = LoggerFactory.getLogger(JsonToGateConverter.class);

   public JsonToGateConverter()
   {
      super(JsonToGateConverter.class);
   }

   public Data configure(Data input)
   {
      return DataFactory.error("Unsupported operation.");
   }

   public Data execute(Data input)
   {
      Discriminator discriminator = DiscriminatorRegistry.getByUri(input.getDiscriminator());
      if (discriminator.getId() != Types.JSON) {
         logger.error("Invalid input discriminator. Expected JSON but found " + DiscriminatorRegistry.get(input.getDiscriminator()));
         return DataFactory.error("Invalid input type. Expected JSON (" + Types.JSON + ")");
      }
      Data result = null;
      try
      {
         logger.debug("Converting JSON to GATE.");
         Container container = new Container(input.getPayload());
         logger.trace("Container created.");
         Document document = GateSerializer.convertToDocument(container);
         logger.trace("Document created.");
         result = new Data(Uri.GATE, document.toXml());
         Factory.deleteResource(document);
      }
      catch (Exception e)
      {
         String message = "Unable to convert to GATE document";
         logger.error(message, e);
         result = DataFactory.error(message, e);
      }
      return result;
   }
}
