package org.anc.lapps.serialization.gate;

import gate.Document;
import org.anc.lapps.serialization.Container;
import org.anc.lapps.serialization.Serializer;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Keith Suderman
 */
public class JsonToGateConverter extends ConverterBase implements WebService
{
   protected static Logger logger = LoggerFactory.getLogger(JsonToGateConverter.class);

   public JsonToGateConverter()
   {

   }

   public long[] produces()
   {
      return new long[] { Types.GATE };
   }

   public long[] requires()
   {
      return new long[] { Types.JSON };
   }

   public Data configure(Data input)
   {
      return DataFactory.error("Unsupported operation.");
   }

   public Data execute(Data input)
   {
      if (input.getDiscriminator() != Types.JSON) {
         return DataFactory.error("Invalid input type. Expected JSON (" + Types.JSON + ")");
      }
      logger.debug("Converting JSON to GATE.");
      Container container = new Container(input.getPayload());
      logger.trace("Container created.");
      Document document = GateSerializer.convertToDocument(container);
      logger.trace("Document created.");
      return new Data(Types.GATE, document.toXml());
   }
}
