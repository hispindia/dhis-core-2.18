package org.hisp.dhis.dataadmin.action.statistics;

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

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.common.Objects;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.statistics.StatisticsProvider;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.PlotOrientation;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetStatisticsChartAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private StatisticsProvider statisticsProvider;

    public void setStatisticsProvider( StatisticsProvider statisticsProvider )
    {
        this.statisticsProvider = statisticsProvider;
    }
    
    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private JFreeChart chart;

    public JFreeChart getChart()
    {
        return chart;
    }

    // -------------------------------------------------------------------------
    // Action implemenation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Map<Objects, Integer> counts = statisticsProvider.getObjectCounts();
        
        Map<String, Double> categoryValues = new HashMap<String, Double>();
        
        categoryValues.put( i18n.getString( "data_elements" ), Double.valueOf( counts.get( Objects.DATAELEMENT ) ) );
        categoryValues.put( i18n.getString( "indicators" ), Double.valueOf( counts.get( Objects.INDICATOR ) ) );
        categoryValues.put( i18n.getString( "data_sets" ), Double.valueOf( counts.get( Objects.DATASET ) ) );
        categoryValues.put( i18n.getString( "organisation_units" ), Double.valueOf( counts.get( Objects.SOURCE ) ) );
        categoryValues.put( i18n.getString( "periods" ), Double.valueOf( counts.get( Objects.PERIOD ) ) );
        
        chart = chartService.getJFreeChart( i18n.getString( "number_of_objects" ), 
            PlotOrientation.HORIZONTAL, CategoryLabelPositions.STANDARD, categoryValues );
        
        return SUCCESS;
    }
}

