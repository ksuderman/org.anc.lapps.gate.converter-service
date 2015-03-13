package org.anc.lapps.converters.gate

import org.junit.Test
import org.lappsgrid.api.WebService
import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.metadata.ServiceMetadata
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer

import static org.junit.Assert.*

/**
 * @author Keith Suderman
 */
class MetadataTests {
    @Test
    void testGateToJsonMetadata() {
        WebService service = new GateToJsonConverter();
        String json = service.getMetadata()
        assertNotNull json
        Data<String> data = Serializer.parse(json, Data)
        assertFalse data.payload, data.discriminator == Discriminators.Uri.ERROR
        assertTrue "Invalid discrimintator: ${data.discriminator}", data.discriminator == Discriminators.Uri.META
        ServiceMetadata metadata = Serializer.parse(data.payload, ServiceMetadata) //new ServiceMetadata(data.payload)
        assertTrue metadata.allow == Discriminators.Uri.ANY
        assertTrue metadata.vendor == "http://www.anc.org"
        assertTrue metadata.version == Version.getVersion()
        assertTrue metadata.name == GateToJsonConverter.class.canonicalName
        List<String> list = metadata.requires.format
        assertTrue "Requires format is empty.", list.size() > 0
        String format = list[0]
        println "Required format is ${format}"
        assertTrue metadata.requires.format[0] == Discriminators.Uri.GATE
        list = metadata.produces.format
        assertTrue "Produces format is empty.", list.size() > 0
        assertTrue metadata.produces.format[0] == Discriminators.Uri.LAPPS
    }

    @Test
    void testJsonToGateMetadata()
    {
        WebService service = new JsonToGateConverter();
        String json = service.getMetadata()
        assertNotNull json
        Data<String> data = Serializer.parse(json, Data)
        assertFalse data.payload, data.discriminator == Discriminators.Uri.ERROR
        assertTrue data.discriminator == Discriminators.Uri.META
        ServiceMetadata metadata = Serializer.parse(data.payload, ServiceMetadata) //new ServiceMetadata(data.payload)
        assertTrue metadata.allow == Discriminators.Uri.ANY
        assertTrue metadata.vendor == "http://www.anc.org"
        assertTrue metadata.version == Version.getVersion()
        assertTrue metadata.name == JsonToGateConverter.class.canonicalName
        List<String> list = metadata.requires.format
        assertTrue "Requires format is empty.", list.size() > 0
        String format = list[0]
        println "Required format is ${format}"
        assertTrue metadata.requires.format[0] == Discriminators.Uri.LAPPS
        list = metadata.produces.format
        assertTrue "Produces format is empty.", list.size() > 0
        assertTrue metadata.produces.format[0] == Discriminators.Uri.GATE
    }
}
