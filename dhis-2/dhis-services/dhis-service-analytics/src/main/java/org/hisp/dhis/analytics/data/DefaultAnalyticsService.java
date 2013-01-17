package org.hisp.dhis.analytics.data;

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

import static org.hisp.dhis.analytics.DataQueryParams.DATAELEMENT_DIM_ID;
import static org.hisp.dhis.analytics.DataQueryParams.DIMENSION_SEP;
import static org.hisp.dhis.analytics.DataQueryParams.INDICATOR_DIM_ID;
import static org.hisp.dhis.analytics.DataQueryParams.ORGUNIT_DIM_ID;
import static org.hisp.dhis.analytics.DataQueryParams.PERIOD_DIM_ID;
import static org.hisp.dhis.analytics.DataQueryParams.getDimensionFromParam;
import static org.hisp.dhis.analytics.DataQueryParams.getDimensionOptionsFromParam;
import static org.hisp.dhis.common.IdentifiableObjectUtils.asList;
import static org.hisp.dhis.common.IdentifiableObjectUtils.asTypedList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.hisp.dhis.analytics.AnalyticsManager;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.AnalyticsTableManager;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.DimensionOption;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.system.util.SystemUtils;
import org.hisp.dhis.system.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class DefaultAnalyticsService
    implements AnalyticsService
{
    private static final String VALUE_HEADER_NAME = "Value";
    
    //TODO indicator aggregation
    //TODO completeness
    
    @Autowired
    private AnalyticsManager analyticsManager;
    
    @Autowired
    private QueryPlanner queryPlanner;
    
    @Autowired
    private IndicatorService indicatorService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    @Autowired
    private ExpressionService expressionService;
    
    @Autowired
    private ConstantService constantService;

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    public Grid getAggregatedDataValues( DataQueryParams params ) throws Exception
    {
        Grid grid = new ListGrid();

        // ---------------------------------------------------------------------
        // Headers and meta-data
        // ---------------------------------------------------------------------

        grid.setMetaData( params.getUidNameMap() );
        
        for ( String col : params.getSelectDimensionNames() )
        {
            grid.addHeader( new GridHeader( col, col, String.class.getName(), false, true ) );
        }
        
        grid.addHeader( new GridHeader( DataQueryParams.VALUE_ID, VALUE_HEADER_NAME, Double.class.getName(), false, false ) );

        // ---------------------------------------------------------------------
        // Indicators
        // ---------------------------------------------------------------------

        if ( params.getIndicators() != null && !params.getIndicators().isEmpty() )
        {         
            Map<String, Double> constantMap = constantService.getConstantMap();

            int indicatorIndex = params.getDataElementOrIndicatorDimensionIndex();
            
            DataQueryParams dataSourceParams = setDataElementsFromIndicators( params );

            List<Indicator> indicators = asTypedList( params.getIndicators() );
            
            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( dataSourceParams );

            Map<String, Map<DataElementOperand, Double>> permutationOperandValueMap = dataSourceParams.getPermutationOperandValueMap( aggregatedDataMap );

            List<List<DimensionOption>> dimensionOptionPermutations = dataSourceParams.getDimensionOptionPermutations();

            for ( Indicator indicator : indicators )
            {
                for ( List<DimensionOption> options : dimensionOptionPermutations )
                {
                    String permKey = DimensionOption.asOptionKey( options );

                    Map<DataElementOperand, Double> valueMap = permutationOperandValueMap.get( permKey );

                    if ( valueMap != null )
                    {
                        Period period = (Period) DimensionOption.getPeriodOption( options );
                        
                        Assert.notNull( period );
                        
                        Double value = expressionService.getIndicatorValue( indicator, period, valueMap, constantMap, null );
                        
                        if ( value != null )
                        {
                            List<DimensionOption> row = new ArrayList<DimensionOption>( options );
                            
                            row.add( indicatorIndex, new DimensionOption( INDICATOR_DIM_ID, indicator ) );
                            
                            grid.addRow();
                            grid.addValues( DimensionOption.getOptionIdentifiers( row ) );
                            grid.addValue( value );
                        }
                    }                    
                }
            }
        }

        // ---------------------------------------------------------------------
        // Data elements
        // ---------------------------------------------------------------------

        if ( params.getDataElements() != null && !params.getDataElements().isEmpty() )
        {
            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( params );
            
            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                grid.addRow();
                grid.addValues( entry.getKey().split( DIMENSION_SEP ) );
                grid.addValue( entry.getValue() );
            }            
        }
        
        return grid;
    }
    
    public Map<String, Double> getAggregatedDataValueMap( DataQueryParams params ) throws Exception
    {
        Timer t = new Timer().start();

        int optimalQueries = MathUtils.getWithin( SystemUtils.getCpuCores(), 1, 6 );
        
        List<DataQueryParams> queries = queryPlanner.planQuery( params, optimalQueries, AnalyticsTableManager.ANALYTICS_TABLE_NAME );
        
        t.getTime( "Planned query for optimal: " + optimalQueries + ", got: " + queries.size() );
        
        List<Future<Map<String, Double>>> futures = new ArrayList<Future<Map<String, Double>>>();
        
        Map<String, Double> map = new HashMap<String, Double>();
        
        for ( DataQueryParams query : queries )
        {
            futures.add( analyticsManager.getAggregatedDataValues( query ) );
        }
        
        for ( Future<Map<String, Double>> future : futures )
        {
            Map<String, Double> taskValues = future.get();
            
            if ( taskValues != null )
            {
                map.putAll( taskValues );
            }
        }
        
        t.getTime( "Got aggregated values" );
        
        return map;
    }
    
    public DataQueryParams getFromUrl( Set<String> dimensionParams, Set<String> filterParams, boolean categories, I18nFormat format )
    {
        DataQueryParams params = new DataQueryParams();

        params.setCategories( categories );
        
        if ( dimensionParams != null && !dimensionParams.isEmpty() )
        {
            for ( String param : dimensionParams )
            {
                String dimension = getDimensionFromParam( param );
                List<String> options = getDimensionOptionsFromParam( param );
                
                if ( dimension != null && options != null )
                {
                    List<IdentifiableObject> dimensionOptions = getDimensionOptions( dimension, options, format );
                    
                    params.getDimensions().put( dimension, dimensionOptions );
                }
            }
        }

        if ( filterParams != null && !filterParams.isEmpty() )
        {
            for ( String param : filterParams )
            {
                String dimension = DataQueryParams.getDimensionFromParam( param );
                List<String> options = DataQueryParams.getDimensionOptionsFromParam( param );
                
                if ( dimension != null && options != null )
                {
                    List<IdentifiableObject> dimensionOptions = getDimensionOptions( dimension, options, format );
                    
                    params.getFilters().put( dimension, dimensionOptions );
                }
            }
        }

        return params;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private List<IdentifiableObject> getDimensionOptions( String dimension, List<String> options, I18nFormat format )
    {
        if ( INDICATOR_DIM_ID.equals( dimension ) )
        {
            return asList( indicatorService.getIndicatorsByUid( options ) );
        }
        else if ( DATAELEMENT_DIM_ID.equals( dimension ) )
        {
            return asList( dataElementService.getDataElementsByUid( options ) );
        }
        else if ( ORGUNIT_DIM_ID.equals( dimension ) )
        {
            return asList( organisationUnitService.getOrganisationUnitsByUid( options ) );
        }
        else if ( PERIOD_DIM_ID.equals( dimension ) )
        {
            List<IdentifiableObject> list = new ArrayList<IdentifiableObject>();
            
            for ( String isoPeriod : options )
            {
                Period period = PeriodType.getPeriodFromIsoString( isoPeriod );
                period.setName( format != null ? format.formatPeriod( period ) : null );
                list.add( period );
            }
            
            return list;
        }
        
        OrganisationUnitGroupSet orgUnitGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( dimension );
            
        if ( orgUnitGroupSet != null )
        {
            return asList( organisationUnitGroupService.getOrganisationUnitGroupsByUid( options ) );
        }
        
        DataElementGroupSet dataElementGroupSet = dataElementService.getDataElementGroupSet( dimension );
        
        if ( dataElementGroupSet != null )
        {
            return asList( dataElementService.getDataElementGroupsByUid( options ) );
        }
        
        return null;
    }
    
    private DataQueryParams setDataElementsFromIndicators( DataQueryParams params )
    {
        DataQueryParams immutableParams = new DataQueryParams( params );
        
        List<Indicator> indicators = asTypedList( immutableParams.getIndicators() );            
        List<IdentifiableObject> dataElements = asList( expressionService.getDataElementsInIndicators( indicators ) );
        
        immutableParams.setDataElements( dataElements );
        immutableParams.removeDimension( INDICATOR_DIM_ID );
        immutableParams.setCategories( true );
        
        return immutableParams;
    }
}
