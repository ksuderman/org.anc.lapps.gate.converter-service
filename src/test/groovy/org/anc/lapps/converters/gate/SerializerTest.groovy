package org.anc.lapps.converters.gate

import gate.Gate
import org.anc.lapps.gate.serialization.GateSerializer
import org.anc.lapps.serialization.Container
import org.anc.lapps.serialization.ProcessingStep
import org.anc.resource.ResourceLoader
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.discriminator.DiscriminatorRegistry
import org.lappsgrid.discriminator.Types
import org.lappsgrid.discriminator.Uri
import org.lappsgrid.metadata.ServiceMetadata

import static org.junit.Assert.*
import org.lappsgrid.api.Data
import org.lappsgrid.api.WebService
import org.lappsgrid.core.DataFactory

/**
 * @author Keith Suderman
 */
class SerializerTest {
    String xml
    Data data
    static boolean init = true

    @Before
    void setup() {
        xml = ResourceLoader.loadString('GateTokens.xml')
        data = DataFactory.gateDocument(xml)
        if (init) {
            init = false
            Gate.setGateHome(new File('/usr/share/gate'))
            Gate.init()
        }
    }

    @After
    void tearDown() {
        xml = null
        data = null
    }

    @Ignore
    void testSerializer() {
        gate.Document document = gate.Factory.newDocument(xml)
        String json = GateSerializer.toPrettyJson(document)
        Container container = new Container(json)
        int nSteps = container.steps.size()
        assertTrue("Wrong number of steps. Expected 1 found ${nSteps}", nSteps == 1)
        ProcessingStep step = container.steps[0]
        int nAnnotations = step.annotations.count { it.type == 'Token' }
        assertTrue("Wrong number of annotations. Expected 7 found ${nAnnotations}", nAnnotations == 7)
        assertNotNull("Step does not contain tokens.", step.metadata?.contains.Token)
    }

    @Test
    void textConverter() {
        WebService service = new GateToJsonConverter();
        Data result = service.execute(data)
        assertTrue(result.payload, result.discriminator != Types.ERROR)
        assertTrue(result.discriminator == Types.JSON)
        Container container = new Container(result.payload)
        int nSteps = container.steps.size()
        assertTrue("Wrong number of steps. Expected 1 found ${nSteps}", nSteps == 1)
        ProcessingStep step = container.steps[0]
        int nAnnotations = step.annotations.count { it.type == 'Token' }
        assertTrue("Wrong number of annotations. Expected 7 found ${nAnnotations}", nAnnotations == 7)
        assertNotNull("Step does not contain tokens.", step.metadata?.contains.Token)
    }

    @Test
    void testGateToJsonMetadata() {
        WebService service = new GateToJsonConverter();
        Data data = service.getMetadata()
        assertTrue data.discriminator, data.discriminator == Uri.META
        ServiceMetadata metadata = new ServiceMetadata(data.payload)
        assertTrue metadata.allow == DiscriminatorRegistry.getUri('any')
        assertTrue metadata.requires.format == Uri.GATE
        assertTrue metadata.produces.format == Uri.LAPPS
    }
}
