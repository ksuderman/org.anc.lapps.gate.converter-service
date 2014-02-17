package org.anc.lapps.serialization.gate

/**
 * @author Keith Suderman
 */
class AnnotationMapper { //extends HashMap {
    static final Map MAP = [
            'Token':'tok',
            'Sentence':'s'
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
