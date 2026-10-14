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
import javax.transaction.xa.XAResource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.test.cases.transaction.util.SimpleTestResource;

/**
 * @version $Rev$ $Date$
 *
 * simple user transaction test, test basic commit, rollback, suspend/resume
 *
 *
 */

public class SimpleUserTransactionTest extends TransactionTestBundleControl {
    
    @BeforeEach
    public void setUp() throws Exception {
		super.setUp();
        super.setUpTransactionManager();
        super.setUpUserTransaction();
    }
    
    // 4.5.3 4.5.5 simple test user transaction commit without resource 
    @Test
    public void testUserTransaction1() throws Exception {
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();

        // test commit
        ut.begin();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        assertThat(tm.getTransaction()).isNotNull();
        
        ut.commit();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull(); 
    }
    
    // 4.5.3 4.5.5 simple test user transaction commit with resource 
    @Test
    public void testUserTransaction2() throws Exception {
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        
        // test commit
        ut.begin();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        assertThat(tm.getTransaction()).isNotNull();
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertThat(res1.isCommitted()).isFalse();
        assertThat(res2.isCommitted()).isFalse();
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        
        ut.commit();
        assertThat(res2.isCommitted()).isTrue();
        assertThat(res1.isCommitted()).isTrue();
        assertThat(res2.isRolledback()).isFalse();
        assertThat(res1.isRolledback()).isFalse();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull(); 
    }
    
    // 4.5.6, 4.5.7 simple test user transaction rollbackonly without resource 
    @Test
    public void testUserTransaction3() throws Exception {
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        
        // test commit
        ut.begin();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        assertThat(tm.getTransaction()).isNotNull();

        // set roll back only.   commit should fail
        ut.setRollbackOnly();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_MARKED_ROLLBACK);
        
        assertThatExceptionOfType(RollbackException.class).as("expected a RollbackException : the transaction has been rolled back.")
        		.isThrownBy(() -> ut.commit());

        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull(); 
    }   
    
    // 4.5.6, 4.5.7 simple test user transaction rollbackonly with resource 
    @Test
    public void testUserTransaction4() throws Exception {
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        
        // test commit
        ut.begin();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        assertThat(tm.getTransaction()).isNotNull();
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertThat(res1.isCommitted()).isFalse();
        assertThat(res2.isCommitted()).isFalse();

        // set roll back only.   commit should fail
        ut.setRollbackOnly();
        assertThat(tm.getStatus()).isEqualTo(Status.STATUS_MARKED_ROLLBACK);
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        
        assertThatExceptionOfType(RollbackException.class).as("expected a RollbackException : the transaction has been rolled back.")
        		.isThrownBy(() -> ut.commit());

        assertThat(res2.isRolledback()).isTrue();
        assertThat(res1.isRolledback()).isTrue();
        assertThat(res2.isCommitted()).isFalse();
        assertThat(res1.isCommitted()).isFalse();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull(); 
    }   

    // 4.5.6, 4.5.7 simple test user transaction rollback 2 without resource 
    @Test
    public void testUserTransaction5() throws Exception {
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        
        // test commit
        ut.begin();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        assertThat(tm.getTransaction()).isNotNull();
        
        ut.rollback();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull(); 
    }  
    
    // 4.5.6, 4.5.7 simple test user transaction rollback 2 with resource 
    @Test
    public void testUserTransaction6() throws Exception {
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        
        // test commit
        ut.begin();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        assertThat(tm.getTransaction()).isNotNull();
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertThat(res1.isCommitted()).isFalse();
        assertThat(res2.isCommitted()).isFalse();
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        
        ut.rollback();
        assertThat(res2.isRolledback()).isTrue();
        assertThat(res1.isRolledback()).isTrue();
        assertThat(res2.isCommitted()).isFalse();
        assertThat(res1.isCommitted()).isFalse();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull(); 
    }   
    
    // 4.5.1 simple test user transaction with error 
    @Test
    public void testUserTransactionNested() throws Exception {
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
        
        ut.begin();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        assertThat(tm.getTransaction()).isNotNull();
        
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertThat(res1.isCommitted()).isFalse();
        assertThat(res2.isCommitted()).isFalse();
        
        assertThatExceptionOfType(NotSupportedException.class).as("expected a NotSupportedException : Nested Transactions are not supported.")
        		.isThrownBy(() -> ut.begin());
        
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        ut.commit();
        assertThat(res2.isCommitted()).isTrue();
        assertThat(res1.isCommitted()).isTrue();
        assertThat(res2.isRolledback()).isFalse();
        assertThat(res1.isRolledback()).isFalse();
        assertThat(ut.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
        assertThat(tm.getTransaction()).isNull();
    }
}
