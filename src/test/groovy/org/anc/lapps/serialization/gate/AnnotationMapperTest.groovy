package org.anc.lapps.serialization.gate

import org.junit.*
import static org.junit.Assert.*

/**
 * @author Keith Suderman
 */
class AnnotationMapperTest {

    AnnotationMapper mapper

    @Before
    void setup() {
        mapper = new AnnotationMapper()
    }

    @Test
    void testToken() {
        assertTrue('token' == mapper['Token'])
        assertTrue('Token' == mapper['token'])
    }

    @Test
    void testSentence() {
        assertTrue('sentence' == mapper['Sentence'])
        assertTrue('Sentence' == mapper['sentence'])
    }

    @Test
    void testUndefined() {
        def expected = "foo"
        def actual = mapper['foo']
        assertTrue("Expected: ${expected} Actual: ${actual}", actual == expected)
    }
}
