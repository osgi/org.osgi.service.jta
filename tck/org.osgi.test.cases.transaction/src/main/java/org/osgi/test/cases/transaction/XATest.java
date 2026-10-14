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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;

import javax.transaction.HeuristicMixedException;
import javax.transaction.RollbackException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.test.cases.transaction.util.XAResourceImpl;

/**
 * @version $Rev$ $Date$
 *
 * XA test, test various XAExceptions arisen from multiple XAResources
 * 
 *
 */
public class XATest extends TransactionTestBundleControl {

    @BeforeEach
    public void setUp() throws Exception {
		super.setUp();
        super.setUpTransactionManager();
    }

    @Test
    public void testXA001() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail("");
        }
    }

    @Test
    public void testXA002() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());

            assertThatExceptionOfType(RollbackException.class)
            		.isThrownBy(() -> tm.commit());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testXA003() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());

            assertThatExceptionOfType(RollbackException.class)
            		.isThrownBy(() -> tm.commit());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testXA004() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));

            assertThatExceptionOfType(RollbackException.class)
            		.isThrownBy(() -> tm.commit());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testXA005() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());

            tx.setRollbackOnly();

            assertThatExceptionOfType(RollbackException.class)
            		.isThrownBy(() -> tm.commit());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testXA006() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());

            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testXA007() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());

            tx.setRollbackOnly();
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testXA008() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testXA009() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURRB));
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());

            assertThatExceptionOfType(RollbackException.class)
            		.isThrownBy(() -> tm.commit());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testXA010() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURCOM));
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());

            assertThatExceptionOfType(HeuristicMixedException.class)
            		.isThrownBy(() -> tm.commit());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testXA011() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURMIX));
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());

            assertThatExceptionOfType(HeuristicMixedException.class)
            		.isThrownBy(() -> tm.commit());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testXA013() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURRB));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURCOM));

            assertThatExceptionOfType(HeuristicMixedException.class)
            		.isThrownBy(() -> tm.commit());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");
        }
    }
}
