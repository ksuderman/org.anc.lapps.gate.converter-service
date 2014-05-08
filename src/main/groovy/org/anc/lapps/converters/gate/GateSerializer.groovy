package org.anc.lapps.converters.gate

import gate.AnnotationSet
import gate.Document
import gate.FeatureMap
import gate.util.InvalidOffsetException
import org.anc.lapps.serialization.Annotation
import org.anc.lapps.serialization.Container
import org.anc.lapps.serialization.ProcessingStep
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Keith Suderman
 */
class GateSerializer {
    private static Logger logger = LoggerFactory.getLogger(GateSerializer.class)

//    static AnnotationMapper annotationMapper = new AnnotationMapper()
//    static FeatureMapper featureMapper = new FeatureMapper()

    static public String toJson(Document document) {
        logger.debug("Generating JSON")
        return convertToContainer(document).toJson()
    }

    static public String toPrettyJson(Document document) {
        logger.debug("Generating pretty JSON")
        return convertToContainer(document).toPrettyJson()
    }

    static public Container convertToContainer(Document document) {
        logger.debug("Creating container.")
        Container container = new Container(false)
        container.text = document.content.getContent(0, document.content.size())
        addToContainer(container, document)
        logger.debug("Container created with {} steps", container.steps.size())
        return container
    }

    static public void addToContainer(Container container, Document document) {
        int counter = -1
        ProcessingStep step = new ProcessingStep()
        AnnotationSet aSet = document.getAnnotations()
        counter = addAnnotationSet(aSet, step, counter)
        document.namedAnnotationSets.each { name, set ->
            logger.debug("Processing annotation set {}", name)
            counter = addAnnotationSet(set, step, counter)
        }

        // TODO Need to filter out the document features added by GATE from the
        // features we are interested in.
        logger.debug("Processing document features.")
        FeatureMap features = document.getFeatures()
        if (features) {
            logger.trace("There are {} document features.", features.size())
            features.each { key, value ->
                logger.trace("Feature {} = \"{}\"", key, value)
                if (key.startsWith("lapps:") && value instanceof String) try {
                    key = key[6..-1]
                    String[] parts = value.split(' ')
                    if (parts.size() == 3) {
                        // TODO the stepNumber should be used to order the processing
                        // steps in the container.
                        String stepNumber = parts[0]
                        String producer = parts[1]
                        String type = parts[2]
                        step.addContains(key, producer, type)
                    }
                }
                catch (Throwable e) {
                    logger.error("Unable to save document feature.", e)
                }
            }
        }
        container.steps << step
        logger.debug("Document added to container.")
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
            //TODO map annotation names.
//            annotation.label = annotationMapper.get(gateAnnotation.type)
            annotation.label = gateAnnotation.type
            gateAnnotation.features.each { key, value ->
                // TODO map feature names
//                def mappedKey = featureMapper.get(key)
//                annotation.features[mappedKey] = value
                annotation.features[key] = value
            }
            step.annotations << annotation
        }
        return counter
    }

    static public Document convertToDocument(Container container) {
        logger.debug("Converting container to GATE document")
        Document document = gate.Factory.newDocument(container.text)
        logger.debug("Document created.")
        //Map annotationSets = [:]

        List list = []
        int i = 0
        container.steps.each { step ->
            logger.debug("Processing step ${++i}.")
            Map map = step.metadata.contains
            if (map) {
                list << new ListData(map:map, step:i);
            }
            step.annotations.each { annotation ->
                String setName = annotation.metadata.aSet ?: ''
                AnnotationSet annotationSet = document.getAnnotations(setName)
                if (annotationSet == null) {
                    annotationSet = document.getAnnotations()
                }
                Integer id = annotation.metadata.gateId ?: -1
                Long start = annotation.start
                Long end = annotation.end
                // TODO map annotation names
//                String label = annotationMapper.get(annotation.label)
                String label = annotation.label
                //println "${start}-${end} ${label}"
//                println "${annotation.label} -> ${label}"
                FeatureMap features = gate.Factory.newFeatureMap()
                annotation.features.each { name, value ->
                    // TODO map feature names
//                    features.put(featureMapper.get(name), value)
                    features.put(name, value)
                }
                try {
                    if (id > 0) {
                        annotationSet.add(id, start, end, label, features)
                    }
                    else {
                        annotationSet.add(start, end, label, features)
                    }
                }
                catch (InvalidOffsetException e) {
                    logger.error("Unable to add {} at offset {}", label, start);
                    throw e
                }
            }
        }
        FeatureMap features = document.getFeatures()
        list.each { data ->
            data.map.each { key,contains->
                String feature = "${data.step} ${contains.producer} ${contains.type}"
                features.put("lapps:${key}", feature)
            }
        }
        return document
    }

}

class ListData {
    Map map
    int step
}