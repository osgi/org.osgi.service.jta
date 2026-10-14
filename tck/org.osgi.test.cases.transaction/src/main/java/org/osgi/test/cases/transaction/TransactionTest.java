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
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.test.cases.transaction.util.SynchronizationImpl;
import org.osgi.test.cases.transaction.util.XAResourceImpl;

/**
 * @version $Rev$ $Date$
 *
 * transaction test, test transaction status, commit/rollback and register synchronization
 * 
 *
 */
public class TransactionTest extends TransactionTestBundleControl {
    
    @BeforeEach
    public void setUp() throws Exception {
		super.setUp();
		super.setUpTransactionManager();
    }
    
    @Test
    public void testTI001() throws Exception {
        try
        {
            tm.begin();
            tm.commit();

            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(tm.getStatus());
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail("");
        }
    }

    @Test
    public void testTI002() throws Exception {       
        try
        {
            tm.begin();
            tm.rollback();

            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(tm.getStatus());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI003() throws Exception {
        try
        {
            tm.begin();

            assertThat(Status.STATUS_ACTIVE).isEqualTo(tm.getStatus());

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI004() throws Exception {
        try
        {
            tm.begin();
            final SynchronizationImpl s = new SynchronizationImpl();
            tm.getTransaction().registerSynchronization(s);
            tm.commit();

            assertThat(!s.beforeCompletionCalled() || s.afterCompletionStatus() != Status.STATUS_COMMITTED).isFalse();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI005() throws Exception {
        Transaction tx = null;
        try
        {
            tm.begin();
            tx = tm.getTransaction();
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }

        try
        {
            tx.registerSynchronization(new SynchronizationImpl());
            fail("");
        }
        catch(IllegalStateException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI007() throws Exception {
        try
        {
            tm.begin();
            final SynchronizationImpl s = new SynchronizationImpl();
            tm.getTransaction().registerSynchronization(s);
            tm.rollback();

            assertThat(s.beforeCompletionCalled() || s.afterCompletionStatus() != Status.STATUS_ROLLEDBACK).isFalse();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI008() throws Exception {
        final SynchronizationImpl s = new SynchronizationImpl();
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();
            tx.registerSynchronization(s);
            tx.enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_RBROLLBACK));
            tm.commit();
            fail("");
        }
        catch (RollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }

        assertThat(!s.beforeCompletionCalled() || s.afterCompletionStatus() != Status.STATUS_ROLLEDBACK).isFalse();
    }

    public void failingtestTI009() throws Exception {
        final SynchronizationImpl s = new SynchronizationImpl();
        try
        {
            tm.begin();
            tm.setRollbackOnly();
            tm.getTransaction().registerSynchronization(s);
            fail("");
        }
        catch (RollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }

        try
        {
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI010() throws Exception {
        try
        {
            tm.begin();
            tm.setRollbackOnly();

            assertThat(Status.STATUS_MARKED_ROLLBACK).isEqualTo(tm.getStatus());

            tm.commit();
            fail("");
        }
        catch (RollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI011() throws Exception {
        try
        {
            tm.begin();
            tm.setRollbackOnly();

            assertThat(Status.STATUS_MARKED_ROLLBACK).isEqualTo(tm.getStatus());

            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI012() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();
            tm.commit();
            tx.setRollbackOnly();
            fail("");
        }
        catch (IllegalStateException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI013() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl();
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
        
        assertThat(Status.STATUS_ROLLING_BACK).isEqualTo(xares.getStatusDuringRollback());
    }

    @Test
    public void testTI014() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl();
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(xares);
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
        
        assertThat(Status.STATUS_PREPARING).isEqualTo(xares.getStatusDuringPrepare());
    }

    @Test
    public void testTI015() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl();
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();
            tx.enlistResource(xares);
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
        
        assertThat(Status.STATUS_COMMITTING).isEqualTo(xares.getStatusDuringCommit());
    }

    @Test
    public void testTI017() throws Exception {
        try
        {
            tm.begin();
            tm.commit();
            tm.commit();
            fail("");
        }
        catch (IllegalStateException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI018() throws Exception {
        try
        {
            tm.begin();
            tm.commit();
            tm.rollback();
            fail("");
        }
        catch (IllegalStateException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI019() throws Exception {
        try
        {
            tm.begin();
            tm.rollback();
            tm.rollback();
            fail("");
        }
        catch (IllegalStateException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI020() throws Exception {
        try
        {
            tm.begin();
            tm.rollback();
            tm.commit();
            fail("");
        }
        catch (IllegalStateException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI021() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.suspend();

            assertThat(Status.STATUS_ACTIVE).isEqualTo(tx.getStatus());

            tm.resume(tx);
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI023() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().registerSynchronization(new SynchronizationImpl().setBeforeCompletionException(new RuntimeException()));
            tm.commit();
            fail("");
        }
        catch (RollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testTI024() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().registerSynchronization(new SynchronizationImpl().setAfterCompletionException(new RuntimeException()));
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }
}
