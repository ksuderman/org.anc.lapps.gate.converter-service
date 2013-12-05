package org.anc.lapps.serialization.gate

//import gate.*
import gate.Document
import gate.AnnotationSet
import gate.Factory
import gate.FeatureMap
import groovy.json.JsonSlurper
import org.anc.lapps.serialization.*

/**
 * @author Keith Suderman
 */
class GateSerializer {

//    static AnnotationMapper annotationMapper = new AnnotationMapper()
//    static FeatureMapper featureMapper = new FeatureMapper()

    static public String toJson(Document document) {
        return convertToContainer(document).toJson()
    }

    static public String toPrettyJson(Document document) {
        return convertToContainer(document).toPrettyJson()
    }

    static public Container convertToContainer(Document document) {

        Container container = new Container()
        container.text = document.content.getContent(0, document.content.size())
        addToContainer(container, document)
        return container
    }

    static public void addToContainer(Container container, Document document) {
        int counter = -1
        ProcessingStep step = new ProcessingStep()
        AnnotationSet aSet = document.getAnnotations()
        counter = addAnnotationSet(aSet, step, counter)
        document.namedAnnotationSets.each { name, set ->
            counter = addAnnotationSet(set, step, counter)
        }
        container.steps << step
    }

    private static int addAnnotationSet(AnnotationSet set, ProcessingStep step, int counter) {
        set.each { gateAnnotation ->
            Annotation annotation = new Annotation()
            annotation.metadata.aSet = set.getName()
            annotation.metadata.gateId = gateAnnotation.getId()
            annotation.id = "${++counter}"
            ++counter
            annotation.start = gateAnnotation.startNode.offset.longValue()
            annotation.end = gateAnnotation.endNode.offset.longValue()
            annotation.label = gateAnnotation.type //annotationMapper[gateAnnotation.type]
            gateAnnotation.features.each { key, value ->
                annotation.features[key] = value
            }
            step.annotations << annotation
        }
        return counter
    }

    static public Document convertToDocument(Container container) {
        Document document = Factory.newDocument(container.text)
        Map annotationSets = [:]

        container.steps.each { step ->
            step.annotations.each { annotation ->
                String setName = annotation.metadata.aSet ?: ''
                AnnotationSet annotationSet = document.getAnnotations(setName)
                if (annotationSet == null) {
                    annotationSet = document.getAnnotations()
                }
                Integer id = annotation.metadata.gateId ?: -1
                Long start = annotation.start
                Long end = annotation.end
                String label = annotation.label
                FeatureMap features = Factory.newFeatureMap()
                annotation.features.each { name, value ->
                    features.put(name, value)
                }
                annotationSet.add(id, start, end, label, features)
            }
        }
        return document
    }


}
