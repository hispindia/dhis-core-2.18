package org.hisp.dhis.dataset;

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

import static org.hisp.dhis.i18n.I18nUtils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.AuditLogUtil;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultDataSetService.java 6255 2008-11-10 16:01:24Z larshelg $
 */
@Transactional
public class DefaultDataSetService
    implements DataSetService
{
    private static final Log log = LogFactory.getLog( DefaultDataSetService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetStore dataSetStore;

    public void setDataSetStore( DataSetStore dataSetStore )
    {
        this.dataSetStore = dataSetStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------

    public int addDataSet( DataSet dataSet )
    {
        log.info( AuditLogUtil.logMessage( currentUserService.getCurrentUsername(),
            AuditLogUtil.ACTION_ADD, DataSet.class.getSimpleName(), dataSet.getName() ) );

        return dataSetStore.save( dataSet );
    }

    public void updateDataSet( DataSet dataSet )
    {
        dataSetStore.update( dataSet );

        log.info( AuditLogUtil.logMessage( currentUserService.getCurrentUsername(),
            AuditLogUtil.ACTION_EDIT, DataSet.class.getSimpleName(), dataSet.getName() ) );
    }

    public void deleteDataSet( DataSet dataSet )
    {
        dataSetStore.delete( dataSet );

        log.info( AuditLogUtil.logMessage( currentUserService.getCurrentUsername(),
            AuditLogUtil.ACTION_DELETE, DataSet.class.getSimpleName(), dataSet.getName() ) );
    }

    public DataSet getDataSet( int id )
    {
        return i18n( i18nService, dataSetStore.get( id ) );
    }
    
    @Override
    public DataSet getDataSet( String uid )
    {
        return i18n( i18nService, dataSetStore.getByUid( uid ) );
    }

    public DataSet getDataSetByName( String name )
    {
        return i18n( i18nService, dataSetStore.getByName( name ) );
    }

    public DataSet getDataSetByShortName( String shortName )
    {
        return i18n( i18nService, dataSetStore.getByShortName( shortName ) );
    }

    public DataSet getDataSetByCode( String code )
    {
        return i18n( i18nService, dataSetStore.getByCode( code ) );
    }

    public Collection<DataSet> getDataSetsBySources( Collection<OrganisationUnit> sources )
    {
        return i18n( i18nService, dataSetStore.getDataSetsBySources( sources ) );
    }

    public int getSourcesAssociatedWithDataSet( DataSet dataSet, Collection<OrganisationUnit> sources )
    {
        int count = 0;

        for ( OrganisationUnit source : sources )
        {
            if ( dataSet.getSources().contains( source ) )
            {
                count++;
            }
        }

        return count;
    }

    public Collection<DataSet> getAllDataSets()
    {
        return i18n( i18nService, dataSetStore.getAll() );
    }

    public Collection<DataSet> getDataSetsByPeriodType( PeriodType periodType )
    {
        return i18n( i18nService, dataSetStore.getDataSetsByPeriodType( periodType ) );
    }

    public Collection<DataSet> getDataSets( final Collection<Integer> identifiers )
    {
        Collection<DataSet> dataSets = getAllDataSets();

        return identifiers == null ? dataSets : FilterUtils.filter( dataSets, new Filter<DataSet>()
        {
            public boolean retain( DataSet object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public List<DataSet> getAvailableDataSets()
    {
        List<DataSet> availableDataSetList = new ArrayList<DataSet>();
        List<DataSet> dataSetList = new ArrayList<DataSet>( getAllDataSets() );

        for ( DataSet dataSet : dataSetList )
        {
            DataEntryForm dataEntryForm = dataSet.getDataEntryForm();

            if ( dataEntryForm == null )
            {
                availableDataSetList.add( dataSet );
            }
        }

        return availableDataSetList;
    }

    public List<DataSet> getAssignedDataSets()
    {
        List<DataSet> assignedDataSetList = new ArrayList<DataSet>();
        List<DataSet> dataSetList = new ArrayList<DataSet>( getAllDataSets() );

        for ( DataSet dataSet : dataSetList )
        {
            DataEntryForm dataEntryForm = dataSet.getDataEntryForm();

            if ( dataEntryForm != null )
            {
                assignedDataSetList.add( dataSet );
            }
        }

        return assignedDataSetList;
    }

    public PeriodType getPeriodType( DataElement dataElement, Collection<Integer> dataSetIdentifiers )
    {
        Collection<DataSet> dataSets = getDataSets( dataSetIdentifiers );

        for ( DataSet dataSet : dataSets )
        {
            if ( dataSet.getDataElements().contains( dataElement ) )
            {
                return dataSet.getPeriodType();
            }
        }

        return null;
    }

    public List<DataSet> getAssignedDataSetsByPeriodType( PeriodType periodType )
    {
        List<DataSet> dataSetListByPeriodType = new ArrayList<DataSet>( getDataSetsByPeriodType( periodType ) );

        Iterator<DataSet> dataSetIterator = dataSetListByPeriodType.iterator();
        while( dataSetIterator.hasNext() )
        {
            DataSet dataSet = dataSetIterator.next();
            if ( dataSet.getSources() == null || dataSet.getSources().size() == 0 )
            {
                dataSetIterator.remove();
            }
        }

        return dataSetListByPeriodType;
    }
    
    public Collection<DataElement> getDistinctDataElements( Collection<Integer> dataSetIdentifiers )
    {
        Collection<DataSet> dataSets = getDataSets( dataSetIdentifiers );

        Set<DataElement> dataElements = new HashSet<DataElement>();

        for ( DataSet dataSet : dataSets )
        {
            dataElements.addAll( dataSet.getDataElements() );
        }

        return dataElements;
    }

    public Collection<DataElement> getDataElements( DataSet dataSet )
    {
        return i18n( i18nService, dataSet.getDataElements() );
    }    
    
    public Collection<DataSet> getDataSetsForMobile( OrganisationUnit source ) 
    {
        return i18n( i18nService, dataSetStore.getDataSetsForMobile(source) );		
    }
    
    public Collection<DataSet> getDataSetsForMobile()
    {
        return i18n( i18nService, dataSetStore.getDataSetsForMobile() );
    }

    @Override
    public int getDataSetCount()
    {
        return dataSetStore.getCount();
    }

    @Override
    public int getDataSetCountByName( String name )
    {
        return getCountByName( i18nService, dataSetStore, name );
    }

    @Override
    public Collection<DataSet> getDataSetsBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, dataSetStore, first, max );
    }

    @Override
    public Collection<DataSet> getDataSetsBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, dataSetStore, name, first, max );
    }
}
