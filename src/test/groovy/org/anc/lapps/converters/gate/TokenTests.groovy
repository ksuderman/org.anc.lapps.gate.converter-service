package org.anc.lapps.converters.gate

import groovy.json.JsonOutput
import groovy.xml.XmlUtil
import org.junit.Test
import static org.junit.Assert.*
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.DataContainer
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

import static org.lappsgrid.discriminator.Discriminators.Uri

/**
 * @author Keith Suderman
 */
class TokenTests {

    @Test
    void testLifTokensToGate() {
        Container container = new Container()
        container.text = "Hello world."
        container.language = "en-US"
        View view = container.newView()
        view.addContains(Uri.TOKEN, "JUnit", "gate:tokens")
        view.newAnnotation("t1", Uri.TOKEN, 0, 5)
        view.newAnnotation("t2", Uri.TOKEN, 6, 11)
        view.newAnnotation("t3", Uri.TOKEN, 11, 12)

        DataContainer data = new DataContainer(container)
        String json = new JsonToGateConverter().execute(data.asJson())
        Data result = Serializer.parse(json, Data)
        def document = new XmlParser().parseText(result.payload)

        assertEquals document.AnnotationSet.Annotation[0].'@Type', 'Token'
        assertEquals document.AnnotationSet.Annotation[1].'@Type', 'Token'
        assertEquals document.AnnotationSet.Annotation[2].'@Type', 'Token'

    }

    @Test
    void testLifSentencesToGate() {
        Container container = new Container()
        container.text = "Hello world."
        container.language = "en-US"
        View view = container.newView()
        view.addContains(Uri.TOKEN, "JUnit", "gate:tokens")
        view.newAnnotation("s1", Uri.SENTENCE, 0, 12)

        DataContainer data = new DataContainer(container)
        String json = new JsonToGateConverter().execute(data.asJson())
        Data result = Serializer.parse(json, Data)
        def document = new XmlParser().parseText(result.payload)
        assertEquals document.AnnotationSet.Annotation[0].'@Type', 'Sentence'
    }
}
