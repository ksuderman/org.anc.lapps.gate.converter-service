package org.anc.lapps.converters.gate;

import org.junit.*;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @author Keith Suderman
 */
@Ignore
public class ConstantsTests
{
	public ConstantsTests()
	{

	}

	@Test
	public void testPaths()
	{
		Configuration K = new Configuration();
		exists(K.GATE_HOME);
		exists(K.PLUGINS_HOME);
		exists(K.SITE_CONFIG);
		exists(K.USER_CONFIG);
	}

	protected void exists(String path)
	{
		File file = new File(path);
		assertTrue("File not found: " + file.getPath(), file.exists());
	}
}
