<script type='text/javascript'>
    function getList(that) {
		fetchList(_opt[0].set.id == that.id ? 0 : 1);
	}

    function fetchList(i) {
   		setErrorTag(WAIT);
//debugger;
    	if (i == 0) { 
        	DWRUtil.removeAllOptions(_opt[1].list);
    		if (typeof assignmentBean == 'object' && _opt[0].bean == assignmentBean) { 
    			_opt[1].refreshList = true;
    		} else {
        		DWRUtil.removeAllOptions(_opt[1].set);
    			_opt[1].refreshList = false;
    		}
    	}

        DWRUtil.removeAllOptions(_opt[i].list);

		var setState = eval("_opt[i].bean.set" + _opt[i].name + "Set");
		if (setState != undefined)
   			setState(_opt[i].set.value);
		
		if (!isPrefetched(i)) {
			if (_opt[0].role != undefined)
        		DWRUtil.removeAllOptions(_opt[0].role);
			if (_opt[0].role2 != undefined)
				DWRUtil.removeAllOptions(_opt[0].role2);

			if (_opt[i].data != undefined)
				_opt[i].data.style.display="none";

			hideRoleData();

        	if (typeof _opt[i].bean.removeRole == 'function') 
        		_opt[i].bean.removeRole();
        }
        
        if (_opt[i].set.value == -1) {
			setErrorTag("&nbsp;");
        	return;
        }
        
//debugger;
		var callBack = setCallBackList(_opt[i].name);
		
		if (i == 1 && _opt[0].set.value != "-1" && _opt[0].list.selectedIndex != -1
					&& _opt[1].set.value != "-1" && _opt[1].set.value != "0") {

			var ruleSetId = -1;
			if (typeof(_rowId) != "undefined" && _rowId != null) {
  				var a = _rowId.split(/,/);
				ruleSetId = parseInt(a[0]);
			}
			
			if (_opt[0].name == "Rule" && _opt[1].name == "Group")
				setDataManager.getGroupSetDataView(_opt[0].set.value, _opt[0].list.value, _opt[1].set.value, _opt[1].filter.value, callBack);
			else if (_opt[0].name == "Target" && _opt[1].name == "Group")
				setDataManager.getUserTargetDataView(_opt[0].set.value, _opt[0].list.value, _opt[1].set.value, ruleSetId, $(sourceDataRuleCode).innerHTML, _opt[1].filter.value, callBack);
			else if (_opt[0].name == "Source" && _opt[1].name == "Rule")
				setDataManager.getUserSourceDataView(_opt[0].set.value, _opt[0].list.value, _opt[1].set.value, $(userDataRuleCode).innerHTML, _opt[1].filter.value, callBack);
			else if (_opt[0].name == "Target" && _opt[1].name == "LdapGroup")
        		setDataManager.getLdapTargetGroupTabDataView(_boxOptions[0][_opt[0].list.selectedIndex].dn, null, callBack);
			else if (_opt[0].name == "Target" && _opt[1].name == "LdapGroup2")
        		setDataManager.getLdapTargetGroupSetDataView(_boxOptions[0][_opt[0].list.selectedIndex].dn, null, callBack);
		} else {
			if (_opt[i].name == "Rule") {
//    			if (typeof assignmentBean == 'object' && _opt[0].bean == assignmentBean) 
        			setDataManager.getRuleSetList(_opt[i].set.value, _opt[i].filter.value, callBack);
//        		else
//        			setDataManager.getRuleSetListMarked(_opt[i].set.value, _opt[i].filter.value, callBack);
			} else if (_opt[i].name == "Group") {
//    			if (typeof assignmentBean == 'object' && _opt[0].bean == assignmentBean) 
        			setDataManager.getGroupSetList(_opt[i].set.value, _opt[i].filter.value, callBack);
//				else
//        			setDataManager.getGroupSetListMarked(_opt[i].set.value, _opt[i].filter.value, callBack);
			} else if (_opt[i].name == "Target") {
				var getDn = 0; 
				if (typeof ldapMembershipBean == 'object' && _opt[0].bean == ldapMembershipBean)
					getDn = 1;
        		setDataManager.getUserTargetList(_opt[i].set.value, _opt[i].filter.value, getDn, callBack);
			} else if (_opt[i].name == "Source")
        		setDataManager.getUserSourceList(_opt[i].set.value, _opt[i].filter.value, callBack);
		}        	
    }

	function setCallBackList(name) {
		return eval("fillList" + name);
	}
	
	var fillListRule = function(data) {
  		fillList(_opt[0].name == "Rule" ? 0 : 1, data);
  	}
	var fillListGroup = function(data) {
  		fillList(_opt[0].name == "Group" ? 0 : 1, data);
  	}
	var fillListTarget = function(data) {
  		fillList(_opt[0].name == "Target" ? 0 : 1, data);
  	}
	var fillListSource = function(data) {
  		fillList(_opt[0].name == "Source" ? 0 : 1, data);
  	}
	var fillListLdapGroup = function(data) {
  		fillList(_opt[0].name == "LdapGroup" ? 0 : 1, data);
  	}
	var fillListLdapGroup2 = function(data) {
  		fillList(_opt[0].name == "LdapGroup2" ? 0 : 1, data);
  	}

    function fillList(i, data) {
//debugger;    
       	DWRUtil.removeAllOptions(_opt[i].list);

		if (!isPrefetched(i)) {
			
			var setState = eval("_opt[i].bean.set" + _opt[i].name + "List");
			if (setState != undefined)
				setState(-1);

			if (_opt[i].data != undefined)
       			_opt[i].data.style.display="none";
    	}

		initBoxOptions(i, data);

		if (data.length > 0) {
			if (_opt[i].name == "Rule") {
				DWRUtil.addOptions(_opt[i].list, data, 'ruleId', 'value');
			} else if (_opt[i].name == "Group") {
				DWRUtil.addOptions(_opt[i].list, data, 'groupId', 'value');
			} else if (_opt[i].name == "Target") {
        		DWRUtil.addOptions(_opt[i].list, data, 'guid', 'value');
			} else if (_opt[i].name == "Source") {
        		DWRUtil.addOptions(_opt[i].list, data, 'guid', 'value');
			} else if (_opt[i].name == "LdapGroup") {
        		DWRUtil.addOptions(_opt[i].list, data, 'guid', 'value');
			} else if (_opt[i].name == "LdapGroup2") {
        		DWRUtil.addOptions(_opt[i].list, data, 'guid', 'value');
			}
        }
		if (isPrefetched(i)) {
			if (_opt[i].listState != undefined && _opt[i].listState != -1) {
				setBoxOptionsIndexByValue(i, _opt[i].listState);
				setIndexByValue(_opt[i].list, _opt[i].listState);
			}
			
			_opt[i].prefetch = false;
//debugger;			
			if (i == 0 && _opt[1].set.value != -1) {
				_opt[1].prefetch = true;
				fetchList(1);
			}
		} else {
			if (i == 0 && _opt[0].set.value != -1 && _opt[1].set.value != -1) {
				if (_opt[1].refreshList != undefined && _opt[1].refreshList) {
					_opt[1].refreshList = false;
					fetchList(1);
				}
			}
		}

		if (i == 1 && _opt[0].set.value != -1 && _opt[0].list.selectedIndex != -1 && _opt[1].set.value != -1) {
			fetchRoles(1);
			return;
		} else
			checkButtons(_opt);

		if (i == 0 && typeof getAssignedGroupSetsForRuleSet == 'function') {
			getAssignedGroupSetsForRuleSet();
		}

		if (i == 0 && typeof setRuleSet == 'function') {
			setRuleSet();
		}
    }

    function getData(that) {
       	setErrorTag(WAIT);
		fetchData(_opt[0].list.id == that.id ? 0 : 1);
	}

    function fetchData(i) {
//debugger;
		if (i == 0) {
			if (_opt[0].role != undefined)
        		DWRUtil.removeAllOptions(_opt[0].role);
			if (_opt[0].role2 != undefined)
				DWRUtil.removeAllOptions(_opt[0].role2);

			hideRoleData();

			if (typeof _opt[i].bean.removeRole == 'function') 
				_opt[i].bean.removeRole();
		}
		
		var setState = eval("_opt[i].bean.set" + _opt[i].name + "List");
		if (setState != undefined)
			setState(_opt[i].list.value);
       	
       	fillData(i);
    }

    function fillData(i) {
//    debugger;
   		var idx = _opt[i].list.selectedIndex;
		var data = getBoxOptions(i);
		if (data.length == 0 || idx == -1) {
			if (_opt[i].data != undefined)
        		_opt[i].data.style.display="none";

			hideRoleData();
			checkButtons(_opt);
			return;
		}
			
		if (_opt[i].data != undefined)
        	_opt[i].data.style.display="block";
        	
        switch(_opt[i].name) {
			case "Rule":
        		DWRUtil.setValue("ruleDataName", data[idx].text);
        		DWRUtil.setValue("ruleDataValue", data[idx].text);
        		DWRUtil.setValue("ruleDataDescription", data[idx].description);
        		DWRUtil.setValue("ruleDataTimestamp", data[idx].lastModifiedDate);
        		break;
			case "Group":
        		DWRUtil.setValue("groupDataName", data[idx].text);
        		DWRUtil.setValue("groupDataValue", data[idx].dn.replace(/,/g,", "));
				DWRUtil.setValue("groupDataDescription", data[idx].description);
        		DWRUtil.setValue("groupDataTimestamp", data[idx].lastModifiedDate);
        		break;
			case "Target":
				if (_opt[1].name == "LdapGroup") {
        			DWRUtil.setValue("userDataName", data[idx].text);
        			DWRUtil.setValue("userDataEmployeeId", data[idx].value);
        		} else {
        			DWRUtil.setValue("userDataName2", data[idx].text);
        			DWRUtil.setValue("userDataEmployeeId2", data[idx].value);
        		}
        		break;
			case "Source":
        		DWRUtil.setValue("userDataName", data[idx].text);
        		DWRUtil.setValue("userDataEmployeeId", data[idx].value);
        		DWRUtil.setValue("userDataTargetCode", data[idx].targetCode);
        		DWRUtil.setValue("userDataRuleCode", data[idx].ruleCode);
        		break;
        }

		if (i == 0) {
			if (typeof getUserSourceData == 'function') {
				getUserSourceData();
			} else {
				if (_opt[0].set.value != -1) {
    				if (typeof assignmentBean == 'object' && _opt[0].bean == assignmentBean) {
    					if (_opt[1].set.value != -1) { 
							fetchList(1);
							return;
						}
					} else {
						if (_opt[1].set.options.length == 1)
							_opt[1].set.selectedIndex = 0;
							
						_opt[1].set.onchange();
						return;
					}
				}
				
				checkButtons(_opt);
			}
		} else {
			if (typeof setRoleData == 'function')
				setRoleData();
			else
				checkButtons(_opt);
		}
    }

	function fetchRoles(i) {
		var ruleSetId = -1;
		if (typeof(_rowId) != "undefined" && _rowId != null) {
  			var a = _rowId.split(/,/);
			ruleSetId = parseInt(a[0]);
		}
	
		if (_opt[0].name == "Rule" && _opt[1].name == "Group")
			setDataManager.getRoles(_opt[0].set.value, _opt[0].list.value, _opt[1].set.value, 0, "", ' ', 'a', fillRoles);
		else if (_opt[0].name == "Target" && _opt[1].name == "Group")
			setDataManager.getUserTargetGroupRoles(_opt[0].set.value, _opt[0].list.value, _opt[1].set.value, ruleSetId, $(sourceDataRuleCode).innerHTML, 'I', fillRoles);
		else if (_opt[0].name == "Source" && _opt[1].name == "Rule")
			setDataManager.getUserSourceRuleRoles(_opt[0].set.value, _opt[0].list.value, _opt[1].set.value, $(userDataRuleCode).innerHTML, 'I', fillRoles);
		else if (_opt[0].name == "Target" && _opt[1].name == "LdapGroup")
       		setDataManager.getLdapTargetGroupTabRoles(_boxOptions[0][_opt[0].list.selectedIndex].dn, fillRoles);
		else if (_opt[0].name == "Target" && _opt[1].name == "LdapGroup2")
       		setDataManager.getLdapTargetGroupSetRoles(_boxOptions[0][_opt[0].list.selectedIndex].dn, fillRoles);
	}

    function fillRoles(data) {
		_roleAttributes = null;
		initRoleAttributes(data);

       	DWRUtil.removeAllOptions(_opt[0].role);
		if (_opt[0].role2 != undefined)
			DWRUtil.removeAllOptions(_opt[0].role2);

		hideRoleData();

		var ruleSetId = -1;
		if (typeof(_rowId) != "undefined" && _rowId != null) {
  			var a = _rowId.split(/,/);
			ruleSetId = parseInt(a[0]);
		}

		if (_opt[0].name == "Rule" && _opt[1].name == "Group") {
			if (data.length > 0)
       			DWRUtil.addOptions(_opt[0].role, data, 'groupId', 'displayName');
			checkButtons(_opt);
		} else if (_opt[0].name == "Target" && _opt[1].name == "Group") {
			if (data.length > 0)
				DWRUtil.addOptions(_opt[0].role, data, 'groupId', 'displayName');
			setDataManager.getUserTargetGroupRoles(_opt[0].set.value, _opt[0].list.value, _opt[1].set.value, ruleSetId, $(sourceDataRuleCode).innerHTML, 'E', fillRoles2);
		} else if (_opt[0].name == "Source" && _opt[1].name == "Rule") {
			if (data.length > 0)
       			DWRUtil.addOptions(_opt[0].role, data, 'ruleId', 'displayName');
			setDataManager.getUserSourceRuleRoles(_opt[0].set.value, _opt[0].list.value, _opt[1].set.value, $(userDataRuleCode).innerHTML, 'E', fillRoles2);
		} else if (_opt[0].name == "Target" && _opt[1].name == "LdapGroup") {
			if (data.length > 0)
       			DWRUtil.addOptions(_opt[0].role, data, 'guid', 'value');
			checkButtons(_opt);
		} else if (_opt[0].name == "Target" && _opt[1].name == "LdapGroup2") {
			if (data.length > 0)
       			DWRUtil.addOptions(_opt[0].role, data, 'guid', 'value');
			checkButtons(_opt);
		}
	}

    function fillRoles2(data) {
		initRoleAttributes(data); // the second call to the function adds a data

		if  (_opt[0].name == "Target" && _opt[1].name == "Group") {
			if (data.length > 0)
       			DWRUtil.addOptions(_opt[0].role2, data, 'groupId', 'displayName');
		} else if (_opt[0].name == "Source" && _opt[1].name == "Rule") {
			if (data.length > 0)
       			DWRUtil.addOptions(_opt[0].role2, data, 'ruleId', 'displayName');
		}

		checkButtons(_opt);
	}
    
    function roleSelectionChanged(that) {
    	var source, middle, target, assignButton, dismissButton, roleType;
  		target = that;
		var lastChar = that.id.substr(that.id.length - 1, 1)
		dismissButton = $(_opt[0].btnDismiss + lastChar);
		dismissButton.disabled = false;

		if (typeof assignmentBean == 'object' && _opt[0].bean == assignmentBean ||
			typeof ldapMembershipBean == 'object' && _opt[0].bean == ldapMembershipBean)
			return;

		if (typeof exceptionBean == 'object' && _opt[0].bean == exceptionBean)
			roleType = 'e';
		else if	(typeof businessExceptionBean == 'object' && _opt[0].bean == businessExceptionBean)
			roleType = 'b';
		else
			return;
		
		if (target.selectedIndex != -1)
			setDataManager.getExceptionDates(
											_opt[0].set.value,
											_opt[1].set.value,
											_opt[0].list.value,
											target.value,
											roleType,
											setExceptionDates);				
	}

	function hideRoleData(whatBox) {
		var suffix = null;
		
		if (whatBox != undefined)
			suffix = whatBox;

		if (suffix == null) {
			if ($("ruleGroupData") != null && $(_opt[1].list).selectedIndex == -1)
				$("ruleGroupData").style.display="none";
			if ($("ruleGroupDataI") != null && $(_opt[0].role) != null && $(_opt[0].role).selectedIndex == -1)
				$("ruleGroupDataI").style.display="none";
			if ($("roleDataI") != null && $(_opt[0].role) != null && $(_opt[0].role).selectedIndex == -1)
				$("roleDataI").style.display="none";
			if ($("roleData2I") != null && $(_opt[0].role) != null && $(_opt[0].role).selectedIndex == -1)
				$("roleData2I").style.display="none";
			if ($("ruleGroupDataE") != null && $(_opt[0].role2) != null && $(_opt[0].role2).selectedIndex == -1)
				$("ruleGroupDataE").style.display="none";
			if ($("roleDataE") != null && $(_opt[0].role2) != null && $(_opt[0].role2).selectedIndex == -1)
				$("roleDataE").style.display="none";
		} else {
			if ($("roleData" + suffix) != null)
				$("roleData" + suffix).style.display="none";
			if ($("roleData2" + suffix) != null)
				$("roleData2" + suffix).style.display="none";
			if ($("ruleGroupData" + suffix) != null)
				$("ruleGroupData" + suffix).style.display="none";
		}
	}
</script>	