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

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.xa.XAException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.test.cases.transaction.util.XAResourceImpl;

/**
 * @version $Rev$ $Date$
 *
 * user transaction test, test transaction status, xa resource state, various exceptions arisen from commit
 * 
 *
 */
public class UserTransactionTest extends TransactionTestBundleControl {
    
    @BeforeEach
    public void setUp() throws Exception {
		super.setUp();
        super.setUpTransactionManager();
        super.setUpUserTransaction();
    }
    
    @Test
    public void testUT001() throws Exception {
        assertThat(ut).isNotNull();
    }

    @Test
    public void testUT003() throws Exception {
        try
        {
            ut.begin();
            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT004() throws Exception {
        try
        {
            ut.begin();
            ut.begin();
            fail("");
        }
        catch (NotSupportedException e)
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
            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT005() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }

        assertThat(XAResourceImpl.allInState(XAResourceImpl.COMMITTED)).isTrue();
    }

    @Test
    public void testUT006() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl());
            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }

        assertThat(XAResourceImpl.allInState(XAResourceImpl.COMMITTED)).isTrue();
    }

    @Test
    public void testUT007() throws Exception {
        assertThatExceptionOfType(IllegalStateException.class)
        		.isThrownBy(() -> ut.commit());
    }

    @Test
    public void testUT008() throws Exception {
        try
        {
            ut.begin();
            ut.setRollbackOnly();
            ut.commit();
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
    public void testUT009() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_RBROLLBACK));
            ut.commit();
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
    public void testUT010() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.getTransaction().enlistResource(new XAResourceImpl());
            ut.commit();
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
            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(ut.getStatus());
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT011() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURRB));
            ut.commit();
            fail("");
        }
        catch (HeuristicRollbackException e)
        {
            // As expected
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
            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(ut.getStatus());
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT012() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX));
            ut.commit();
            fail("");
        }
        catch (HeuristicMixedException e)
        {
            // As expected
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
            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(ut.getStatus());
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT013() throws Exception {
        try
        {
            ut.begin();
            ut.commit();
            ut.commit();
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

        try
        {
            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(ut.getStatus());
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT014() throws Exception {
        try
        {
            ut.begin();
            ut.commit();
            ut.rollback();
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

        try
        {
            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(ut.getStatus());
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT015() throws Exception {
        try
        {
            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(ut.getStatus());
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT016() throws Exception {
        try
        {
            ut.begin();

            assertThat(Status.STATUS_ACTIVE).isEqualTo(ut.getStatus());

            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT017() throws Exception {
        try
        {
            ut.begin();
            ut.setRollbackOnly();

            assertThat(Status.STATUS_MARKED_ROLLBACK).isEqualTo(ut.getStatus());

            ut.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT018() throws Exception {
        try
        {
            ut.begin();
            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }

        try
        {
            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(ut.getStatus());
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT019() throws Exception {
        try
        {
            ut.begin();
            ut.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }

        try
        {
            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(ut.getStatus());
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT020() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            ut.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }

        try
        {
            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(ut.getStatus());
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT021() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl());
            ut.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }

        try
        {
            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(ut.getStatus());
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT022() throws Exception {
        try
        {
            ut.begin();
            ut.setRollbackOnly();
            ut.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }

        try
        {
            assertThat(Status.STATUS_NO_TRANSACTION).isEqualTo(ut.getStatus());
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testUT023() throws Exception {
        assertThatExceptionOfType(IllegalStateException.class)
        		.isThrownBy(() -> ut.rollback());
    }

    @Test
    public void testUT024() throws Exception {
        try
        {
            ut.begin();
            ut.rollback();
            ut.rollback();
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
    public void testUT025() throws Exception {
        try
        {
            ut.begin();
            ut.rollback();
            ut.commit();
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
    public void testUT026() throws Exception {
        assertThatExceptionOfType(IllegalStateException.class)
        		.isThrownBy(() -> ut.setRollbackOnly());
    }

    @Test
    public void testUT027() throws Exception {
        try
        {
            ut.begin();
            ut.setRollbackOnly();
            ut.setRollbackOnly();
            ut.commit();
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
    public void testUT033() throws Exception {
        try
        {
            // Defect 447459 allows timeout of -1
            ut.setTransactionTimeout(-1);
            fail("");
        }
        catch (SystemException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }
}
