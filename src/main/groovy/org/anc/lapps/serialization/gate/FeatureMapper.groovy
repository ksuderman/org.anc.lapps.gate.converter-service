package org.anc.lapps.serialization.gate

/**
 * @author Keith Suderman
 */
class FeatureMapper {
    private static final Map FEATURES = [
            'category':'pos',
            'base':'lemma'
    ]

//    Map map = [:]

    public FeatureMapper() {
//        FEATURES.each { name, value ->
//            map[name] = value
//            map[valu] = name
//        }
    }

    String get(String key) {
        return FEATURES[key] ?: key
    }

//    String getAt(String key) {
//        return get(key)
//    }
}
