package org.hisp.dhis.user.action;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import com.google.common.collect.Lists;
import com.opensymphony.xwork2.Action;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.system.util.AttributeUtils;
import org.hisp.dhis.system.util.LocaleUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.user.UserSetting;
import org.hisp.dhis.user.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Torgeir Lorange Ostby
 */
public class UpdateUserAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    @Autowired
    private UserGroupService userGroupService;

    private PasswordManager passwordManager;

    public void setPasswordManager( PasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String rawPassword;

    public void setRawPassword( String rawPassword )
    {
        this.rawPassword = rawPassword;
    }

    private String surname;

    public void setSurname( String surname )
    {
        this.surname = surname;
    }

    private String firstName;

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    private String email;

    public void setEmail( String email )
    {
        this.email = email;
    }

    private String openId;

    public void setOpenId( String openId )
    {
        this.openId = openId;
    }

    private String phoneNumber;

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    private String localeUi;

    public void setLocaleUi( String localeUi )
    {
        this.localeUi = localeUi;
    }

    private String localeDb;

    public void setLocaleDb( String localeDb )
    {
        this.localeDb = localeDb;
    }

    private List<String> urSelected = Lists.newArrayList();

    public void setUrSelected( List<String> urSelected )
    {
        this.urSelected = urSelected;
    }

    private List<String> ugSelected = Lists.newArrayList();

    public void setUgSelected( List<String> ugSelected )
    {
        this.ugSelected = ugSelected;
    }

    private List<String> jsonAttributeValues;

    public void setJsonAttributeValues( List<String> jsonAttributeValues )
    {
        this.jsonAttributeValues = jsonAttributeValues;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( email != null && email.trim().length() == 0 )
        {
            email = null;
        }

        if ( rawPassword != null && rawPassword.trim().length() == 0 )
        {
            rawPassword = null;
        }

        // ---------------------------------------------------------------------
        // User credentials and user
        // ---------------------------------------------------------------------

        User user = userService.getUser( id );
        user.setSurname( surname );
        user.setFirstName( firstName );
        user.setEmail( email );
        user.setPhoneNumber( phoneNumber );

        UserCredentials userCredentials = userService.getUserCredentials( user );

        if ( !StringUtils.isEmpty( openId ) )
        {
            userCredentials.setOpenId( openId );
        }
        else
        {
            userCredentials.setOpenId( null );
        }

        if ( rawPassword != null )
        {
            userCredentials.setPassword( passwordManager.encodePassword( userCredentials.getUsername(), rawPassword ) );
        }

        if ( jsonAttributeValues != null )
        {
            AttributeUtils.updateAttributeValuesFromJson( user.getAttributeValues(), jsonAttributeValues,
                attributeService );
        }

        // ---------------------------------------------------------------------
        // Organisation units
        // ---------------------------------------------------------------------

        Set<OrganisationUnit> dataCaptureOrgUnits = new HashSet<OrganisationUnit>( selectionManager.getSelectedOrganisationUnits() );
        user.updateOrganisationUnits( dataCaptureOrgUnits );

        Set<OrganisationUnit> dataViewOrgUnits = new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );
        user.setDataViewOrganisationUnits( dataViewOrgUnits );

        // ---------------------------------------------------------------------
        // User roles
        // ---------------------------------------------------------------------

        Set<UserAuthorityGroup> userAuthorityGroups = new HashSet<UserAuthorityGroup>();

        for ( String id : urSelected )
        {
            userAuthorityGroups.add( userService.getUserAuthorityGroup( id ) );
        }

        userService.canIssueFilter( userAuthorityGroups );

        userCredentials.setUserAuthorityGroups( userAuthorityGroups );

        userService.updateUserCredentials( userCredentials );
        userService.updateUser( user );

        // ---------------------------------------------------------------------
        // Organisation unit trees
        // ---------------------------------------------------------------------

        if ( user.equals( currentUserService.getCurrentUser() ) )
        {
            selectionManager.setRootOrganisationUnits( dataCaptureOrgUnits );
            selectionManager.setSelectedOrganisationUnits( dataCaptureOrgUnits );

            selectionTreeManager.setRootOrganisationUnits( dataViewOrgUnits );
            selectionTreeManager.setSelectedOrganisationUnits( dataViewOrgUnits );
        }

        // ---------------------------------------------------------------------
        // User settings
        // ---------------------------------------------------------------------

        userService.addOrUpdateUserSetting( new UserSetting( user, UserSettingService.KEY_UI_LOCALE, LocaleUtils.getLocale( localeUi ) ) );
        userService.addOrUpdateUserSetting( new UserSetting( user, UserSettingService.KEY_DB_LOCALE, LocaleUtils.getLocale( localeDb ) ) );

        // ---------------------------------------------------------------------
        // User groups
        // ---------------------------------------------------------------------

        Set<UserGroup> userGroups = new HashSet<UserGroup>();

        for ( String id : ugSelected )
        {
            userGroups.add( userGroupService.getUserGroup( id ) );
        }

        for ( UserGroup userGroup : new HashSet<UserGroup>( user.getGroups() ) )
        {
            if ( !userGroups.contains( userGroup ) )
            {
                userGroup.removeUser( user );
                userGroupService.updateUserGroup( userGroup );
            }
        }

        for ( UserGroup userGroup : userGroups )
        {
            userGroup.addUser( user );
            userGroupService.updateUserGroup( userGroup );
        }

        return SUCCESS;
    }
}
