

Alter table patient rename to trackedentityinstance;
ALTER TABLE trackedentityinstance RENAME COLUMN patientid TO trackedentityinstanceid;
ALTER TABLE trackedentityinstance RENAME CONSTRAINT patient_pkey TO trackedentityinstance_pkey;
ALTER TABLE trackedentityinstance RENAME CONSTRAINT fk_patient_organisationunitid TO fk_trackedentityinstance_organisationunitid;
ALTER TABLE trackedentityinstance RENAME CONSTRAINT fk_user_patientid TO fk_user_trackedentityinstance;



Alter table patientattribute rename to trackedentityattribute;
ALTER TABLE trackedentityattribute RENAME COLUMN patientattributeid TO trackedentityattributeid;
ALTER TABLE trackedentityattribute RENAME COLUMN patientattributegroupid TO trackedentityattributegroupid;
ALTER TABLE trackedentityattribute RENAME CONSTRAINT patientattribute_pkey TO trackedentityattribute_pkey;
ALTER TABLE trackedentityattribute RENAME CONSTRAINT fk_patientidentifiertype_periodtypeid TO trackedentityattribute_periodtypeid;
ALTER TABLE trackedentityattribute RENAME CONSTRAINT patientattribute_code_key TO trackedentityattribute_code_key;
ALTER TABLE trackedentityattribute RENAME CONSTRAINT patientattribute_name_key TO trackedentityattribute_name_key;


Alter table patientattributegroup rename to trackedentityattributegroup;
ALTER TABLE trackedentityattributegroup RENAME COLUMN patientattributeoptionid TO trackedentityattributegroupid;
ALTER TABLE trackedentityattributegroup RENAME CONSTRAINT patientattributegroup_pkey TO trackedentityattributegroup_pkey;
ALTER TABLE trackedentityattributegroup RENAME CONSTRAINT patientattributegroup_code_key TO trackedentityattributegroup_code_key;
ALTER TABLE trackedentityattributegroup RENAME CONSTRAINT patientattributegroup_name_key TO trackedentityattributegroup_name_key;


Alter table patientattributeoption rename to trackedentityattributeoption;
ALTER TABLE trackedentityattributeoption RENAME COLUMN patientattributeoptionid TO trackedentityattributeoptionid;
ALTER TABLE trackedentityattributeoption RENAME COLUMN patientattributeid TO trackedentityattributeid;
ALTER TABLE trackedentityattributeoption RENAME CONSTRAINT patientattributeoption_pkey TO trackedentityattributeoption_pkey;
ALTER TABLE trackedentityattributeoption RENAME CONSTRAINT fk_patientattributeoption_patientattributeid TO fk_attributeoption_attributeid;


Alter table patientaudit rename to trackedentityaudit;
ALTER TABLE trackedentityaudit RENAME COLUMN patientauditid TO trackedentityauditid;
ALTER TABLE trackedentityaudit RENAME CONSTRAINT patientaudit_pkey TO trackedentityaudit_pkey;
ALTER TABLE trackedentityaudit RENAME CONSTRAINT fk_patientauditid_patientid TO fk_trackedentityauditid_trackedentityinstanceid;


Alter table patientregistrationform rename to trackedentityform;
ALTER TABLE trackedentityform RENAME COLUMN patientregistrationformid TO trackedentityformid;
ALTER TABLE trackedentityform RENAME CONSTRAINT patientregistrationform_pkey TO trackedentityform_pkey;
ALTER TABLE trackedentityform RENAME CONSTRAINT fk_patientregistrationform_programid TO fk_trackedentityform_programid;
ALTER TABLE trackedentityform RENAME CONSTRAINT fk_patientregistrationform_dataentryformid TO fk_trackedentityform_dataentryformid;


Alter table patientreminder rename to trackedentityinstancereminder;
ALTER TABLE trackedentityinstancereminder RENAME COLUMN patientreminderid TO trackedentityinstancereminderid;
ALTER TABLE trackedentityinstancereminder RENAME CONSTRAINT patientreminder_pkey TO trackedentityinstancereminder_pkey;
ALTER TABLE trackedentityinstancereminder RENAME CONSTRAINT fk_patientreminder_usergroup TO fk_trackedentityinstancereminder_programid;


Alter table patientattributevalue rename to trackedentityattributevalue;
ALTER TABLE trackedentityattributevalue RENAME COLUMN patientid TO trackedentityinstanceid;
ALTER TABLE trackedentityattributevalue RENAME COLUMN patientattributeid TO trackedentityattributeid;
ALTER TABLE trackedentityattributevalue RENAME COLUMN patientattributeoptionid TO trackedentityattributeoptionid;
ALTER TABLE trackedentityattributevalue RENAME CONSTRAINT patientattributevalue_pkey TO trackedentityattributevalue_pkey;
ALTER TABLE trackedentityattributevalue RENAME CONSTRAINT fk_patientattributevalue_patientattributeid TO fk_attributevalue_attributeid;
ALTER TABLE trackedentityattributevalue RENAME CONSTRAINT fk_patientattributevalue_patientattributeoption TO fk_attributeValue_attributeoptionid;
ALTER TABLE trackedentityattributevalue RENAME CONSTRAINT fk_patientattributevalue_patientid TO fk_attributevalue_trackedentityinstanceid;

Alter table patientcomment rename to trackedentitycomment;
ALTER TABLE trackedentitycomment RENAME COLUMN patientcommentid TO trackedentitycommentid;
ALTER TABLE trackedentitycomment RENAME CONSTRAINT patientcomment_pkey TO trackedentitycomment_pkey;


Alter table patientdatavalue rename to trackedentitydatavalue;
ALTER TABLE trackedentitydatavalue RENAME CONSTRAINT patientdatavalue_pkey TO trackedentitydatavalue_pkey;
ALTER TABLE trackedentitydatavalue RENAME CONSTRAINT fk_patientdatavalue_dataelementid TO fk_entityinstancedatavalue_dataelementid;
ALTER TABLE trackedentitydatavalue RENAME CONSTRAINT fk_patientdatavalue_programstageinstanceid TO fk_entityinstancedatavalue_programstageinstanceid;


Alter table patientaggregatereport rename to trackedentityaggregatereport;
ALTER TABLE trackedentityaggregatereport RENAME COLUMN patientaggregatereportid TO trackedentityaggregatereportid;
ALTER TABLE trackedentityaggregatereport RENAME CONSTRAINT patientaggregatereport_pkey TO trackedentityaggregatereport_pkey;
ALTER TABLE trackedentityaggregatereport RENAME CONSTRAINT fk_patientaggregatereport_programstage TO fk_aggregatereport_programstage;
ALTER TABLE trackedentityaggregatereport RENAME CONSTRAINT fk_patientaggregatereport_userid TO fk_aggregatereport_userid;
ALTER TABLE trackedentityaggregatereport RENAME CONSTRAINT fk_patientaggregatereport_program TO fk_aggregatereport_program;
ALTER TABLE trackedentityaggregatereport RENAME CONSTRAINT patientaggregatereport_code_key TO trackedentityaggregatereport_code_key;
ALTER TABLE trackedentityaggregatereport RENAME CONSTRAINT patientaggregatereport_name_key TO trackedentityaggregatereport_name_key;
ALTER TABLE trackedentityaggregatereport RENAME CONSTRAINT patientaggregatereport_uid_key TO trackedentityaggregatereport_uid_key;


ALTER TABLE patientaggregatereport_dimension RENAME TO trackedentityaggregatereport_dimension;
ALTER TABLE trackedentityaggregatereport_dimension RENAME COLUMN patientaggregatereportid TO trackedentityaggregatereportid;
ALTER TABLE trackedentityaggregatereport_dimension RENAME CONSTRAINT patientaggregatereport_dimension_pkey TO trackedentityaggregatereport_dimension_pkey;
ALTER TABLE trackedentityaggregatereport_dimension RENAME CONSTRAINT fk_patientaggregatereportid_dimensions TO fk_aggregatereportid_dimensions;


ALTER TABLE patientaggregatereport_filters RENAME TO trackedentityaggregatereport_filters;
ALTER TABLE trackedentityaggregatereport_filters RENAME COLUMN patientaggregatereportid TO trackedentityaggregatereportid;
ALTER TABLE trackedentityaggregatereport_filters RENAME CONSTRAINT patientaggregatereport_filters_pkey TO trackedentityaggregatereport_filters_pkey;
ALTER TABLE trackedentityaggregatereport_filters RENAME CONSTRAINT fk_patientaggregatereportid_filters TO fk_aggregatereportid_filters;


ALTER TABLE patientaggregatereportusergroupaccesses RENAME TO trackedentityaggregatereportusergroupaccesses;
ALTER TABLE trackedentityaggregatereportusergroupaccesses RENAME COLUMN patientaggregatereportid TO trackedentityaggregatereportid;
ALTER TABLE trackedentityaggregatereportusergroupaccesses RENAME CONSTRAINT patientaggregatereportusergroupaccesses_pkey TO trackedentityaggregatereportusergroupaccesses_pkey;


ALTER TABLE patienttabularreport RENAME TO trackedentitytabularreport
ALTER TABLE trackedentitytabularreport RENAME COLUMN patienttabularreportid TO trackedentitytabularreportid;
ALTER TABLE trackedentitytabularreport RENAME CONSTRAINT patienttabularreport_pkey TO trackedentitytabularreport_pkey;
ALTER TABLE trackedentitytabularreport RENAME CONSTRAINT fk_patientaggregatereport_programstage TO fk_tabularreport_programstage;
ALTER TABLE trackedentitytabularreport RENAME CONSTRAINT fk_patienttabularreport_userid TO fk_tabularreport_userid;
ALTER TABLE trackedentitytabularreport RENAME CONSTRAINT fk_patienttabularreport_program TO trackedentitytabularreport_code_key;
ALTER TABLE trackedentitytabularreport RENAME CONSTRAINT patienttabularreport_code_key TO trackedentitytabularreport_name_key;
ALTER TABLE trackedentitytabularreport RENAME CONSTRAINT patienttabularreport_name_key TO trackedentitytabularreport_uid_key;


ALTER TABLE patienttabularreport_dimensions RENAME TO trackedentitytabularreport_dimensions;
ALTER TABLE trackedentitytabularreport_dimensions RENAME COLUMN patienttabularreportid TO trackedentitytabularreportid;
ALTER TABLE trackedentitytabularreport_dimensions RENAME CONSTRAINT patienttabularreport_dimensions_pkey TO trackedentitytabularreport_dimensiona_pkey;
ALTER TABLE trackedentitytabularreport_dimensions RENAME CONSTRAINT fk_patienttabularreportidid_dimensions TO fk_tabularreportidid_dimensions;


ALTER TABLE patienttabularreport_filters RENAME TO trackedentitytabularreport_filters;
ALTER TABLE trackedentitytabularreport_filters RENAME COLUMN patienttabularreportid TO trackedentitytabularreportid;
ALTER TABLE trackedentitytabularreport_filters RENAME CONSTRAINT patienttabularreport_filters_pkey TO trackedentitytabularreport_filters_pkey;
ALTER TABLE trackedentitytabularreport_filters RENAME CONSTRAINT fk_patienttabularreportid_filters TO fk_tabularreportid_filters;


ALTER TABLE patienttabularreportusergroupaccesses RENAME TO trackedentitytabularreportusergroupaccesses;
ALTER TABLE trackedentitytabularreportusergroupaccesses RENAME COLUMN patienttabularreportid TO trackedentitytabularreportid;
ALTER TABLE trackedentitytabularreportusergroupaccesses RENAME CONSTRAINT patienttabularreportusergroupaccesses_pkey TO trackedentitytabularreportusergroupaccesses_pkey;


ALTER TABLE relationship RENAME COLUMN patientaid TO trackedentityinstanceaid;
ALTER TABLE relationship RENAME COLUMN patientbid TO trackedentityinstancebid;
ALTER TABLE relationship RENAME CONSTRAINT fk_relationship_patientida TO fk_relationship_trackedentityinstanceida;
ALTER TABLE relationship RENAME CONSTRAINT fk_relationship_patientidb TO fk_relationship_trackedentityinstanceidb;


ALTER TABLE patientmobilesetting rename to trackedentitymobilesetting;
ALTER TABLE trackedentitymobilesetting RENAME COLUMN patientmobilesettingid TO trackedentitymobilesettingid;
ALTER TABLE trackedentitymobilesetting RENAME CONSTRAINT patientmobilesetting_pkey TO trackedentitymobilesetting_pkey;


ALTER TABLE dashboarditem_patienttabularreports rename to dashboarditem_trackedentitytabularreports;
ALTER TABLE dashboarditem_trackedentitytabularreports RENAME COLUMN patienttabularreportid TO trackedentitytabularreportid;
ALTER TABLE dashboarditem_trackedentitytabularreports RENAME CONSTRAINT dashboarditem_patienttabularreports_pkey TO dashboarditem_tabularreports_pkey;
ALTER TABLE dashboarditem_trackedentitytabularreports RENAME CONSTRAINT fk_dashboarditem_patienttabularreports_patienttabularreportid TO fk_dashboarditem_tabularreports_tabularreportid;
ALTER TABLE dashboarditem_trackedentitytabularreports RENAME CONSTRAINT fk_dashboarditem_patienttabularreports_dashboardid TO fk_dashboarditem_tabularreports_dashboardid;



ALTER TABLE program_attributes RENAME COLUMN attributeid TO trackedentityattributeid; 
ALTER TABLE program_attributes RENAME COLUMN patientattributeid TO trackedentityattributeid;


ALTER TABLE programinstance RENAME COLUMN patientcommentid TO trackedentitycommentid;
ALTER TABLE programinstance RENAME COLUMN patientid TO trackedentityinstanceid;

ALTER TABLE programstage_dataelements RENAME COLUMN patienttabularreportid TO trackedentitytabularreportid;


