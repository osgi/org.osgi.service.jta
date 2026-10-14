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

import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.test.cases.transaction.util.SimpleTestResource;

/**
 * @version $Rev$ $Date$
 *
 * simple transaction manager test, test basic commit, rollback, suspend/resume
 *
 *
 */

public class SimpleTransactionManagerTest extends TransactionTestBundleControl {
    
    @BeforeEach
    public void setUp() throws Exception {
		super.setUp();
        super.setUpTransactionManager();
    }

    // 4.5.3 4.5.5 simple test transaction manager commit without resource
    @Test
    public void testTransactionManager1() throws Exception {
        startWithClean();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        
        tm.begin();       
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        assertThat(tm.getTransaction()).isNotNull();
        
        tm.commit();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        
        tm.begin();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        assertThat(tm.getTransaction()).isNotNull();
        
        tm.commit();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
    }
    
    // 4.5.3 4.5.5 simple test transaction manager commit with resource
    @Test
    public void testTransactionManager2() throws Exception {
        startWithClean();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        
        tm.begin();        
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertThat(res1.isCommitted()).isFalse();
        assertThat(res2.isCommitted()).isFalse();
        assertThat(res1.isRolledback()).isFalse();
        assertThat(res2.isRolledback()).isFalse();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        assertThat(tm.getTransaction()).isNotNull();
        
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        tm.commit();
        assertThat(res1.isCommitted()).isTrue();
        assertThat(res2.isCommitted()).isTrue();
        assertThat(res1.isRolledback()).isFalse();
        assertThat(res2.isRolledback()).isFalse();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        
        tm.begin();        
        res1 = new SimpleTestResource("res1");
        res2 = new SimpleTestResource("res2");
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertThat(res1.isCommitted()).isFalse();
        assertThat(res2.isCommitted()).isFalse();
        assertThat(res1.isRolledback()).isFalse();
        assertThat(res2.isRolledback()).isFalse();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        assertThat(tm.getTransaction()).isNotNull();
        
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        tm.commit();
        assertThat(res1.isCommitted()).isTrue();
        assertThat(res2.isCommitted()).isTrue();
        assertThat(res1.isRolledback()).isFalse();
        assertThat(res2.isRolledback()).isFalse();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
    }
    // 4.5.1, 4.5.6 test transaction manager status, rollback without resource
    @Test
    public void testTransactionManager3() throws Exception {
        startWithClean();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        
        tm.begin();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        Transaction tx = tm.getTransaction();
        assertThat(tx).isNotNull();
        assertThat(tx.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
         
        tm.rollback();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();     
         
        tm.begin();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        tx = tm.getTransaction();
        assertThat(tx).isNotNull();
        assertThat(tx.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
         
        tm.rollback();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull(); 
    }
    
    // 4.5.1, 4.5.6 test transaction manager status, rollback with resource
    @Test
    public void testTransactionManager4() throws Exception {
        startWithClean();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        
        tm.begin();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        Transaction tx = tm.getTransaction();
        assertThat(tx).isNotNull();
        assertThat(tx.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertThat(res1.isCommitted()).isFalse();
        assertThat(res2.isCommitted()).isFalse();
        assertThat(res1.isRolledback()).isFalse();
        assertThat(res2.isRolledback()).isFalse();
         
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        tm.rollback();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();     
        assertThat(res1.isCommitted()).isFalse();
        assertThat(res2.isCommitted()).isFalse();
        assertThat(res1.isRolledback()).isTrue();
        assertThat(res2.isRolledback()).isTrue();
    }

    // 4.5.3 test transaction manager status, commit fail, without resource
    @Test
    public void testTransactionManager5() throws Exception {
        startWithClean();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        
        tm.begin();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        Transaction tx = tm.getTransaction();
        assertThat(tx).isNotNull();
        assertThat(tx.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
         
        // set roll back only.   commit should fail
        tm.setRollbackOnly();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_MARKED_ROLLBACK);
        assertThatExceptionOfType(RollbackException.class).as("expected a RollbackException : the transaction has been rolled back.")
        		.isThrownBy(() -> tm.commit());
        
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull(); 
    }
    
    // 4.5.3 test transaction manager status, commit fail, with resource
    @Test
    public void testTransactionManager6() throws Exception {
        startWithClean();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        
        tm.begin();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        Transaction tx = tm.getTransaction();
        assertThat(tx).isNotNull();
        assertThat(tx.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertThat(res1.isCommitted()).isFalse();
        assertThat(res2.isCommitted()).isFalse();
        assertThat(res1.isRolledback()).isFalse();
        assertThat(res2.isRolledback()).isFalse();
         
        // set roll back only.   commit should fail
        tm.setRollbackOnly();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_MARKED_ROLLBACK);
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        assertThatExceptionOfType(RollbackException.class).as("expected a RollbackException : the transaction has been rolled back.")
        		.isThrownBy(() -> tm.commit());
        
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull(); 
        assertThat(res1.isCommitted()).isFalse();
        assertThat(res2.isCommitted()).isFalse();
        assertThat(res1.isRolledback()).isTrue();
        assertThat(res2.isRolledback()).isTrue();
    }
    // test 4.3.1 Nested transactions are not supported. 
    @Test
    public void testTransactionManagerNested() throws Exception {
        startWithClean();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        
        tm.begin();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        Transaction tx = tm.getTransaction();
        assertThat(tx).isNotNull();
        assertThat(tx.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        
        tm.rollback();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();     
        
        tm.begin();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        
        assertThatExceptionOfType(NotSupportedException.class).as("expected a NotSupportedException : Nested Transactions are not supported.")
        		.isThrownBy(() -> tm.begin());
        
        tm.commit();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
    }
    
    // 4.5.8 test transaction suspend/resume
    // test transaction manager status without resource
    @Test
    public void testTransactionManager10() throws Exception {
        startWithClean();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        
        tm.begin();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        Transaction tx = tm.getTransaction();
        assertThat(tx).isNotNull();
        assertThat(tx.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        
        tx = tm.suspend();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull(); 
        
        tm.resume(tx);

        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        assertThat(tm.getTransaction()).isNotNull();
        assertThat(tm.getTransaction()).isEqualTo(tx);  
     
        tm.rollback();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull(); 
    }
    
    private void startWithClean()
    {
        try
        {
            // let's always start with no transaction when starting each test.
            if (tm.getStatus() != Status.STATUS_NO_TRANSACTION)
            {
                tm.rollback();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("");     
        }
    }  

}
