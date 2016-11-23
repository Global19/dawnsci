/*-
 * Copyright 2016 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.dawnsci.json.test.marshaller;

import org.eclipse.dawnsci.analysis.api.persistence.IMarshaller;
import org.eclipse.dawnsci.json.test.testobject.ITestTypeNonRegistered;

public class TestTypeNonRegisteredMarshaller implements IMarshaller {

	@Override
	public Class<ITestTypeNonRegistered> getObjectClass() {
		return ITestTypeNonRegistered.class;
	}

	@Override
	public Class<TestTypeNonRegisteredSerializer> getSerializerClass() {
		return TestTypeNonRegisteredSerializer.class;
	}

	@Override
	public Class<TestTypeNonRegisteredDeserializer> getDeserializerClass() {
		return TestTypeNonRegisteredDeserializer.class;
	}
}
