package org.anc.lapps.converters.gate;

import gate.Gate;
import gate.creole.ResourceInstantiationException;
import org.anc.lapps.gate.serialization.GateSerializer;
import org.anc.resource.ResourceLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Annotation;
import org.lappsgrid.serialization.lif.Container;
import org.lappsgrid.serialization.lif.View;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Keith Suderman
 */
public class SerializerTests
{
	private String xml;
	private String json;
	private static boolean init = true;

	protected static final int EXPECTED_ANNOTATIONS = 460;
	protected static final String EXPECTED_ERROR = "Expected " + EXPECTED_ANNOTATIONS + " annotations. Found ";

	@BeforeClass
	public static void init() {
		if (init) try {
			Gate.setGateHome(new File("/usr/share/lapps/gate"));
			Gate.setPluginsHome(new File("/usr/share/lapps/gate/plugins"));
			Gate.init();
			init = false;
		}
		catch (Throwable t) {
			t.printStackTrace();
			init = false;
		}
	}
	@Before
	public void setup() throws IOException
	{
		xml = ResourceLoader.loadString("GateTokens.xml");
		json = DataFactory.gateDocument(xml);
	}

	@After
	public void tearDown() {
		xml = null;
		json = null;
	}

	@Test
	public void testSerializer() throws ResourceInstantiationException
	{
		gate.Document document = gate.Factory.newDocument(xml);
		String json = GateSerializer.toPrettyJson(document);
		Data<Map> data = Serializer.parse(json, Data.class);
		Container container = new Container(data.getPayload());
		int nSteps = container.getViews().size();
		assertTrue("Wrong number of steps. Expected 1 found " + nSteps, nSteps == 1);
		View step = container.getView(0);
//		step.annotations.each { Annotation a -> println "${a.label} ${a.type}" }
//		int nAnnotations = step.annotations.count { it.label == "Token" }
		Selector labelSelector = new Selector() {
			public String select(Annotation a)
			{
				return a.getAtType();
			}
		};
		int nAnnotations = count(step.getAnnotations(), labelSelector, Discriminators.Uri.TOKEN);
		assertTrue(EXPECTED_ERROR + nAnnotations, nAnnotations == EXPECTED_ANNOTATIONS);

//		Selector typeSelector = new Selector() {
//			public String select(Annotation a) {
//				return a.getType();
//			}
//		};
////		nAnnotations = step.annotations.count { it.type == Discriminators.Uri.TOKEN }
//		nAnnotations = count(step.getAnnotations(), typeSelector, Discriminators.Uri.TOKEN);
//		assertTrue("Wrong number of annotation typess. Expected 7 found " + nAnnotations, nAnnotations == 7);
		Map<String,Object> metadata = step.getMetadata();
		assertNotNull(metadata);
		assertTrue(metadata.containsKey("contains"));
		Map contains = (Map) metadata.get("contains");
		System.out.println("Contains map");
		printMap(contains);
//		assertNotNull("Step does not contain tokens.", step.metadata?.contains.Token)
		assertTrue("View does not contain tokens.", contains.containsKey(Discriminators.Uri.TOKEN));
	}

	protected void printMap(Map map)
	{
		for (Object key : map.keySet())
		{
			System.out.println(key.toString());
		}
	}

	@Test
	public void textConverter() {
		WebService service = new GateToJsonConverter();
		String resultJson = service.execute(json);
		Data<Map> result = Serializer.parse(resultJson, Data.class);
		assertFalse(result.getDiscriminator().equals(Discriminators.Uri.ERROR));
		assertEquals(result.getDiscriminator(), Discriminators.Uri.LAPPS);
		Container container = new Container(result.getPayload());
		int nViews = container.getViews().size();
		assertTrue("Wrong number of views. Expected 1 found " + nViews, nViews == 1);
		View step = container.getView(0);
		Selector labelSelector = new Selector() {
			public String select(Annotation a)
			{
				return a.getAtType();
			}
		};
//		int nAnnotations = step.annotations.count { it.label == "Token" }
		int nAnnotations = count(step.getAnnotations(), labelSelector, Discriminators.Uri.TOKEN);
		assertTrue(EXPECTED_ERROR + nAnnotations, nAnnotations == EXPECTED_ANNOTATIONS);
//		assertNotNull("View does not contain tokens.", step.metadata?.contains.Token)
		assertTrue("View does not contain tokens", step.contains(Discriminators.Uri.TOKEN));
	}

	protected int count(List<Annotation> annotations, Selector selector, String type)
	{
		int count = 0;
		for (Annotation a : annotations)
		{
			String selected = selector.select(a);
			if (type.equals(selected))
			{
				++count;
			}
		}
		return count;
	}

	public interface Selector
	{
		String select(Annotation a);
	}
}
