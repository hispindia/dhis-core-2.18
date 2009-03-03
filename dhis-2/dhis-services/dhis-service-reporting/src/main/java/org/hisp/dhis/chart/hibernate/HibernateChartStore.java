package org.hisp.dhis.chart.hibernate;

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

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartStore;
import org.hisp.dhis.hibernate.HibernateSessionManager;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class HibernateChartStore
    implements ChartStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }

    // -------------------------------------------------------------------------
    // ChartStore implementation
    // -------------------------------------------------------------------------

    public int saveChart( Chart chart )
    {
        return (Integer) sessionManager.getCurrentSession().save( chart );
    }
    
    public void saveOrUpdate( Chart chart )
    {
        sessionManager.getCurrentSession().saveOrUpdate( chart );
    }
    
    public Chart getChart( int id )
    {
        return (Chart) sessionManager.getCurrentSession().get( Chart.class, id );
    }
    
    public void deleteChart( Chart chart )
    {
        sessionManager.getCurrentSession().delete( chart );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Chart> getAllCharts()
    {
        return sessionManager.getCurrentSession().createCriteria( Chart.class ).list();
    }
    
    public Chart getChartByTitle( String title )
    {
        Criteria criteria = sessionManager.getCurrentSession().createCriteria( Chart.class );
        criteria.add( Restrictions.eq( "title", title ) );
        return (Chart) criteria.uniqueResult();
    }
}
