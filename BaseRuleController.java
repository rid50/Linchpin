package com.autheus.authmanager.web.controller;

import com.autheus.authmanager.common.GroupSetData;

import org.springframework.validation.Errors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

import com.autheus.authmanager.facade.SourcesFacade;
import com.autheus.authmanager.facade.AssignmentFacade;
import com.autheus.authmanager.web.view.NavigationMap;
import com.autheus.authmanager.web.WebConstants;
import com.autheus.authmanager.common.GroupSetData;
import com.autheus.authmanager.common.UserAttributes;
import com.autheus.authmanager.common.UserSource;
import com.autheus.authmanager.common.RuleSetData;
import com.autheus.authmanager.data.DatasourceManager;
import com.autheus.authmanager.data.SetDataManager;
import com.autheus.framework.web.mvc.AbstractMultiActionFormController;

import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class BaseRuleController extends AbstractMultiActionFormController {

    private SourcesFacade sourcesFacade;
    private AssignmentFacade assignmentFacade;

    public SourcesFacade getSourcesFacade() {
        return sourcesFacade;
    }

    public void setSourcesFacade(SourcesFacade sourcesFacade) {
        this.sourcesFacade = sourcesFacade;
    }

    public AssignmentFacade getAssignmentFacade() {
        return assignmentFacade;
    }

    public void setAssignmentFacade(AssignmentFacade assignmentFacade) {
        this.assignmentFacade = assignmentFacade;
    }

    /**
     * Handles rule data reference loading
     * @param request
     * @param command
     * @param errors
     * @return
     * @throws ServletException
     */
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws ServletException, Exception {
		return loadReferenceData(request);
	}

    /**
     * Helper method established to get data into a Map.
     * 
     * @return map of reference items for presentation
     */
    protected Map loadReferenceData(HttpServletRequest request) throws Exception {
        Map<String,Object>referenceData = new HashMap<String,Object>();

		referenceData.put("topNavSelected", "label.rule.tab");
        
        List list = null;
        
        DatasourceManager datasourceManager = new DatasourceManager();
        
        list = datasourceManager.getRuleSets();
        referenceData.put("ruleSet", list);

        list = datasourceManager.getGroupSets();
        referenceData.put("groupSet", list);
        
        SetDataManager setDataManager = new SetDataManager();
        
        char operation;
    	Object data = null;

    	if (this instanceof RuleAssignmentController) {
        	operation = 'a';
        	Integer rules = (Integer)request.getSession().getAttribute("AssignmentBean.ruleSet");
        	Integer rule = (Integer)request.getSession().getAttribute("AssignmentBean.ruleList");
        	Integer groups = (Integer)request.getSession().getAttribute("AssignmentBean.groupSet");
        	Integer group = (Integer)request.getSession().getAttribute("AssignmentBean.groupList");
        	String ruleFilter = (String)request.getSession().getAttribute("AssignmentBean.ruleFilter");
        	String groupFilter = (String)request.getSession().getAttribute("AssignmentBean.groupFilter");

            list = null;

            if (rules != null && rules != -1) {
            	list = setDataManager.getRuleSetList(rules.intValue(), ruleFilter);
                if (list != null && list.size() != 0)
                	referenceData.put("ruleList", list);
            }
            
            if (groups != null && groups != -1) {
            	if (rules != null && rule != null)
        			list = setDataManager.getGroupSetDataView(rules.intValue(), rule.toString(), groups.intValue(), groupFilter);
            	else
            		list = setDataManager.getGroupSetList(groups.intValue(), groupFilter);

            	if (list != null && list.size() != 0)
                	referenceData.put("groupList", list);
            }
            
            data = null;
            if (rules != null && rules != -1 && rule != null) {
            	data = setDataManager.getRuleData(rules.intValue(), rule.intValue());        
            	if (data != null)
            		referenceData.put("ruleData", data);
            }
            
            if (groups != null && groups != -1 && group != null) {
            	data = setDataManager.getGroupData(groups.intValue(), group.intValue());        
            	if (data != null)
            		referenceData.put("groupData", data);
            }

			} else if (this instanceof ExceptionAssignmentController) {
    	}

    	return referenceData;
    }
}
