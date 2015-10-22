package org.anc.lapps.converters.gate;

import gate.Document;
import gate.Factory;
import org.anc.lapps.gate.serialization.GateSerializer;
import org.lappsgrid.annotations.ServiceMetadata;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.lappsgrid.discriminator.Discriminators.Uri;

/**
 * @author Keith Suderman
 */
@ServiceMetadata(
	description = "Converts LAPPS JSON/LD into GATE documents.",
	requires_format = "lapps",
	produces_format = "gate"
)
public class JsonToGateConverter extends ConverterBase
{
   protected static Logger logger = LoggerFactory.getLogger(JsonToGateConverter.class);

   public JsonToGateConverter()
   {
      super(JsonToGateConverter.class);
   }

   public String execute(String json)
   {
		Data<Map> data = Serializer.parse(json, Data.class);
		String discriminator = data.getDiscriminator();
//		Discriminator discriminator = DiscriminatorRegistry.getByUri(input.getDiscriminator());
      if (!discriminator.equals(Uri.JSON) && !discriminator.equals(Uri.LAPPS) && !discriminator.equals(Uri.JSON_LD)) {
         logger.error("Invalid input discriminator. Expected JSON but found " + discriminator);
         return DataFactory.error("Invalid input type: " + discriminator);
      }
      Data result = null;
      try
      {
         logger.debug("Converting JSON to GATE.");
         Container container = new Container(data.getPayload());
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
         return DataFactory.error(message, e);
      }
      return result.asJson();
   }
}
