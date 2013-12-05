package org.anc.lapps.serialization.gate;

import gate.Document;
import org.anc.lapps.serialization.Container;
import org.anc.lapps.serialization.Serializer;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;

/**
 * @author Keith Suderman
 */
public class JsonToGateConverter implements WebService
{
   public JsonToGateConverter()
   {

   }

   public long[] produces()
   {
      return new long[] { Types.GATE };
   }

   public long[] requires()
   {
      return new long[] { Types.JSON_LD };
   }

   public Data configure(Data input)
   {
      return DataFactory.error("Unsupported operation.");
   }

   public Data execute(Data input)
   {
      if (input.getDiscriminator() != Types.JSON_LD) {
         return DataFactory.error("Invalid input type. Expected JSON_LD (" + Types.JSON_LD + ")");
      }
      Container container = new Container(input.getPayload());
      Document document = GateSerializer.convertToDocument(container);
      return new Data(Types.GATE, document.toXml());
   }
}
