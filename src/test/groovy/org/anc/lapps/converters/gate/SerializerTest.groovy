package org.anc.lapps.converters.gate

import gate.Gate
import org.anc.lapps.gate.serialization.GateSerializer
import org.anc.resource.ResourceLoader
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

import org.lappsgrid.metadata.ServiceMetadata
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

import static org.junit.Assert.*
import org.lappsgrid.api.WebService
import org.lappsgrid.core.DataFactory
import org.lappsgrid.serialization.*

import static org.lappsgrid.discriminator.Discriminators.Uri

/**
 * @author Keith Suderman
 */
//@Test
class SerializerTest {
    String xml
//    Data data
    String json
    static boolean init = true

    @BeforeClass
    static void init() {
        if (init) try {
            Gate.setGateHome(new File('/usr/share/lapps/gate'))
            Gate.setPluginsHome(new File('/usr/share/lapps/gate/plugins'))
            Gate.init()
            init = false
        }
        catch (Throwable t) {
            t.printStackTrace()
            init = false
        }
    }
    @Before
    void setup() {
        xml = ResourceLoader.loadString('GateTokens.xml')
        json = DataFactory.gateDocument(xml)
//        if (init) {
//            init = false
//        }
    }

    @After
    void tearDown() {
        xml = null
//        data = null
        json = null
    }

    @Test
    void testSerializer() {
        gate.Document document = gate.Factory.newDocument(xml)
        String json = GateSerializer.toPrettyJson(document)
        Data<Container> data = Serializer.parse(json, Data)
        Container container = data.getPayload()
        int nSteps = container.views.size()
        assertTrue("Wrong number of steps. Expected 1 found ${nSteps}", nSteps == 1)
        View step = container.views[0]
        step.annotations.each { Annotation a -> println "${a.label} ${a.type}" }
        int nAnnotations = step.annotations.count { it.label == 'Token' }
        assertTrue("Wrong number of annotation labels. Expected 7 found ${nAnnotations}", nAnnotations == 7)
        nAnnotations = step.annotations.count { it.type == Uri.TOKEN }
        assertTrue("Wrong number of annotation typess. Expected 7 found ${nAnnotations}", nAnnotations == 7)
        assertNotNull("Step does not contain tokens.", step.metadata?.contains.Token)
    }

    @Test
    void textConverter() {
        WebService service = new GateToJsonConverter();
        String resultJson = service.execute(json)
        Data result = Serializer.parse(resultJson, Data)
        assertFalse(result.discriminator == Uri.ERROR)
        assertEquals(result.discriminator, Uri.JSON_LD)
//        Container container = Serializer.parse(result.payload, Container)
        Container container = result.payload
        int nViews = container.views.size()
        assertTrue("Wrong number of views. Expected 1 found ${nViews}", nViews == 1)
        View step = container.views[0]
        int nAnnotations = step.annotations.count { it.label == 'Token' }
        assertTrue("Wrong number of annotations. Expected 7 found ${nAnnotations}", nAnnotations == 7)
        assertNotNull("View does not contain tokens.", step.metadata?.contains.Token)
    }

    @Test
    void testGateToJsonMetadata() {
        WebService service = new GateToJsonConverter();
        String json = service.getMetadata()
        assertNotNull json
        Data<String> data = Serializer.parse(json, Data)
        assertTrue data.discriminator == Uri.META
        ServiceMetadata metadata = Serializer.parse(data.payload, ServiceMetadata) //new ServiceMetadata(data.payload)
        assertTrue metadata.allow == Uri.ANY
        List<String> list = metadata.requires.format
        assertTrue "Requires format is empty.", list.size() > 0
        String format = list[0]
        println "Required format is ${format}"
        assertTrue metadata.requires.format[0] == Uri.GATE
        list = metadata.produces.format
        assertTrue "Produces format is emtpy.", list.size() > 0
        assertTrue metadata.produces.format[0] == Uri.LAPPS
    }

    @Test
    void testJsonToGateMetadata()
    {

    }
}
