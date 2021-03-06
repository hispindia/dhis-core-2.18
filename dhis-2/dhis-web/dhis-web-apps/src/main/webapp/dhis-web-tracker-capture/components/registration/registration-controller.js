trackerCapture.controller('RegistrationController', 
        function($rootScope,
                $scope,
                $location,
                $timeout,
                orderByFilter,
                AttributesFactory,
                DHIS2EventFactory,
                TEService,
                TEIService,
                EnrollmentService,
                DialogService,
                CurrentSelection,
                OptionSetService,
                EventUtils,
                DateUtils,
                storage) {
    
    $scope.today = DateUtils.getToday();
    
    $scope.optionSets = CurrentSelection.getOptionSets();
            
    if(!$scope.optionSets){
        $scope.optionSets = [];
        OptionSetService.getAll().then(function(optionSets){
            angular.forEach(optionSets, function(optionSet){                        
                $scope.optionSets[optionSet.id] = optionSet;
            });

            CurrentSelection.setOptionSets($scope.optionSets);
        });
    }
    
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    $scope.enrollment = {dateOfEnrollment: '', dateOfIncident: ''};   
    
    /*AttributesFactory.getWithoutProgram().then(function(atts){
        $scope.attributes = atts;
    });*/
            
    $scope.trackedEntities = {available: []};
    TEService.getAll().then(function(entities){
        $scope.trackedEntities.available = entities;   
        $scope.trackedEntities.selected = $scope.trackedEntities.available[0];
    });
    
    //watch for selection of program
    $scope.$watch('selectedProgram', function() {        
        $scope.getAttributes();
    });    
        
    $scope.getAttributes = function(){

        if($scope.selectedProgram){
            AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){
                $scope.attributes = atts;
                $scope.attributesById = [];
                angular.forEach(atts, function(att){
                    $scope.attributesById[att.id] = att;
                });
            });           
        }
        else{            
            AttributesFactory.getWithoutProgram().then(function(atts){
                $scope.attributes = orderByFilter(atts, '-sortOrderInListNoProgram').reverse();                
                $scope.attributesById = [];
                angular.forEach(atts, function(att){
                    $scope.attributesById[att.id] = att;
                });
            });
        }
    };
    
    $scope.registerEntity = function(destination){
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid ){
            return false;
        }
        
        //form is valid, continue the registration
        //get selected entity
        var selectedTrackedEntity = $scope.trackedEntities.selected.id; 
        if($scope.selectedProgram){
            selectedTrackedEntity = $scope.selectedProgram.trackedEntity.id;
        }
        
        //get tei attributes and their values
        //but there could be a case where attributes are non-mandatory and
        //registration form comes empty, in this case enforce at least one value        
        var result = TEIService.reconstructForWebApi($scope.attributes, $scope.attributesById, $scope.optionSets);        
        $scope.formEmpty = result.formEmpty;
        
        if($scope.formEmpty){
            //registration form is empty
            return false;
        }
        
        //prepare tei model and do registration
        $scope.tei = {trackedEntity: selectedTrackedEntity, orgUnit: $scope.selectedOrgUnit.id, attributes: result.attributes };   
        var teiId = '';
        TEIService.register($scope.tei).then(function(tei){
            
            if(tei.status === 'SUCCESS'){
                
                teiId = tei.reference;
                
                //registration is successful and check for enrollment
                if($scope.selectedProgram){    
                    //enroll TEI
                    var enrollment = {trackedEntityInstance: teiId,
                                program: $scope.selectedProgram.id,
                                status: 'ACTIVE',
                                dateOfEnrollment: DateUtils.formatFromUserToApi($scope.enrollment.dateOfEnrollment),
                                dateOfIncident: $scope.enrollment.dateOfIncident === '' ? DateUtils.formatFromUserToApi($scope.enrollment.dateOfEnrollment) : DateUtils.formatFromUserToApi($scope.enrollment.dateOfIncident)
                            };                           
                    var en = angular.copy($scope.enrollment);
                    EnrollmentService.enroll(enrollment).then(function(data){
                        if(data.status !== 'SUCCESS'){
                            //enrollment has failed
                            var dialogOptions = {
                                    headerText: 'enrollment_error',
                                    bodyText: data.description
                                };
                            DialogService.showDialog({}, dialogOptions);
                            return;
                        }
                        else{
                            enrollment.enrollment = data.reference;
                            $scope.autoGenerateEvents(teiId,$scope.selectedProgram, $scope.selectedOrgUnit, en);                          
                        }
                    });
                }
            }
            else{
                //registration has failed
                var dialogOptions = {
                        headerText: 'registration_error',
                        bodyText: tei.description
                    };
                DialogService.showDialog({}, dialogOptions);
                return;
            }
            
            $timeout(function() { 
                //reset form
                angular.forEach($scope.attributes, function(attribute){
                    delete attribute.value;                
                });            

                $scope.enrollment.dateOfEnrollment = '';
                $scope.enrollment.dateOfIncident =  '';
                $scope.outerForm.submitted = false; 


                if(destination === 'DASHBOARD') {
                    $location.path('/dashboard').search({tei: teiId,                                            
                                            program: $scope.selectedProgram ? $scope.selectedProgram.id: null});
                }            
                else if(destination === 'RELATIONSHIP' ){
                    $scope.tei.trackedEntityInstance = teiId;
                    $scope.broadCastSelections();
                }
            }, 100);
            
        });
    };
    
    $scope.resetRelationshipSource = function(){
        $scope.selectedRelationshipSource = '';        
    };
    
    $scope.broadCastSelections = function(){
        angular.forEach($scope.tei.attributes, function(att){
            $scope.tei[att.attribute] = att.value;
        });
        
        $scope.tei.orgUnitName = $scope.selectedOrgUnit.name;
        $scope.tei.created = DateUtils.formatFromApiToUser(new Date());
        CurrentSelection.setRelationshipInfo({tei: $scope.tei, src: $scope.selectedRelationshipSource});
        $timeout(function() { 
            $rootScope.$broadcast('relationship', {});
        }, 100);
    };
    
    $scope.autoGenerateEvents = function(teiId, program, orgUnit, enrollment){            
            
        if(teiId && program && orgUnit && enrollment){            
            var dhis2Events = {events: []};
            angular.forEach(program.programStages, function(stage){
                if(stage.autoGenerateEvent){
                    var newEvent = {
                            trackedEntityInstance: teiId,
                            program: program.id,
                            programStage: stage.id,
                            orgUnit: orgUnit.id,                        
                            dueDate: DateUtils.formatFromUserToApi(EventUtils.getEventDueDate(null,stage, enrollment)),
                            status: 'SCHEDULE'
                        };
                    
                    if(stage.openAfterEnrollment){
                        if(stage.reportDateToUse === 'dateOfIncident'){
                            newEvent.eventDate = DateUtils.formatFromUserToApi(enrollment.dateOfIncident);
                        }
                        else{
                            newEvent.eventDate = DateUtils.formatFromUserToApi(enrollment.dateOfEnrollment);
                        }
                    }
                    
                    dhis2Events.events.push(newEvent);    
                }
            });

            if(dhis2Events.events.length > 0){
                DHIS2EventFactory.create(dhis2Events).then(function(data){
                });
            }
        }
    };    
});