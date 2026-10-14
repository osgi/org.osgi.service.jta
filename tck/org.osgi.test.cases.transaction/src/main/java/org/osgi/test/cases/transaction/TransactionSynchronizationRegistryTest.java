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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;

import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.TransactionSynchronizationRegistry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.test.cases.transaction.util.SynchronizationImpl;
import org.osgi.test.cases.transaction.util.TransactionSynchronizationRegistryFactory;

/**
 * @version $Rev$ $Date$
 *
 * transaction SynchronizationRegistry test, test transactionKey, transaction status, etc
 * 
 *
 */
public class TransactionSynchronizationRegistryTest extends TransactionTestBundleControl {

    @BeforeEach
    public void setUp() throws Exception {
		super.setUp();
        super.setUpTransactionManager();
    }
    
    @Test
    public void testTSR001() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();

        assertThat(tsr.getTransactionKey()).isNull();
    }

    @Test
    public void testTSR002() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();

        assertThat(tsr.getTransactionStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
    }

    @Test
    public void testTSR003() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();

        try
        {
            tm.begin();
            
            final String key = "resourceKey";
            final Object value = new Object();
            
            tsr.putResource(key, value);
            
            final Object retrievedResource = tsr.getResource(key);

            assertThat(value).isEqualTo(retrievedResource);

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail("");
        }
    }

    @Test
    public void testTSR004() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();

        try
        {
            tm.begin();

            assertThat(tsr.getTransactionKey()).isNotNull();

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail("");
        }
    }

    @Test
    public void testTSR005() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
    
        try
        {
            tm.begin();
    
            assertThat(tsr.getTransactionStatus()).isEqualTo(Status.STATUS_ACTIVE);

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail("");
        }
    }

    @Test
    public void testTSR006() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
    
        try
        {
            tm.begin();
    
            final SynchronizationImpl s = new SynchronizationImpl();

            tsr.registerInterposedSynchronization(s);

            tm.commit();

            assertThat(!s.beforeCompletionCalled() || s.afterCompletionStatus() != Status.STATUS_COMMITTED).isFalse();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail("");
        }
    }

    @Test
    public void testTSR007() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
    
        try
        {
            tm.begin();

            assertThat(tsr.getRollbackOnly()).isFalse();

            tsr.setRollbackOnly();

            assertThat(!tsr.getRollbackOnly() || tsr.getTransactionStatus() != Status.STATUS_MARKED_ROLLBACK).isFalse();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail("");
        }

        assertThatExceptionOfType(RollbackException.class)
        		.isThrownBy(() -> tm.commit());
    }
}
