package org.egov.pm.model;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NOCApplicationRemarkTest {

	@Test
	public void testNOCApplicationRemark_1() throws Exception {

		NOCApplicationRemark result = new NOCApplicationRemark();

		assertNotNull(result);
		assertEquals(null, result.getLastModifiedTime());
		assertEquals(null, result.getRemarkId());
		assertEquals(null, result.getRemark());
		assertEquals(null, result.getRemarkBy());
		assertEquals(null, result.getIsActive());
		assertEquals(null, result.getDocumentId());
		assertEquals(null, result.getCreatedBy());
		assertEquals(null, result.getCreatedTime());
		assertEquals(null, result.getTenantId());
		assertEquals(null, result.getLastModifiedBy());
		assertEquals(null, result.getApplicationUuid());
		assertEquals(null, result.getApplicationStatus());
	}

	@Test
	public void testNOCApplicationRemark_2() throws Exception {
		String remarkId = "";
		String applicationUuid = "";
		String applicationStatus = "";
		String remark = "";
		String remarkBy = "";
		Boolean isActive = new Boolean(true);
		String createdBy = "";
		Long createdTime = new Long(1L);
		String lastModifiedBy = "";
		Long lastModifiedTime = new Long(1L);
		String documentId = "";
		String tenantId = "";

		NOCApplicationRemark result = new NOCApplicationRemark(remarkId, applicationUuid, applicationStatus, remark,
				remarkBy, isActive, createdBy, createdTime, lastModifiedBy, lastModifiedTime, documentId, tenantId);

		assertNotNull(result);
		assertEquals(new Long(1L), result.getLastModifiedTime());
		assertEquals("", result.getRemarkId());
		assertEquals("", result.getRemark());
		assertEquals("", result.getRemarkBy());
		assertEquals(Boolean.TRUE, result.getIsActive());
		assertEquals("", result.getDocumentId());
		assertEquals("", result.getCreatedBy());
		assertEquals(new Long(1L), result.getCreatedTime());
		assertEquals("", result.getTenantId());
		assertEquals("", result.getLastModifiedBy());
		assertEquals("", result.getApplicationUuid());
		assertEquals("", result.getApplicationStatus());
	}

	@Test
	public void testBuilder_1() throws Exception {

		NOCApplicationRemark.NOCApplicationRemarkBuilder result = NOCApplicationRemark.builder();

		assertNotNull(result);
		assertEquals(
				"NOCApplicationRemark.NOCApplicationRemarkBuilder(remarkId=null, applicationUuid=null, applicationStatus=null, remark=null, remarkBy=null, isActive=null, createdBy=null, createdTime=null, lastModifiedBy=null, lastModifiedTime=null, documentId=null, tenantId=null)",
				result.toString());
	}

	@Test
	public void testGetApplicationStatus_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");

		String result = fixture.getApplicationStatus();

		assertEquals("", result);
	}

	@Test
	public void testGetApplicationUuid_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");

		String result = fixture.getApplicationUuid();

		assertEquals("", result);
	}

	@Test
	public void testGetCreatedBy_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");

		String result = fixture.getCreatedBy();

		assertEquals("", result);
	}

	@Test
	public void testGetCreatedTime_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");

		Long result = fixture.getCreatedTime();

		assertNotNull(result);
		assertEquals("1", result.toString());
		assertEquals((byte) 1, result.byteValue());
		assertEquals((short) 1, result.shortValue());
		assertEquals(1, result.intValue());
		assertEquals(1L, result.longValue());
		assertEquals(1.0f, result.floatValue(), 1.0f);
		assertEquals(1.0, result.doubleValue(), 1.0);
	}

	@Test
	public void testGetDocumentId_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");

		String result = fixture.getDocumentId();

		assertEquals("", result);
	}

	@Test
	public void testGetIsActive_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");

		Boolean result = fixture.getIsActive();

		assertNotNull(result);
		assertEquals("true", result.toString());
		assertEquals(true, result.booleanValue());
	}

	@Test
	public void testGetLastModifiedBy_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");

		String result = fixture.getLastModifiedBy();

		assertEquals("", result);
	}

	@Test
	public void testGetLastModifiedTime_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");

		Long result = fixture.getLastModifiedTime();

		assertNotNull(result);
		assertEquals("1", result.toString());
		assertEquals((byte) 1, result.byteValue());
		assertEquals((short) 1, result.shortValue());
		assertEquals(1, result.intValue());
		assertEquals(1L, result.longValue());
		assertEquals(1.0f, result.floatValue(), 1.0f);
		assertEquals(1.0, result.doubleValue(), 1.0);
	}

	@Test
	public void testGetRemark_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");

		String result = fixture.getRemark();

		assertEquals("", result);
	}

	@Test
	public void testGetRemarkBy_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");

		String result = fixture.getRemarkBy();

		assertEquals("", result);
	}

	@Test
	public void testGetRemarkId_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");

		String result = fixture.getRemarkId();

		assertEquals("", result);
	}

	@Test
	public void testGetTenantId_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");

		String result = fixture.getTenantId();

		assertEquals("", result);
	}

	@Test
	public void testSetApplicationStatus_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");
		String applicationStatus = "";

		fixture.setApplicationStatus(applicationStatus);

	}

	@Test
	public void testSetApplicationUuid_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");
		String applicationUuid = "";

		fixture.setApplicationUuid(applicationUuid);

	}

	@Test
	public void testSetCreatedBy_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");
		String createdBy = "";

		fixture.setCreatedBy(createdBy);

	}

	@Test
	public void testSetCreatedTime_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");
		Long createdTime = new Long(1L);

		fixture.setCreatedTime(createdTime);

	}

	@Test
	public void testSetDocumentId_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");
		String documentId = "";

		fixture.setDocumentId(documentId);

	}

	@Test
	public void testSetIsActive_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");
		Boolean isActive = new Boolean(true);

		fixture.setIsActive(isActive);

	}

	@Test
	public void testSetLastModifiedBy_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");
		String lastModifiedBy = "";

		fixture.setLastModifiedBy(lastModifiedBy);

	}

	@Test
	public void testSetLastModifiedTime_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");
		Long lastModifiedTime = new Long(1L);

		fixture.setLastModifiedTime(lastModifiedTime);

	}

	@Test
	public void testSetRemark_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");
		String remark = "";

		fixture.setRemark(remark);

	}

	@Test
	public void testSetRemarkBy_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");
		String remarkBy = "";

		fixture.setRemarkBy(remarkBy);

	}

	@Test
	public void testSetRemarkId_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");
		String remarkId = "";

		fixture.setRemarkId(remarkId);

	}

	@Test
	public void testSetTenantId_1() throws Exception {
		NOCApplicationRemark fixture = new NOCApplicationRemark("", "", "", "", "", new Boolean(true), "", new Long(1L),
				"", new Long(1L), "", "");
		String tenantId = "";

		fixture.setTenantId(tenantId);

	}

	@Test
	public void testNOCApplicationRemarks_1() throws Exception {
		NOCApplicationRemark.NOCApplicationRemarkBuilder builder = new NOCApplicationRemark.NOCApplicationRemarkBuilder();
		builder.applicationStatus("APPROVED");
		builder.applicationUuid("asdh8wdd182udn128dh");
		builder.createdBy("11");
		builder.createdTime(93872837482L);
		builder.documentId("f3f3f3f3fg56h56h");
		builder.isActive(true);
		builder.lastModifiedBy("455");
		builder.lastModifiedTime(2323523523L);
		builder.remark("Junit Test");
		builder.remarkBy("254");
		builder.remarkId("sdf4tf45y56h");
		builder.tenantId("ch");
		builder.build();

		NOCApplicationRemark.NOCApplicationRemarkBuilder builder2 = new NOCApplicationRemark.NOCApplicationRemarkBuilder();
		builder2.applicationStatus("APPROVED");
		builder2.applicationUuid("asdh8wdd182udn128dh");
		builder2.createdBy("11");
		builder2.createdTime(93872837482L);
		builder2.documentId("f3f3f3f3fg56h56h");
		builder2.isActive(true);
		builder2.lastModifiedBy("455");
		builder2.lastModifiedTime(2323523523L);
		builder2.remark("Junit Test");
		builder2.remarkBy("254");
		builder2.remarkId("sdf4tf45y56h");
		builder2.tenantId("ch");
		builder2.build();

		Assert.assertEquals(builder2.toString(), builder.toString());
	}

}