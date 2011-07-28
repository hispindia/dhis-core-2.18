// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramAttributeDetails( programAttributeId )
{
	$.ajax({
		url: 'getProgramAttribute.action?id=' + programAttributeId,
		cache: false,
		dataType: "xml",
		success: programAttributeReceived
	});
}

function programAttributeReceived( programAttributeElement )
{
	setInnerHTML( 'idField', $( programAttributeElement).find('id' ).text() );
	setInnerHTML( 'nameField',  $( programAttributeElement).find('name').text() );	
    setInnerHTML( 'descriptionField', $( programAttributeElement).find('description').text() );
    
    var valueTypeMap = { 'NUMBER':i18n_number, 'BOOL':i18n_yes_no, 'TEXT':i18n_text, 'DATE':i18n_date, 'COMBO':i18n_combo };
    var valueType =  $( programAttributeElement).find('valueType' ).text();    
	
    setInnerHTML( 'valueTypeField', valueTypeMap[valueType] );    
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove Program Attribute
// -----------------------------------------------------------------------------

function removeProgramAttribute( programAttributeId, name )
{
	removeItem( programAttributeId, name, i18n_confirm_delete, 'removeProgramAttribute.action' );	
}

ATTRIBUTE_OPTION = 
{
	selectValueType : function (this_)
	{
		if ( jQuery(this_).val() == "COMBO" )
		{
			jQuery("#attributeComboRow").show();
			if( jQuery("#attrOptionContainer").find("input").length ==0 ) 
			{
				ATTRIBUTE_OPTION.addOption();
				ATTRIBUTE_OPTION.addOption();
			}
		}else {
			jQuery("#attributeComboRow").hide();
		}
	},
	checkOnSubmit : function ()
	{
		if( jQuery("#valueType").val() != "COMBO" ) 
		{
			jQuery("#attrOptionContainer").children().remove();
			return true;
		}else {
			$("input","#attrOptionContainer").each(function(){ 
				if( !jQuery(this).val() )
					jQuery(this).remove();
			});
			if( $("input","#attrOptionContainer").length < 2)
			{
				alert(i18n_at_least_2_option);
				return false;
			}else return true;
		}
	},
	addOption : function ()
	{
		jQuery("#attrOptionContainer").append(ATTRIBUTE_OPTION.createInput());
	},
	remove : function (this_, optionId)
	{
		if( jQuery(this_).siblings("input").attr("name") != "attrOptions")
		{
			jQuery.getJSON("removeProgramAttributeOption.action",
				{ 
					id: optionId 
				},function( json )
				{
					var type  = json.response;
					
					if( type == "success")
					{
						jQuery(this_).parent().parent().remove();
						showSuccessMessage(json.message);
					}else 
					{
						showWarningMessage(json.message);
					}
				});
		}else
		{
			jQuery(this_).parent().parent().remove();
		}
	},
	removeInAddForm : function(this_)
	{
		jQuery(this_).parent().parent().remove();
	},
	createInput : function ()
	{
		return "<tr><td><input type='text' name='attrOptions' style='width:28em'/><a href='#' style='text-decoration: none; margin-left:0.5em;' title='"+i18n_remove_option+"'  onClick='ATTRIBUTE_OPTION.remove(this,null)'>[ - ]</a></td></tr>";
	}
}