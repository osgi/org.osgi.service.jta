/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import javax.transaction.Status;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAResource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.test.cases.transaction.util.SimpleSynchronization;
import org.osgi.test.cases.transaction.util.SimpleTestResource;
import org.osgi.test.cases.transaction.util.TransactionSynchronizationRegistryFactory;

/**
 * @version $Rev$ $Date$
 *
 * simple transaction SynchronizationRegistry test, test basic commit, rollback
 * 
 *
 */
public class SimpleTransactionSynchronizationRegistryTest extends TransactionTestBundleControl {
    
    // 4.4 locating TransactionSynchronizationRegistry OSGi service
    @BeforeEach
    public void setUp() throws Exception {
		super.setUp();
        super.setUpTransactionManager();
    }
 
    // 4.3.3 4.4 simple test TransactionSynchronizationRegistry rollback without resource
    @Test
    public void testTransactionSynchronizationRegistry1() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
        
        SimpleSynchronization sync = new SimpleSynchronization();
      
        assertThat(tsr.getTransactionKey()).isNull();
        
        tm.begin();
        assertThat(tsr.getTransactionKey()).isNotNull();
        
        tsr.registerInterposedSynchronization(sync);  
        
        tsr.setRollbackOnly();
        assertThat(tsr.getRollbackOnly()).isTrue();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_MARKED_ROLLBACK);
        assertThat(sync.getBeforeValue()).doesNotEndWith("-before");
        assertThat(sync.getAfterValue()).doesNotEndWith("-after");
        
        tm.rollback();

        assertThat(tsr.getTransactionKey()).isNull();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        assertThat(sync.getBeforeValue()).doesNotEndWith("-before");
        assertThat(sync.getAfterValue()).endsWith("-after");
    }
    
    // 4.3.3 4.4 simple test TransactionSynchronizationRegistry rollback with resource
    @Test
    public void testTransactionSynchronizationRegistry2() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
        SimpleSynchronization sync = new SimpleSynchronization();
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
      
        assertThat(tsr.getTransactionKey()).isNull();
        
        tm.begin();
        assertThat(tsr.getTransactionKey()).isNotNull();
        
        tsr.registerInterposedSynchronization(sync);  
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        
        tsr.setRollbackOnly();
        assertThat(tsr.getRollbackOnly()).isTrue();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_MARKED_ROLLBACK);
        assertThat(sync.getBeforeValue()).doesNotEndWith("-before");
        assertThat(sync.getAfterValue()).doesNotEndWith("-after");
        
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        tm.rollback();

        assertThat(tsr.getTransactionKey()).isNull();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        assertThat(sync.getBeforeValue()).doesNotEndWith("-before");
        assertThat(sync.getAfterValue()).endsWith("-after");
    }
    
    // 4.3.3 4.4 simple test TransactionSynchronizationRegistry commit without resource
    @Test
    public void testTransactionSynchronizationRegistry3() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
        SimpleSynchronization sync = new SimpleSynchronization();
        
        assertThat(tsr.getTransactionKey()).isNull();
        
        tm.begin();
        assertThat(tsr.getTransactionKey()).isNotNull();
        
        tsr.registerInterposedSynchronization(sync);
        assertThat(sync.getBeforeValue()).doesNotEndWith("-before");
        assertThat(sync.getAfterValue()).doesNotEndWith("-after");

        tm.commit();

        assertThat(tsr.getTransactionKey()).isNull();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        assertThat(sync.getBeforeValue()).endsWith("-before");
        assertThat(sync.getAfterValue()).endsWith("-after");
    }
    
    
    // 4.3.3 4.4 simple test TransactionSynchronizationRegistry commit with resource
    @Test
    public void testTransactionSynchronizationRegistry4() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        SimpleSynchronization sync = new SimpleSynchronization();
        

        assertThat(tsr.getTransactionKey()).isNull();
        
        tm.begin();
        assertThat(tsr.getTransactionKey()).isNotNull();
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        
        tsr.registerInterposedSynchronization(sync);
        assertThat(sync.getBeforeValue()).doesNotEndWith("-before");
        assertThat(sync.getAfterValue()).doesNotEndWith("-after");

        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        tm.commit();

        assertThat(tsr.getTransactionKey()).isNull();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        assertThat(sync.getBeforeValue()).endsWith("-before");
        assertThat(sync.getAfterValue()).endsWith("-after");
    }
}
