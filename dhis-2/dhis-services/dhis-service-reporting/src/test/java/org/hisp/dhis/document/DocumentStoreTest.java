package org.hisp.dhis.document;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DocumentStoreTest
    extends DhisSpringTest
{
    private DocumentStore documentStore;
    
    private Document documentA;
    private Document documentB;
    private Document documentC;
    
    @Override
    public void setUpTest()
    {
        documentStore = (DocumentStore) getBean( DocumentStore.ID );
        
        documentA = new Document( "DocumentA", "UrlA", true );
        documentB = new Document( "DocumentB", "UrlB", true );
        documentC = new Document( "DocumentC", "UrlC", false );
    }

    @Test
    public void testSaveGet()
    {
        int id = documentStore.saveDocument( documentA );
        
        assertEquals( documentA, documentStore.getDocument( id ) );
    }

    @Test
    public void testDelete()
    {
        int idA = documentStore.saveDocument( documentA );
        int idB = documentStore.saveDocument( documentB );
        
        assertNotNull( documentStore.getDocument( idA ) );
        assertNotNull( documentStore.getDocument( idB ) );
        
        documentStore.deleteDocument( documentA );
        
        assertNull( documentStore.getDocument( idA ) );
        assertNotNull( documentStore.getDocument( idB ) );
        
        documentStore.deleteDocument( documentB );

        assertNull( documentStore.getDocument( idA ) );
        assertNull( documentStore.getDocument( idB ) );
    }

    @Test
    public void testGetAll()
    {
        documentStore.saveDocument( documentA );
        documentStore.saveDocument( documentB );
        documentStore.saveDocument( documentC );
        
        Collection<Document> actual = documentStore.getAllDocuments();
        
        assertEquals( 3, actual.size() );
        assertTrue( actual.contains( documentA ) );
        assertTrue( actual.contains( documentB ) );
        assertTrue( actual.contains( documentC ) );        
    }

    @Test
    public void testGetByName()
    {
        documentStore.saveDocument( documentA );
        documentStore.saveDocument( documentB );
        documentStore.saveDocument( documentC );
        
        assertEquals( documentA, documentStore.getDocumentByName( "DocumentA" ) );
    }
}
