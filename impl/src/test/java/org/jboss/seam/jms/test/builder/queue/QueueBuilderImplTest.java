/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.jms.test.builder.queue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.QueueBuilder;
import org.jboss.seam.jms.QueueBuilderImpl;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class QueueBuilderImplTest {

	@Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(QueueBuilderImplTest.class,QueueBuilderImpl.class);
    }
	
	@Inject QueueBuilder queueBuilder;
	private static QueueTestListener ttl = new QueueTestListener();
	
	@Test
	public void testNewBuilder() {
		QueueBuilder tb = queueBuilder.newBuilder();
		Assert.assertFalse(tb.equals(queueBuilder));
	}
	
	@Test
	public void testDestination() {
		QueueBuilder tb = queueBuilder.newBuilder();
		tb.destination("myDestination");
		if(!(tb instanceof QueueBuilderImpl)) {
			Assert.assertFalse(true);
		}
		QueueBuilderImpl tbi = (QueueBuilderImpl)tb;
		List<String> destinations = tbi.getDestinations();
		Assert.assertEquals(1, destinations.size());
		Assert.assertEquals("myDestination",destinations.get(0));
	}
	
	private static void testMessageSent(boolean observed,Class<?> type,QueueTestListener ttl) {
		Assert.assertEquals(observed, ttl.isObserved());
		if(type == null) {
			Assert.assertTrue(ttl.getMessageClass() == null);
		} else {
			Assert.assertTrue(type.isAssignableFrom(ttl.getMessageClass()));
		}
	}
	
	@Test
	public void testListen() {
		queueBuilder.newBuilder().listen(ttl);
		testMessageSent(false,null,ttl);
	}
	
	@Test
	public void testSendMap() {
		Map mapData = new HashMap<String,String>();
		queueBuilder.newBuilder().destination("jms/Q").listen(ttl).send(mapData);
		testMessageSent(true,MapMessage.class,ttl);
	}
	@Test
	public void testSendString() {
		String data = "new data";
		queueBuilder.newBuilder().destination("jms/Q").listen(ttl).send(data);
		testMessageSent(true,TextMessage.class,ttl);
	}
	@Test
	public void testSendObject() {
		Object data = 33L;
		queueBuilder.newBuilder().destination("jms/Q").listen(ttl).send(data);
		testMessageSent(true,ObjectMessage.class,ttl);
	}
}
