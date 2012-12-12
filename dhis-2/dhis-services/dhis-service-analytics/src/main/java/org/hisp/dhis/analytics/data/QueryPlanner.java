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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.table.PartitionUtils;
import org.hisp.dhis.system.util.ListMap;
import org.hisp.dhis.system.util.PaginatedList;

public class QueryPlanner
{
    /**
     * Creates a list of DataQueryParams.
     * 
     * @param params the data query params.
     * @param optimalQueries the number of optimal queries for the planner to return.
     * @return
     */
    public static List<DataQueryParams> planQuery( DataQueryParams params, int optimalQueries )
    {
        params = new DataQueryParams( params );
        
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();

        ListMap<String, String> tablePeriodMap = PartitionUtils.getTablePeriodMap( params.getPeriods() );
        
        boolean periodSatisfies = tablePeriodMap.size() >= optimalQueries;
        
        if ( periodSatisfies )
        {
            for ( String tableName : tablePeriodMap.keySet() )
            {
                DataQueryParams query = new DataQueryParams( params );
                query.setPeriods( tablePeriodMap.get( tableName ) );
                query.setTableName( tableName );
                queries.add( query );
            }
        }
        else
        {
            int pages = optimalQueries / tablePeriodMap.size(); // TODO individual no per table

            for ( String tableName : tablePeriodMap.keySet() )
            {
                params.setPeriods( tablePeriodMap.get( tableName ) );
                
                String dimension = getPartitionDimension( params, pages );
                
                List<String> partitionValues = params.getDimension( dimension );
                
                List<List<String>> partitionValuePages = new PaginatedList<String>( partitionValues ).setNumberOfPages( pages ).getPages();
            
                for ( List<String> valuePage : partitionValuePages )
                {
                    DataQueryParams query = new DataQueryParams( params );
                    query.setPeriods( tablePeriodMap.get( tableName ) );
                    query.setDimension( dimension, valuePage );
                    query.setTableName( tableName );
                    queries.add( query );
                }
            }
        }
        
        return queries;
    }
    
    /**
     * Gets the data dimension must suitable as partition key. Will first check
     * if any of the dimensions have enough values to satisfy a optimal number of
     * queries, and return that dimension if so. If not returns the dimension
     * with the highest number of values. The order of the fixed dimensions are
     * data element, organisation unit, period.
     * 
     * @param params the data query parameters.
     * @param optimalQueries the optimal number of queries to create.
     */
    public static String getPartitionDimension( DataQueryParams params, int optimalQueries )
    {
        SortedMap<String, List<String>> map = params.getDimensionValuesMap();
        
        for ( String dimension : map.keySet() )
        {
            if ( map.get( dimension ).size() >= optimalQueries )
            {
                return dimension;
            }
        }
        
        return params.getLargestDimension();
    }
}
