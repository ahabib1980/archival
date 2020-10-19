/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.archival.api;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.module.archival.ArchivedEncounter;
import org.openmrs.module.archival.api.dao.ArchivalDao;
import org.openmrs.module.archival.api.impl.ArchivalServiceImpl;

/**
 * This is a unit test, which verifies logic in ArchivalService. It doesn't extend
 * BaseModuleContextSensitiveTest, thus it is run without the in-memory DB and Spring context.
 */
public class ArchivalServiceTest {
	
	@InjectMocks
	ArchivalServiceImpl basicModuleService;
	
	@Mock
	ArchivalDao dao;
	
	@Mock
	UserService userService;
	
	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void saveItem_shouldSetOwnerIfNotSet() {
		//Given
		ArchivedEncounter item = new ArchivedEncounter();
		//item.setDescription("some description");
		
		//when(dao.saveItem(item)).thenReturn(item);
		
		User user = new User();
		when(userService.getUser(1)).thenReturn(user);
		
		//When
		//basicModuleService.saveItem(item);
		
		//Then
		//assertThat(item, hasProperty("owner", is(user)));
	}
}
