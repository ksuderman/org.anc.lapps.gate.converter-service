package org.anc.lapps.serialization.gate;

import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import org.lappsgrid.api.*;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;

/**
 * @author Keith Suderman
 */
public class GateToJsonConverter implements WebService
{
   public GateToJsonConverter()
   {

   }

   public long[] produces()
   {
      return new long[] { Types.JSON_LD };
   }

   public long[] requires()
   {
      return new long[] { Types.GATE };
   }

   public Data configure(Data input)
   {
      return DataFactory.error("Unsupported operation.");
   }

   public Data execute(Data input)
   {
      Data result;
      try
      {
         Document document = Factory.newDocument(input.getPayload());
         String json = GateSerializer.toJson(document);
         result = new Data(Types.JSON_LD, json);
      }
      catch (ResourceInstantiationException e)
      {
         result = DataFactory.error(e.getMessage());
      }
      return result;
   }
}
