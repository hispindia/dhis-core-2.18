package org.hisp.dhis.options.charts;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import org.hisp.dhis.user.NoCurrentUserException;
import org.hisp.dhis.user.UserSettingService;

/**
 * @author mortenoh
 */
public class DefaultDashboardChartsToDisplayManager
    implements DashboardChartsToDisplayManager
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    // -------------------------------------------------------------------------
    // DashboardChartsToDisplayManager implementation
    // -------------------------------------------------------------------------

    @Override
    public void setCurrentDashboardChartsToDisplay( String chartsToDisplay )
    {
        try
        {
            userSettingService.saveUserSetting( UserSettingService.KEY_DASHBOARD_CHARTS_TO_DISPLAY, chartsToDisplay );
        }
        catch ( NoCurrentUserException e )
        {
        }
    }

    @Override
    public String getCurrentDashboardChartsToDisplay()
    {
        return (String) userSettingService.getUserSetting( UserSettingService.KEY_DASHBOARD_CHARTS_TO_DISPLAY,
            DASHBOARD_CHARTS_TO_DISPLAY_4 );
    }

    @Override
    public List<String> getDashboardChartsToDisplay()
    {
        List<String> list = new ArrayList<String>();

        list.add( DASHBOARD_CHARTS_TO_DISPLAY_4 );
        list.add( DASHBOARD_CHARTS_TO_DISPLAY_6 );
        list.add( DASHBOARD_CHARTS_TO_DISPLAY_8 );

        return list;
    }

}
