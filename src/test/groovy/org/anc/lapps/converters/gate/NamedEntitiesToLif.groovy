package org.anc.lapps.converters.gate

import groovy.json.JsonOutput
import org.junit.*
import org.lappsgrid.api.WebService

import static org.junit.Assert.*

/**
 * @author Keith Suderman
 */
class NamedEntitiesToLif {

    @Test
    void testConversion() {
        ClassLoader loader = this.class.classLoader;
        InputStream stream = loader.getResourceAsStream('NamedEntities.json')
        if (stream == null) {
            throw new IOException("Unable to load test resource")
        }
        String json = stream.text
        stream.close()
        //println json
        WebService service = new GateToJsonConverter();
        json = service.execute(json)
        println JsonOutput.prettyPrint(json)

    }
}