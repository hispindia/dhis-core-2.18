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

import static org.hisp.dhis.dashboard.Dashboard.MAX_ITEMS;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dashboard.Dashboard;
import org.hisp.dhis.dashboard.DashboardItem;
import org.hisp.dhis.dashboard.DashboardSearchResult;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.hibernate.exception.DeleteAccessDeniedException;
import org.hisp.dhis.hibernate.exception.UpdateAccessDeniedException;
import org.hisp.dhis.schema.descriptors.DashboardItemSchemaDescriptor;
import org.hisp.dhis.schema.descriptors.DashboardSchemaDescriptor;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = DashboardSchemaDescriptor.API_ENDPOINT )
public class DashboardController
    extends AbstractCrudController<Dashboard>
{
    @Autowired
    private DashboardService dashboardService;

    @RequestMapping( value = "/q/{query}", method = RequestMethod.GET )
    public String search( @PathVariable String query, @RequestParam( required = false ) Set<String> max,
        Model model, HttpServletResponse response ) throws Exception
    {
        DashboardSearchResult result = dashboardService.search( query, max );

        model.addAttribute( "model", result );

        return "dashboardSearchResult";
    }

    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonObject( HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        Dashboard dashboard = JacksonUtils.fromJson( request.getInputStream(), Dashboard.class );

        dashboardService.mergeDashboard( dashboard );
        dashboardService.saveDashboard( dashboard );

        ContextUtils.createdResponse( response, "Dashboard created", DashboardSchemaDescriptor.API_ENDPOINT + "/" + dashboard.getUid() );
    }

    @Override
    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json" )
    public void putJsonObject( @PathVariable( "uid" ) String uid, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        Dashboard dashboard = dashboardService.getDashboard( uid );

        if ( dashboard == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard does not exist: " + uid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), dashboard ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this dashboard." );
        }

        Dashboard newDashboard = JacksonUtils.fromJson( request.getInputStream(), Dashboard.class );

        dashboard.setName( newDashboard.getName() ); // TODO Name only for now

        dashboardService.updateDashboard( dashboard );
    }

    @Override
    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE )
    public void deleteObject( @PathVariable( "uid" ) String uid, HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        List<Dashboard> objects = getEntity( uid );

        if ( objects.isEmpty() )
        {
            ContextUtils.conflictResponse( response, getEntityName() + " does not exist: " + uid );
            return;
        }

        if ( !aclService.canDelete( currentUserService.getCurrentUser(), objects.get( 0 ) ) )
        {
            throw new DeleteAccessDeniedException( "You don't have the proper permissions to delete this object." );
        }

        dashboardService.deleteDashboard( objects.get( 0 ) );
    }

    @RequestMapping( value = "/{uid}/items", method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonItem( @PathVariable String uid, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        Dashboard dashboard = dashboardService.getDashboard( uid );

        if ( dashboard == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard does not exist: " + uid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), dashboard ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this dashboard." );
        }

        DashboardItem item = JacksonUtils.fromJson( request.getInputStream(), DashboardItem.class );

        dashboardService.mergeDashboardItem( item );

        dashboard.getItems().add( 0, item );

        dashboardService.updateDashboard( dashboard );

        ContextUtils.createdResponse( response, "Dashboard item created", item.getUid() );
    }

    @RequestMapping( value = "/{dashboardUid}/items/content", method = RequestMethod.POST )
    public void postJsonItemContent( HttpServletResponse response, HttpServletRequest request,
        @PathVariable String dashboardUid, @RequestParam String type, @RequestParam( "id" ) String contentUid ) throws Exception
    {
        Dashboard dashboard = dashboardService.getDashboard( dashboardUid );

        if ( dashboard == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard does not exist: " + dashboardUid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), dashboard ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this dashboard." );
        }

        DashboardItem item = dashboardService.addItemContent( dashboardUid, type, contentUid );

        if ( item == null )
        {
            ContextUtils.conflictResponse( response, "Max number of dashboard items reached: " + MAX_ITEMS );
        }
        else
        {
            ContextUtils.createdResponse( response, "Dashboard item added", DashboardItemSchemaDescriptor.API_ENDPOINT + "/" + item.getUid() );
        }
    }

    @RequestMapping( value = "/{dashboardUid}/items/{itemUid}/position/{position}", method = RequestMethod.POST )
    public void moveItem( HttpServletResponse response, HttpServletRequest request,
        @PathVariable String dashboardUid, @PathVariable String itemUid, @PathVariable int position ) throws Exception
    {
        Dashboard dashboard = dashboardService.getDashboard( dashboardUid );

        if ( dashboard == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard does not exist: " + dashboardUid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), dashboard ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this dashboard." );
        }

        if ( dashboard.moveItem( itemUid, position ) )
        {
            dashboardService.updateDashboard( dashboard );

            ContextUtils.okResponse( response, "Dashboard item moved" );
        }
    }

    @RequestMapping( value = "/{dashboardUid}/items/{itemUid}", method = RequestMethod.DELETE )
    public void deleteItem( HttpServletResponse response, HttpServletRequest request,
        @PathVariable String dashboardUid, @PathVariable String itemUid )
    {
        Dashboard dashboard = dashboardService.getDashboard( dashboardUid );

        if ( dashboard == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard does not exist: " + dashboardUid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), dashboard ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this dashboard." );
        }

        DashboardItem item = dashboardService.getDashboardItem( itemUid );

        if ( item == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard item does not exist: " + itemUid );
            return;
        }

        if ( dashboard.hasItems() && dashboard.getItems().remove( item ) )
        {
            dashboardService.deleteDashboardItem( item );
            dashboardService.updateDashboard( dashboard );

            ContextUtils.okResponse( response, "Dashboard item removed" );
        }
    }

    @RequestMapping( value = "/{dashboardUid}/items/{itemUid}/content/{contentUid}", method = RequestMethod.DELETE )
    public void deleteItemContent( HttpServletResponse response, HttpServletRequest request,
        @PathVariable String dashboardUid, @PathVariable String itemUid, @PathVariable String contentUid )
    {
        Dashboard dashboard = dashboardService.getDashboard( dashboardUid );

        if ( dashboard == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard does not exist: " + dashboardUid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), dashboard ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this dashboard." );
        }

        DashboardItem item = dashboard.getItemByUid( itemUid );

        if ( item == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard item does not exist: " + itemUid );
            return;
        }

        if ( item.removeItemContent( contentUid ) )
        {
            if ( item.getContentCount() == 0 && dashboard.getItems().remove( item ) )
            {
                dashboardService.deleteDashboardItem( item ); // Delete if empty                
            }

            dashboardService.updateDashboard( dashboard );

            ContextUtils.okResponse( response, "Dashboard item content removed" );
        }
    }

    // -------------------------------------------------------------------------
    // Hooks
    // -------------------------------------------------------------------------

    @Override
    protected void postProcessEntity( Dashboard entity, WebOptions options, Map<String, String> parameters ) throws Exception
    {
        for ( DashboardItem item : entity.getItems() )
        {
            if ( item != null )
            {
                item.setHref( null ); // Null item link, not relevant

                if ( item.getEmbeddedItem() != null )
                {
                    linkService.generateLinks( item.getEmbeddedItem(), true );
                }
                else if ( item.getLinkItems() != null )
                {
                    for ( IdentifiableObject link : item.getLinkItems() )
                    {
                        linkService.generateLinks( link, true );
                    }
                }
            }
        }
    }
}
