package org.anc.lapps.converters.gate;

import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import org.anc.lapps.gate.serialization.GateSerializer;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.annotations.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lappsgrid.discriminator.Discriminators.Uri;

/**
 * @author Keith Suderman
 */
@ServiceMetadata(
	description = "Converts GATE documents to the LAPPS JSON/LD format.",
	requires_format = "gate",
	produces_format = "lapps"
)
public class GateToJsonConverter extends ConverterBase
{
   private static Logger logger = LoggerFactory.getLogger(GateToJsonConverter.class);

   public GateToJsonConverter()
   {
      super(GateToJsonConverter.class);
   }

//   public Data configure(Data input)
//   {
//      return DataFactory.error("Unsupported operation.");
//   }

   @Override
   public String execute(String input)
   {
		String result;
      Data<String> data = Serializer.parse(input, Data.class);
      if (!Uri.GATE.equals(data.getDiscriminator()))
      {
         return DataFactory.error("Invalid discriminator type: " + data.getDiscriminator());
      }
      String payload = data.getPayload();
      if (payload == null)
      {
         return DataFactory.error("Payload is empty");
      }
      Document document = null;
      try
      {
         logger.info("Converting document to JSON");
         document = Factory.newDocument(payload);
         logger.debug("Gate document created.");
         result = GateSerializer.toJson(document);
         logger.debug("Document serialized to JSON.");
//         result = new Data(Uri.JSON, json);
      }
//		catch (NullPointerException e)
      catch (NullPointerException | ResourceInstantiationException e)
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
