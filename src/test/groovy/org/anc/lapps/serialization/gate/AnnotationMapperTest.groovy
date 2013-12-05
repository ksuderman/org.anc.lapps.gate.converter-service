package org.anc.lapps.serialization.gate

import org.junit.*
import static org.junit.Assert.*

/**
 * @author Keith Suderman
 */
@Ignore
class AnnotationMapperTest {

    AnnotationMapper mapper

    @Before
    void setup() {
        mapper = new AnnotationMapper()
    }

    @Test
    void testToken() {
        def expected = "tok"
        def actual = mapper['Token']
        assertTrue("Expected: ${expected} Actual: ${actual}", actual == expected)
    }

    @Test
    void testSentence() {
        def expected = 's'
        def actual = mapper['Sentence']
        assertTrue("Expected: ${expected} Actual: ${actual}", actual == expected)
    }

    @Test
    void testUndefined() {
        def expected = "foo"
        def actual = mapper['foo']
        assertTrue("Expected: ${expected} Actual: ${actual}", actual == expected)
    }
}
