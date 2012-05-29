package org.hisp.dhis.interpretation.impl;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.Date;
import java.util.List;

import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.interpretation.InterpretationComment;
import org.hisp.dhis.interpretation.InterpretationService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
@Transactional
public class DefaultInterpretationService
    implements InterpretationService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<Interpretation> interpretationStore;

    public void setInterpretationStore( GenericIdentifiableObjectStore<Interpretation> interpretationStore )
    {
        this.interpretationStore = interpretationStore;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // InterpretationService implementation
    // -------------------------------------------------------------------------

    public int saveInterpretation( Interpretation interpretation )
    {
        User user = currentUserService.getCurrentUser();
        
        if ( user != null )
        {
            interpretation.setUser( user );
        }
        
        return interpretationStore.save( interpretation );
    }
    
    public Interpretation getInterpretation( int id )
    {
        return interpretationStore.get( id );
    }
    
    public Interpretation getInterpretation( String uid )
    {
        return interpretationStore.getByUid( uid );
    }
    
    public void deleteInterpretation( Interpretation interpretation )
    {
        interpretationStore.delete( interpretation );
    }
    
    public List<Interpretation> getInterpretations( int first, int max )
    {
        return interpretationStore.getBetweenOrderderByLastUpdated( first, max );
    }
    
    public void addInterpretationComment( String uid, String text )
    {
        Interpretation interpretation = getInterpretation( uid );

        User user = currentUserService.getCurrentUser();
        
        InterpretationComment comment = new InterpretationComment( text );
        comment.setLastUpdated( new Date() );
        comment.setUid( CodeGenerator.generateCode() );
        
        if ( user != null )
        {
            comment.setUser( user );
        }
        
        interpretation.addComment( comment );
        
        interpretationStore.update( interpretation );
    }
}
