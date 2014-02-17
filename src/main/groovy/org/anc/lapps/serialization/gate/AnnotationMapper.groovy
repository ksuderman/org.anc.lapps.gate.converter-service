package org.anc.lapps.serialization.gate

import org.lappsgrid.vocabulary.*

/**
 * @author Keith Suderman
 */
class AnnotationMapper { //extends HashMap {
    static final Map MAP = [
            'Token':Annotations.TOKEN,
            'Sentence':Annotations.SENTENCE
    ]

    Map map = [:]

    public AnnotationMapper() {
        MAP.each { name, value ->
            map[name] = value
            map[value] = name
        }
    }

    String get(String key) {
        return MAP[key] ?: key
    }

//    String getAt(String key) {
//        return get(key)
//    }
}
