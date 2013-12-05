package org.anc.lapps.serialization.gate

//import org.anc.lapps.client.RemoteService
//import org.lappsgrid.api.*
//import org.lappsgrid.discriminator.Types

import org.junit.*
import static org.junit.Assert.*

import org.anc.lapps.serialization.*

import gate.*
import org.anc.resource.*

/**
 * @author Keith Suderman
 */
public class GateSerializerTest {

    public static boolean initialized = false

    @Before
    void setup() {
        if (initialized) return

        initialized = true
        Gate.setGateHome(new File('/Applications/gate-7.0'))
        Gate.init()
    }

    @Test
    void gateToJsonTest() {
        //setup()
        Document document = getDocument() //Factory.newDocument(new File('org.anc.lapps.serialization.gate/src/test/resources/test-file.xml').toURI().toURL())
        Container container = GateSerializer.convertToContainer(document)
        println container.toJson()
    }

    @Test
    void testRoundTrip() {
        setup()
        Document document = getDocument() //Factory.newDocument(new File('org.anc.lapps.serialization.gate/src/test/resources/test-file.xml').toURI().toURL())
        Container container = GateSerializer.convertToContainer(document)
        document = GateSerializer.convertToDocument(container)
        println document.toXml()

    }

    Document getDocument() {
        ClassLoader loader = Thread.currentThread().contextClassLoader
        if (loader == null) {
            loader = GateSerializer.class.classLoader
        }
        URL url = loader.getResource('test-file.xml')
        if (url == null) {
            throw new NullPointerException('Unable to load test file.')
        }
        return Factory.newDocument(url)
    }

    /*
    @Test
    public void testWithServices() {
        setup()
        Document document = Factory.newDocument(new File('org.anc.lapps.serialization.gate/src/test/resources/test-file.xml').toURI().toURL())
        String base = "http://grid.anc.org:8080/service_manager/invoker"
        String user = "operator"
        String pass = "operator"
        RemoteService splitter = new RemoteService("${base}/anc:GATE_SPLITTER", user, pass)
        RemoteService tokenizer = new RemoteService("${base}/anc:GATE_TOKENZIER", user, pass)
        RemoteService tagger = new RemoteService("${base}/anc:GATE_TAGGER", user, pass)

        Data data = new Data(Types.GATE, document.toXml())
        data = splitter.execute(data)
        data = tokenizer.execute(data)
        data = tagger.execute(data)

        if (data.discriminator == Types.ERROR) {
            println data.payload
        }
        else {
            document = Factory.newDocument(data.payload)
            println GateSerializer.toPrettyJson(document)
        }
    }
    */
}
