package org.hisp.dhis.dataintegrity;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataintegrity.DataIntegrityService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DefaultDataIntegrityService
    implements DataIntegrityService
{
    private static final String FORMULA_SEPARATOR = "#";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // DataIntegrityService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // DataElement
    // -------------------------------------------------------------------------

    public Collection<DataElement> getDataElementsWithoutDataSet()
    {
        Collection<DataSet> dataSets = dataSetService.getAllDataSets();
        
        Collection<DataElement> dataElements = dataElementService.getAllDataElements();
        
        Iterator<DataElement> iterator = dataElements.iterator();
        
        while ( iterator.hasNext() )
        {
            final DataElement element = iterator.next();
            
            for ( DataSet dataSet : dataSets )
            {
                if ( dataSet.getDataElements().contains( element ) )
                {
                    iterator.remove();
                    
                    break;
                }
            }
        }
        
        return dataElements;
    }

    public Collection<DataElement> getDataElementsWithoutGroups()
    {
        Collection<DataElementGroup> groups = dataElementService.getAllDataElementGroups();
        
        Collection<DataElement> dataElements = dataElementService.getAllDataElements();
        
        Iterator<DataElement> iterator = dataElements.iterator();
        
        while ( iterator.hasNext() )
        {
            final DataElement element = iterator.next();
            
            for ( DataElementGroup group : groups )
            {
                if ( group.getMembers().contains( element ) )
                {
                    iterator.remove();
                    
                    break;
                }
            }
        }
        
        return dataElements;
    }

    public Map<DataElement, Collection<DataSet>> getDataElementsAssignedToDataSetsWithDifferentPeriodTypes()
    {
        Collection<DataElement> dataElements = dataElementService.getAllDataElements();
        
        Collection<DataSet> dataSets = dataSetService.getAllDataSets();
        
        Map<DataElement, Collection<DataSet>> targets = new HashMap<DataElement, Collection<DataSet>>();
                
        for ( DataElement element : dataElements )
        {
            final Set<PeriodType> targetPeriodTypes = new HashSet<PeriodType>();
            final Collection<DataSet> targetDataSets = new HashSet<DataSet>();
            
            for ( DataSet dataSet : dataSets )
            {
                if ( dataSet.getDataElements().contains( element ) )
                {
                    targetPeriodTypes.add( dataSet.getPeriodType() );
                    targetDataSets.add( dataSet );
                }
            }
            
            if ( targetPeriodTypes.size() > 1 )
            {
                targets.put( element, targetDataSets );
            }          
        }
        
        return targets;
    }

    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------

    public Collection<DataSet> getDataSetsNotAssignedToOrganisationUnits()
    {
        Collection<DataSet> dataSets = dataSetService.getAllDataSets();
        
        Iterator<DataSet> iterator = dataSets.iterator();
        
        while ( iterator.hasNext() )
        {
            final DataSet dataSet = iterator.next();
            
            if ( dataSet.getSources().size() > 0 )
            {
                iterator.remove();
            }
        }
        
        return dataSets;
    }
    
    // -------------------------------------------------------------------------
    // Indicator
    // -------------------------------------------------------------------------

    public Collection<Indicator> getIndicatorsWithBlankFormulas()
    {
        Collection<Indicator> indicators = indicatorService.getAllIndicators();
        
        Iterator<Indicator> iterator = indicators.iterator();
        
        while ( iterator.hasNext() )
        {
            final Indicator indicator = iterator.next();
            
            if ( indicator.getNumerator() != null && 
                 indicator.getNumerator().trim().length() > 0 &&
                 indicator.getDenominator() != null &&
                 indicator.getDenominator().trim().length() > 0 )
            {
                iterator.remove();
            }
        }
        
        return indicators;
    }
    
    public Collection<Indicator> getIndicatorsWithIdenticalFormulas()
    {
        List<String> formulas = new ArrayList<String>();
        
        Set<Indicator> targets = new HashSet<Indicator>();
        
        Collection<Indicator> indicators = indicatorService.getAllIndicators();
        
        for ( Indicator indicator : indicators )
        {
            final String formula = indicator.getNumerator() + FORMULA_SEPARATOR + indicator.getDenominator();
            
            if ( formulas.contains( formula ) )
            {
                targets.add( indicator );
            }
            else
            {
                formulas.add( formula );
            }
        }
        
        return targets;
    }

    public Collection<Indicator> getIndicatorsWithoutGroups()
    {
        Collection<IndicatorGroup> groups = indicatorService.getAllIndicatorGroups();
        
        Collection<Indicator> indicators = indicatorService.getAllIndicators();
        
        Iterator<Indicator> iterator = indicators.iterator();
        
        while ( iterator.hasNext() )
        {
            final Indicator indicator = iterator.next();
            
            for ( IndicatorGroup group : groups )
            {
                if ( group.getMembers().contains( indicator ) )
                {
                    iterator.remove();
                    
                    break;
                }
            }
        }
        
        return indicators;
    }
    
    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    public Collection<OrganisationUnit> getOrganisationUnitsWithCyclicReferences()
    {
        Collection<OrganisationUnit> organisationUnits = organisationUnitService.getAllOrganisationUnits();
        
        Set<OrganisationUnit> cyclic = new HashSet<OrganisationUnit>();
        
        Set<OrganisationUnit> visited = new HashSet<OrganisationUnit>();
        
        OrganisationUnit parent = null;
        
        for ( OrganisationUnit unit : organisationUnits )
        {
            parent = unit;
                        
            while ( ( parent = parent.getParent() ) != null )
            {
                if ( parent.equals( unit ) ) // Cyclic reference
                {
                    cyclic.add( unit );

                    break;                    
                }
                else if ( visited.contains( parent ) ) // Ends in cyclic reference but not part of it
                {
                    break;
                }
                else // Remember visited
                {
                    visited.add( parent );
                }
            }
            
            visited.clear();
        }
        
        return cyclic;
    }

    public Collection<OrganisationUnit> getOrphanedOrganisationUnits()
    {
        Collection<OrganisationUnit> organisationUnits = organisationUnitService.getAllOrganisationUnits();
        
        Set<OrganisationUnit> orphans = new HashSet<OrganisationUnit>();
        
        for ( OrganisationUnit unit : organisationUnits )
        {
            if ( unit.getParent() == null && ( unit.getChildren() == null || unit.getChildren().size() == 0 ) )
            {
                orphans.add( unit );
            }
        }
        
        return orphans;
    }

    public Collection<OrganisationUnit> getOrganisationUnitsWithoutGroups()
    {
        Collection<OrganisationUnitGroup> groups = organisationUnitGroupService.getAllOrganisationUnitGroups();
        
        Collection<OrganisationUnit> organisationUnits = organisationUnitService.getAllOrganisationUnits();
        
        Iterator<OrganisationUnit> iterator = organisationUnits.iterator();
        
        while ( iterator.hasNext() )
        {
            final OrganisationUnit unit = iterator.next();
            
            for ( OrganisationUnitGroup group : groups )
            {
                if ( group.getMembers().contains( unit ) )
                {
                    iterator.remove();
                    
                    break;
                }
            }
        }
        
        return organisationUnits;
    }

    public Collection<OrganisationUnit> getOrganisationUnitsViolatingCompulsoryGroupSets()
    {
        Collection<OrganisationUnitGroupSet> groupSets = organisationUnitGroupService.getCompulsoryOrganisationUnitGroupSets();
        
        Collection<OrganisationUnit> organisationUnits = organisationUnitService.getAllOrganisationUnits();
        
        Set<OrganisationUnit> targets = new HashSet<OrganisationUnit>();
        
        for ( OrganisationUnitGroupSet groupSet : groupSets )
        {
            organisationUnit: for ( OrganisationUnit unit : organisationUnits )
            {
                for ( OrganisationUnitGroup group : groupSet.getOrganisationUnitGroups() )
                {                    
                    if ( group.getMembers().contains( unit ) )
                    {
                        continue organisationUnit;
                    }
                    
                    targets.add( unit ); // Unit is not a member of any groups in the compulsory group set                    
                }
            }
        }
        
        return targets;
    }

    public Collection<OrganisationUnit> getOrganisationUnitsViolatingExclusiveGroupSets()
    {
        Collection<OrganisationUnitGroupSet> groupSets = organisationUnitGroupService.getExclusiveOrganisationUnitGroupSets();
        
        Collection<OrganisationUnit> organisationUnits = organisationUnitService.getAllOrganisationUnits();
        
        Set<OrganisationUnit> targets = new HashSet<OrganisationUnit>();

        for ( OrganisationUnitGroupSet groupSet : groupSets )
        {
            organisationUnit: for ( OrganisationUnit unit : organisationUnits )
            {
                int memberships = 0;
                
                for ( OrganisationUnitGroup group : groupSet.getOrganisationUnitGroups() )
                {
                    if ( group.getMembers().contains( unit ) )
                    {
                        if ( ++memberships > 1 )
                        {
                            targets.add( unit ); // Unit is member of more than one group in the exclusive group set
                            
                            continue organisationUnit;
                        }
                    }
                }
            }
        }
        
        return targets;
    }

    public Collection<OrganisationUnitGroup> getOrganisationUnitGroupsWithoutGroupSets()
    {
        Collection<OrganisationUnitGroupSet> groupSets = organisationUnitGroupService.getExclusiveOrganisationUnitGroupSets();
        
        Collection<OrganisationUnitGroup> groups = organisationUnitGroupService.getAllOrganisationUnitGroups();
        
        Iterator<OrganisationUnitGroup> iterator = groups.iterator();
        
        while ( iterator.hasNext() )
        {
            final OrganisationUnitGroup group = iterator.next();
            
            for ( OrganisationUnitGroupSet groupSet : groupSets )
            {
                if ( groupSet.getOrganisationUnitGroups().contains( group ) )
                {
                    iterator.remove();
                    
                    break;
                }
            }
        }
        
        return groups;
    }
}
