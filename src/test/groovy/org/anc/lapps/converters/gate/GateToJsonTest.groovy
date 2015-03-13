package org.anc.lapps.converters.gate

import groovy.json.JsonOutput
import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.api.WebService
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Container

/**
 * @author Keith Suderman
 */
//@Ignore
class GateToJsonTest {

    @Ignore
    void testGateTokens()
    {
        URL url = this.class.getResource("/GateTagged.json")
        WebService service = new GateToJsonConverter();
        String json = service.execute(url.text)
        Data<Container> data = Serializer.parse(json, Data)
        //JsonOutput.prettyPrint(data.payload)
        Container container = new Container(data.payload)
        println Serializer.toPrettyJson(container)
    }


}
