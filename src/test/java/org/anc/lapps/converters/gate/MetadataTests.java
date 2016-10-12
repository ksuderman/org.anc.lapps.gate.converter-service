package org.anc.lapps.converters.gate;

import org.junit.Test;
import org.lappsgrid.api.WebService;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;

import java.util.List;
import java.util.Map;

import static org.lappsgrid.discriminator.Discriminators.Uri;
import static org.junit.Assert.*;

/**
 * @author Keith Suderman
 */
public class MetadataTests
{
	@Test
	public void testGateToJsonMetadata() {
		System.out.println("MetadataTests.testGateToJsonMetadata");
		WebService service = new GateToJsonConverter();
		String json = service.getMetadata();
		assertNotNull(json);
		Data<Object> data = Serializer.parse(json, Data.class);
		check(data.getPayload().toString(), Uri.META, data.getDiscriminator());
		ServiceMetadata metadata = new ServiceMetadata((Map)data.getPayload());
		checkCommonMetadata(metadata, GateToJsonConverter.class);
		List<String> list = metadata.getRequires().getFormat();
		assertTrue("Requires format is empty.", list.size() > 0);
		String format = list.get(0);
		check(Uri.GATE, format);
		list = metadata.getProduces().getFormat();
		assertTrue("Produces format is empty.", list.size() > 0);
		check(Uri.LAPPS, list.get(0));
	}

	@Test
	public void testJsonToGateMetadata()
	{
		System.out.println("MetadataTests.testJsonToGateMetadata");
		WebService service = new JsonToGateConverter();
		String json = service.getMetadata();
		assertNotNull(json);
		Data<Object> data = Serializer.parse(json, Data.class);
		check(data.getPayload().toString(), Uri.META, data.getDiscriminator());
		ServiceMetadata metadata = new ServiceMetadata((Map)data.getPayload());
		checkCommonMetadata(metadata, JsonToGateConverter.class);
		List<String> list = metadata.getRequires().getFormat();
		assertTrue("Requires format is empty.", list.size() > 0);
		String format = list.get(0);
		check(Uri.LAPPS, format);
		list = metadata.getProduces().getFormat();
		assertTrue("Produces format is empty.", list.size() > 0);
		check(Uri.GATE, list.get(0));
	}

	protected void checkCommonMetadata(ServiceMetadata metadata, Class<?> theClass)
	{
		check(Uri.ANY, metadata.getAllow());
		check("http://www.anc.org", metadata.getVendor());
		check(Version.getVersion(), metadata.getVersion());
		check(theClass.getCanonicalName(), metadata.getName());
	}

	protected void check(String expected, String actual)
	{
		String message = String.format("Expected %s Found %s", expected, actual);
	}

	protected void check(String message, String expected, String actual)
	{
		if (!expected.equals(actual))
		{
			fail(message);
		}
	}
}
