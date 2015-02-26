package org.hisp.dhis.dataapproval;

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

import static org.hisp.dhis.setting.SystemSettingManager.KEY_ACCEPTANCE_REQUIRED_FOR_APPROVAL;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_HIDE_UNAPPROVED_DATA_IN_ANALYTICS;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This package private class holds the context for deciding on data approval permissions.
 * The context contains both system settings and some qualities of the user.
 * <p>
 * This class is especially efficient if the settings are set once and
 * then used several times to generate ApprovalPermissions for different
 * DataApproval objects.
 *
 * @author Jim Grace
 */
class DataApprovalPermissionsEvaluator
{
    private final static Log log = LogFactory.getLog( DataApprovalPermissionsEvaluator.class );

    private DataApprovalLevelService dataApprovalLevelService;

    private User user;

    private boolean acceptanceRequiredForApproval;
    private boolean hideUnapprovedData;

    private boolean authorizedToApprove;
    private boolean authorizedToApproveAtLowerLevels;
    private boolean authorizedToAcceptAtLowerLevels;
    private boolean authorizedToViewUnapprovedData;

    private int maxApprovalLevel;

    private DataApprovalPermissionsEvaluator()
    {
    }

    private static Cache<String, DataApprovalLevel> USER_APPROVAL_LEVEL_CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess( 10, TimeUnit.MINUTES ).initialCapacity( 10000 )
        .maximumSize( 50000 ).build();

    /**
     * Allocates and populates the context for determining user permissions
     * on one or more DataApproval objects.
     *
     * @param currentUserService Current user service
     * @param systemSettingManager System setting manager
     * @param dataApprovalLevelService Data approval level service
     * @return context for determining user permissions
     */
    static DataApprovalPermissionsEvaluator makePermissionsEvaluator( CurrentUserService currentUserService,
            SystemSettingManager systemSettingManager, DataApprovalLevelService dataApprovalLevelService )
    {
        DataApprovalPermissionsEvaluator ev = new DataApprovalPermissionsEvaluator();

        ev.dataApprovalLevelService = dataApprovalLevelService;

        ev.user = currentUserService.getCurrentUser();

        ev.acceptanceRequiredForApproval = (Boolean) systemSettingManager.getSystemSetting( KEY_ACCEPTANCE_REQUIRED_FOR_APPROVAL, false );
        ev.hideUnapprovedData = (Boolean) systemSettingManager.getSystemSetting( KEY_HIDE_UNAPPROVED_DATA_IN_ANALYTICS, false );

        ev.authorizedToApprove = ev.user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE );
        ev.authorizedToApproveAtLowerLevels = ev.user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        ev.authorizedToAcceptAtLowerLevels = ev.user.getUserCredentials().isAuthorized( DataApproval.AUTH_ACCEPT_LOWER_LEVELS );
        ev.authorizedToViewUnapprovedData = ev.user.getUserCredentials().isAuthorized( DataApproval.AUTH_VIEW_UNAPPROVED_DATA );

        ev.maxApprovalLevel = dataApprovalLevelService.getAllDataApprovalLevels().size();

        log.debug( "makePermissionsEvaluator acceptanceRequiredForApproval " + ev.acceptanceRequiredForApproval
            + " hideUnapprovedData " + ev.hideUnapprovedData + " authorizedToApprove " + ev.authorizedToApprove
            + " authorizedToAcceptAtLowerLevels " + ev.authorizedToAcceptAtLowerLevels
            + " authorizedToViewUnapprovedData " + ev.authorizedToViewUnapprovedData + " maxApprovalLevel " + ev.maxApprovalLevel );

        return ev;
    }

    /**
     * Allocates and fills a data approval permissions object according to
     * the context of system settings and user information.
     * <p>
     * If there is a data permissions state, also takes this into account.
     *
     * @param status the data approval status (if any)
     * @return the data approval permissions for the object
     */
    DataApprovalPermissions getPermissions( DataApprovalStatus status )
    {
        final DataApproval da = status.getDataApproval();

        DataApprovalState s = status.getState();

        DataApprovalPermissions permissions = new DataApprovalPermissions();

        if ( da == null || da.getOrganisationUnit() == null )
        {
            log.debug( "getPermissions da " + da + ( da != null ? ( " " + da.getOrganisationUnit() ) : "" ) );

            return permissions; // No permissions are set.
        }

        DataApprovalLevel userApprovalLevel;

        try
        {
            userApprovalLevel = ( USER_APPROVAL_LEVEL_CACHE.get( user.getId() + "-" + da.getOrganisationUnit().getId(), new Callable<DataApprovalLevel>()
            {
                public DataApprovalLevel call() throws ExecutionException
                {
                    return dataApprovalLevelService.getUserApprovalLevel( user, da.getOrganisationUnit() );
                }
            } ) );
        }
        catch ( CacheLoader.InvalidCacheLoadException ex )
        {
            userApprovalLevel = null; // Google cache doesn't like to cache a null value.
        }
        catch ( ExecutionException ex )
        {
            throw new RuntimeException( ex );
        }

        if ( userApprovalLevel == null )
        {
            log.debug( "getPermissions userApprovalLevel null for user " + ( user == null ? "(null)" : user.getUsername() ) + " orgUnit " +  da.getOrganisationUnit().getName() );

            return permissions; // Can't find user approval level, so no permissions are set.
        }

        int userLevel = userApprovalLevel.getLevel();

        int dataLevel = ( da.getDataApprovalLevel() != null ? da.getDataApprovalLevel().getLevel() : maxApprovalLevel );

        int nextApproveDataLevel = s.isApproved() ? dataLevel - 1 : dataLevel;

        boolean mayApproveOrUnapproveAtLevel = ( authorizedToApprove && userLevel == dataLevel && !da.isAccepted() ) ||
            ( authorizedToApproveAtLowerLevels && userLevel < dataLevel );

        boolean mayApproveAtNextLevel = ( s == DataApprovalState.ACCEPTED_HERE || ( s == DataApprovalState.APPROVED_HERE && ! acceptanceRequiredForApproval ) ) && 
            ( ( authorizedToApprove && userLevel == nextApproveDataLevel ) || ( authorizedToApproveAtLowerLevels && userLevel < nextApproveDataLevel ) );

        boolean mayAcceptOrUnacceptAtLevel = authorizedToAcceptAtLowerLevels &&
                ( userLevel == dataLevel - 1 || ( userLevel < dataLevel && authorizedToApproveAtLowerLevels ) );

        boolean mayApprove = ( s.isApprovable() && mayApproveOrUnapproveAtLevel ) || mayApproveAtNextLevel;

        boolean mayUnapprove = s.isUnapprovable() && ( ( mayApproveOrUnapproveAtLevel && !da.isAccepted() ) || mayAcceptOrUnacceptAtLevel );

        boolean mayAccept = s.isAcceptable() && mayAcceptOrUnacceptAtLevel;

        boolean mayUnaccept = s.isUnacceptable() && mayAcceptOrUnacceptAtLevel;

        boolean mayReadData = authorizedToViewUnapprovedData || !hideUnapprovedData || mayApprove
                || userLevel >= dataLevel;

        log.debug( "getPermissions orgUnit " + ( da.getOrganisationUnit() == null ? "(null)" : da.getOrganisationUnit().getName() )
            + " combo " + da.getAttributeOptionCombo().getName() + " state " + s.name()
            + " isApproved " + s.isApproved() + " isApprovable " + s.isApprovable() + " isUnapprovable " + s.isUnapprovable()
            + " isAccepted " + s.isAccepted() + " isAcceptable " + s.isAcceptable() + " isUnacceptable " + s.isUnacceptable()
            + " userLevel " + userLevel + " dataLevel " + dataLevel
            + " mayApproveOrUnapproveAtLevel " + mayApproveOrUnapproveAtLevel + " mayAcceptOrUnacceptAtLevel " + mayAcceptOrUnacceptAtLevel
            + " mayApprove " + mayApprove + " mayUnapprove " + mayUnapprove
            + " mayAccept " + mayAccept + " mayUnaccept " + mayUnaccept
            + " mayReadData " + mayReadData );

        permissions.setMayApprove( mayApprove );
        permissions.setMayUnapprove( mayUnapprove );
        permissions.setMayAccept( mayAccept );
        permissions.setMayUnaccept( mayUnaccept );
        permissions.setMayReadData( mayReadData );

        return permissions;
    }
}
