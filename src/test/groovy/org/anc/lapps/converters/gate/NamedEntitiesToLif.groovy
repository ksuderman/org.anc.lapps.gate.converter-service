package org.anc.lapps.converters.gate

import groovy.json.JsonOutput
import org.junit.*
import org.lappsgrid.api.WebService
import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.serialization.DataContainer
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.Contains
import org.lappsgrid.serialization.lif.View

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
        //println JsonOutput.prettyPrint(json)

        DataContainer dc = Serializer.parse(json, DataContainer.class);
        Container container = dc.payload
        assert 1 == container.views.size()
        View view = container.views[0]
        def map = view.metadata["contains"]
        assert null != map

        List<Annotation> ne = view.annotations.findAll { it.atType == Discriminators.Uri.NE }
        assert ne.size() > 0
        ne.each { println it }

    }
}