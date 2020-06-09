package org.egov.prscp.web.models;

import org.junit.*;
import static org.junit.Assert.*;

public class MapPressNoteTest {
	
	@Test
	public void testMapPressNote_1()
		throws Exception {
		String mapPressNoteUuid = "";
		String pressMasterUuid = "";
		String pressNoteUuid = "";
		String isActive = "";
		String tenantId = "";

		MapPressNote result = new MapPressNote(mapPressNoteUuid, pressMasterUuid, pressNoteUuid, isActive, tenantId);

		assertNotNull(result);
		assertEquals("", result.getIsActive());
		assertEquals("", result.getPressNoteUuid());
		assertEquals("", result.getMapPressNoteUuid());
		assertEquals("", result.getPressMasterUuid());
		assertEquals("", result.getTenantId());
	}

	
	@Test
	public void testBuilder_1()
		throws Exception {

		MapPressNote.MapPressNoteBuilder result = MapPressNote.builder();

	
		assertNotNull(result);
		assertEquals("MapPressNote.MapPressNoteBuilder(mapPressNoteUuid=null, pressMasterUuid=null, pressNoteUuid=null, isActive=null, tenantId=null)", result.toString());
	}


	@Test
	public void testGetIsActive_1()
		throws Exception {
		MapPressNote fixture = new MapPressNote("", "", "", "", "");

		String result = fixture.getIsActive();

		assertEquals("", result);
	}

	
	@Test
	public void testGetMapPressNoteUuid_1()
		throws Exception {
		MapPressNote fixture = new MapPressNote("", "", "", "", "");

		String result = fixture.getMapPressNoteUuid();

		assertEquals("", result);
	}

	
	@Test
	public void testGetPressMasterUuid_1()
		throws Exception {
		MapPressNote fixture = new MapPressNote("", "", "", "", "");

		String result = fixture.getPressMasterUuid();

		assertEquals("", result);
	}

	@Test
	public void testGetPressNoteUuid_1()
		throws Exception {
		MapPressNote fixture = new MapPressNote("", "", "", "", "");

		String result = fixture.getPressNoteUuid();

		assertEquals("", result);
	}

	@Test
	public void testGetTenantId_1()
		throws Exception {
		MapPressNote fixture = new MapPressNote("", "", "", "", "");

		String result = fixture.getTenantId();

		assertEquals("", result);
	}

	
	@Test
	public void testSetIsActive_1()
		throws Exception {
		MapPressNote fixture = new MapPressNote("", "", "", "", "");
		String isActive = "";

		fixture.setIsActive(isActive);

	}


	@Test
	public void testSetMapPressNoteUuid_1()
		throws Exception {
		MapPressNote fixture = new MapPressNote("", "", "", "", "");
		String mapPressNoteUuid = "";

		fixture.setMapPressNoteUuid(mapPressNoteUuid);

	}

	
	@Test
	public void testSetPressMasterUuid_1()
		throws Exception {
		MapPressNote fixture = new MapPressNote("", "", "", "", "");
		String pressMasterUuid = "";

		fixture.setPressMasterUuid(pressMasterUuid);

	}

	
	@Test
	public void testSetPressNoteUuid_1()
		throws Exception {
		MapPressNote fixture = new MapPressNote("", "", "", "", "");
		String pressNoteUuid = "";

		fixture.setPressNoteUuid(pressNoteUuid);

	}

	@Test
	public void testSetTenantId_1()
		throws Exception {
		MapPressNote fixture = new MapPressNote("", "", "", "", "");
		String tenantId = "";

		fixture.setTenantId(tenantId);

	}

	@Before
	public void setUp()
		throws Exception {
		
	}

	@After
	public void tearDown()
		throws Exception {
		
	}

	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(MapPressNoteTest.class);
	}
}