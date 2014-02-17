package org.anc.lapps.serialization.gate

import org.lappsgrid.vocabulary.Features

/**
 * @author Keith Suderman
 */
class FeatureMapper {
    private static final Map FEATURES = [
            'category':Features.CATEGORY,
            'base':Features.LEMMA
    ]

    Map map = [:]

    public FeatureMapper() {
        FEATURES.each { name, value ->
            map[name] = value
            map[value] = name
        }
    }

    String get(String key) {
        return FEATURES[key] ?: key
    }

//    String getAt(String key) {
//        return get(key)
//    }
}
