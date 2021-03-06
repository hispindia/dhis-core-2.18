package org.hisp.dhis.webapi.controller;

/*
 * Copyright (c) 2004-2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dxf2.datavalueset.DataValueSetService;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ExportOptions;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import static org.hisp.dhis.webapi.utils.ContextUtils.*;

@Controller
@RequestMapping( value = DataValueSetController.RESOURCE_PATH )
public class DataValueSetController
{
    public static final String RESOURCE_PATH = "/dataValueSets";

    private static final Log log = LogFactory.getLog( DataValueSetController.class );

    @Autowired
    private DataValueSetService dataValueSetService;

    // -------------------------------------------------------------------------
    // Get
    // -------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET, produces = CONTENT_TYPE_XML )
    public void getDataValueSetXml(
        @RequestParam Set<String> dataSet,
        @RequestParam( required = false ) String period,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date startDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date endDate,
        @RequestParam Set<String> orgUnit,
        @RequestParam( required = false ) boolean children,
        ExportOptions exportOptions,
        HttpServletResponse response ) throws IOException
    {
        response.setContentType( CONTENT_TYPE_XML );

        boolean isSingleDataValueSet = dataSet.size() == 1 && period != null && orgUnit.size() == 1;

        if ( isSingleDataValueSet )
        {
            String ds = dataSet.iterator().next();
            String ou = orgUnit.iterator().next();

            log.debug( "Get XML data value set for data set: " + ds + ", period: " + period + ", org unit: " + ou );

            dataValueSetService.writeDataValueSetXml( ds, period, ou, response.getOutputStream(), exportOptions );
        }
        else
        {
            log.debug( "Get XML bulk data value set for start date: " + startDate + ", end date: " + endDate );

            dataValueSetService.writeDataValueSetXml( dataSet, startDate, endDate, orgUnit, children, response.getOutputStream(), exportOptions );
        }
    }

    @RequestMapping( method = RequestMethod.GET, produces = CONTENT_TYPE_JSON )
    public void getDataValueSetJson(
        @RequestParam Set<String> dataSet,
        @RequestParam( required = false ) String period,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date startDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date endDate,
        @RequestParam Set<String> orgUnit,
        @RequestParam( required = false ) boolean children,
        ExportOptions exportOptions,
        HttpServletResponse response ) throws IOException
    {
        response.setContentType( CONTENT_TYPE_JSON );

        boolean isSingleDataValueSet = dataSet.size() == 1 && period != null && orgUnit.size() == 1;

        if ( isSingleDataValueSet )
        {
            String ds = dataSet.iterator().next();
            String ou = orgUnit.iterator().next();

            log.debug( "Get JSON data value set for data set: " + ds + ", period: " + period + ", org unit: " + ou );

            dataValueSetService.writeDataValueSetJson( ds, period, ou, response.getOutputStream(), exportOptions );
        }
        else
        {
            log.debug( "Get JSON bulk data value set for start date: " + startDate + ", end date: " + endDate );

            dataValueSetService.writeDataValueSetJson( dataSet, startDate, endDate, orgUnit, children, response.getOutputStream(), exportOptions );
        }
    }

    @RequestMapping( method = RequestMethod.GET, produces = CONTENT_TYPE_CSV )
    public void getDataValueSetCsv(
        @RequestParam Set<String> dataSet,
        @RequestParam( required = false ) String period,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date startDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date endDate,
        @RequestParam Set<String> orgUnit,
        @RequestParam( required = false ) boolean children,
        ExportOptions exportOptions,
        HttpServletResponse response ) throws IOException
    {
        response.setContentType( CONTENT_TYPE_CSV );

        boolean isSingleDataValueSet = dataSet.size() == 1 && period != null && orgUnit.size() == 1;

        if ( isSingleDataValueSet )
        {
            String ds = dataSet.iterator().next();
            String ou = orgUnit.iterator().next();

            log.debug( "Get CSV data value set for data set: " + ds + ", period: " + period + ", org unit: " + ou );

            dataValueSetService.writeDataValueSetCsv( ds, period, ou, response.getWriter(), exportOptions );
        }
        else
        {
            log.debug( "Get CSV bulk data value set for start date: " + startDate + ", end date: " + endDate );

            dataValueSetService.writeDataValueSetCsv( dataSet, startDate, endDate, orgUnit, children, response.getWriter(), exportOptions );
        }
    }

    // -------------------------------------------------------------------------
    // Post
    // -------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, consumes = "application/xml" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_ADD')" )
    public void postDxf2DataValueSet( ImportOptions importOptions,
        HttpServletResponse response, InputStream in, Model model ) throws IOException
    {
        ImportSummary summary = dataValueSetService.saveDataValueSet( in, importOptions );

        log.debug( "Data values set saved" );

        response.setContentType( CONTENT_TYPE_XML );
        JacksonUtils.toXml( response.getOutputStream(), summary );
    }

    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_ADD')" )
    public void postJsonDataValueSet( ImportOptions importOptions,
        HttpServletResponse response, InputStream in, Model model ) throws IOException
    {
        ImportSummary summary = dataValueSetService.saveDataValueSetJson( in, importOptions );

        log.debug( "Data values set saved" );

        response.setContentType( CONTENT_TYPE_JSON );
        JacksonUtils.toJson( response.getOutputStream(), summary );
    }

    @RequestMapping( method = RequestMethod.POST, consumes = "application/csv" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_ADD')" )
    public void postCsvDataValueSet( ImportOptions importOptions,
        HttpServletResponse response, InputStream in, Model model ) throws IOException
    {
        ImportSummary summary = dataValueSetService.saveDataValueSetCsv( in, importOptions );

        log.debug( "Data values set saved" );

        response.setContentType( CONTENT_TYPE_XML );
        JacksonUtils.toXml( response.getOutputStream(), summary );
    }

    // -------------------------------------------------------------------------
    // Supportive
    // -------------------------------------------------------------------------

    @ExceptionHandler( IllegalArgumentException.class )
    public void handleError( IllegalArgumentException ex, HttpServletResponse response )
    {
        ContextUtils.conflictResponse( response, ex.getMessage() );
    }
}
