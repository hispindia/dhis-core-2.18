package org.hisp.dhis.common;

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

import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroupAccess;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class SharingUtils
{
    public static Map<Class<? extends IdentifiableObject>, String> PUBLIC_AUTHORITIES = new HashMap<Class<? extends IdentifiableObject>, String>();

    public static Map<Class<? extends IdentifiableObject>, String> PRIVATE_AUTHORITIES = new HashMap<Class<? extends IdentifiableObject>, String>();

    public static final Map<String, Class<? extends IdentifiableObject>> SUPPORTED_TYPES = new HashMap<String, Class<? extends IdentifiableObject>>();

    public static final String SHARING_OVERRIDE_AUTHORITY = "ALL";

    static
    {
        SUPPORTED_TYPES.put( "document", Document.class );
        PUBLIC_AUTHORITIES.put( Document.class, "F_DOCUMENT_PUBLIC_ADD" );
        PRIVATE_AUTHORITIES.put( Document.class, "F_DOCUMENT_PRIVATE_ADD" );

        SUPPORTED_TYPES.put( "report", Report.class );
        PUBLIC_AUTHORITIES.put( Report.class, "F_REPORT_PUBLIC_ADD" );
        PRIVATE_AUTHORITIES.put( Report.class, "F_REPORT_PRIVATE_ADD" );

        SUPPORTED_TYPES.put( "reportTable", ReportTable.class );
        PUBLIC_AUTHORITIES.put( ReportTable.class, "F_REPORTTABLE_PUBLIC_ADD" );
        PRIVATE_AUTHORITIES.put( ReportTable.class, "F_REPORTTABLE_PRIVATE_ADD" );

        SUPPORTED_TYPES.put( "dataSet", DataSet.class );
        PUBLIC_AUTHORITIES.put( DataSet.class, "F_DATASET_PUBLIC_ADD" );
        PRIVATE_AUTHORITIES.put( DataSet.class, "F_DATASET_PRIVATE_ADD" );

        SUPPORTED_TYPES.put( "dataDictionary", DataDictionary.class );
        PUBLIC_AUTHORITIES.put( DataDictionary.class, "F_DATADICTIONARY_PUBLIC_ADD" );
        PRIVATE_AUTHORITIES.put( DataDictionary.class, "F_DATADICTIONARY_PRIVATE_ADD" );

        SUPPORTED_TYPES.put( "dataElementGroup", DataElementGroup.class );
        PUBLIC_AUTHORITIES.put( DataElementGroup.class, "F_DATAELEMENTGROUP_PUBLIC_ADD" );
        PRIVATE_AUTHORITIES.put( DataElementGroup.class, "F_DATAELEMENTGROUP_PRIVATE_ADD" );

        SUPPORTED_TYPES.put( "dataElementGroupSet", DataElementGroupSet.class );
        PUBLIC_AUTHORITIES.put( DataElementGroupSet.class, "F_DATAELEMENTGROUPSET_PUBLIC_ADD" );
        PRIVATE_AUTHORITIES.put( DataElementGroupSet.class, "F_DATAELEMENTGROUPSET_PRIVATE_ADD" );
    }

    public static boolean isSupported( String type )
    {
        return SUPPORTED_TYPES.containsKey( type );
    }

    public static boolean isSupported( IdentifiableObject object )
    {
        return isSupported( object.getClass() );
    }

    public static boolean isSupported( Class clazz )
    {
        return SUPPORTED_TYPES.containsValue( clazz );
    }

    public static Class<? extends IdentifiableObject> classForType( String type )
    {
        return SUPPORTED_TYPES.get( type );
    }

    /**
     * Checks if a user can create a public instance of a certain object.
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Does user have the authority to create public instances of that object
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static boolean canCreatePublic( User user, IdentifiableObject object )
    {
        Set<String> authorities = user != null ? user.getUserCredentials().getAllAuthorities() : new HashSet<String>();
        return authorities.contains( SHARING_OVERRIDE_AUTHORITY ) || authorities.contains( PUBLIC_AUTHORITIES.get( object.getClass() ) );
    }

    /**
     * Checks if a user can create a private instance of a certain object.
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Does user have the authority to create private instances of that object
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static boolean canCreatePrivate( User user, IdentifiableObject object )
    {
        Set<String> authorities = user != null ? user.getUserCredentials().getAllAuthorities() : new HashSet<String>();
        return authorities.contains( SHARING_OVERRIDE_AUTHORITY ) || authorities.contains( PRIVATE_AUTHORITIES.get( object.getClass() ) );
    }

    /**
     * Can user write to this object (create)
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Is the user for the object null?
     * 3. Is the user of the object equal to current user?
     * 4. Is the object public write?
     * 5. Does any of the userGroupAccesses contain public write and the current user is in that group
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static boolean canWrite( User user, IdentifiableObject object )
    {
        if ( sharingOverrideAuthority( user ) || object.getUser() == null || user.equals( object.getUser() ) ||
            AccessStringHelper.canWrite( object.getPublicAccess() ) )
        {
            return true;
        }

        for ( UserGroupAccess userGroupAccess : object.getUserGroupAccesses() )
        {
            if ( AccessStringHelper.canWrite( userGroupAccess.getAccess() )
                && userGroupAccess.getUserGroup().getMembers().contains( user ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Can user read this object
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Is the user for the object null?
     * 3. Is the user of the object equal to current user?
     * 4. Is the object public read?
     * 5. Does any of the userGroupAccesses contain public read and the current user is in that group
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static boolean canRead( User user, IdentifiableObject object )
    {
        if ( sharingOverrideAuthority( user ) || object.getUser() == null || user.equals( object.getUser() ) ||
            AccessStringHelper.canRead( object.getPublicAccess() ) )
        {
            return true;
        }

        for ( UserGroupAccess userGroupAccess : object.getUserGroupAccesses() )
        {
            if ( AccessStringHelper.canRead( userGroupAccess.getAccess() )
                && userGroupAccess.getUserGroup().getMembers().contains( user ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Can user update this object
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Can user write to this object?
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static boolean canUpdate( User user, IdentifiableObject object )
    {
        return canWrite( user, object );
    }

    /**
     * Can user delete this object
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Can user write to this object?
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static boolean canDelete( User user, IdentifiableObject object )
    {
        return canWrite( user, object );
    }

    /**
     * Can user read this object
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Can user write to this object?
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static boolean canManage( User user, IdentifiableObject object )
    {
        return sharingOverrideAuthority( user ) || canWrite( user, object );
    }

    private static boolean sharingOverrideAuthority( User user )
    {
        return user != null && user.getUserCredentials().getAllAuthorities().contains( SHARING_OVERRIDE_AUTHORITY );
    }
}
