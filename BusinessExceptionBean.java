package com.autheus.authmanager.common.beans;

import org.directwebremoting.WebContextFactory;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * <p>
 * Object to handle form session variable.
 * </p>
 *
 * @author $Author: davir $
 * @version $Revision: 1.6 $  ($Date: 2007/11/25 20:39:23 $)
 */
public class BusinessExceptionBean implements Serializable {

	public class RoleDates {
		String startDate;
		String endDate;
		
		RoleDates(String startDate, String endDate) {
			this.startDate = startDate;
			this.endDate = endDate;
		}

		public String getStartDate() {
			return this.startDate;
		}

		public String getEndDate() {
			return this.endDate;
		}
	}

    public Integer getRuleSet() {
        HttpSession session = WebContextFactory.get().getSession();
        return (Integer)session.getAttribute("BusinessExceptionBean.ruleSet");
    }
    
    public void setRuleSet(int ruleSet) {
        HttpSession session = WebContextFactory.get().getSession();
        session.setAttribute("BusinessExceptionBean.ruleSet", new Integer(ruleSet));
    }

    public Integer getRuleList() {
        HttpSession session = WebContextFactory.get().getSession();
        return (Integer)session.getAttribute("BusinessExceptionBean.ruleList");
    }
    
    public void setRuleList(int ruleList) {
        HttpSession session = WebContextFactory.get().getSession();
        session.setAttribute("BusinessExceptionBean.ruleList", new Integer(ruleList));
    }
    
    public Integer getSourceSet() {
        HttpSession session = WebContextFactory.get().getSession();
        return (Integer)session.getAttribute("BusinessExceptionBean.sourceSet");
    }

    public void setSourceSet(int sourceSet) {
        HttpSession session = WebContextFactory.get().getSession();
        session.setAttribute("BusinessExceptionBean.sourceSet", new Integer(sourceSet));
    }

    public String getSourceList() {
        HttpSession session = WebContextFactory.get().getSession();
        return (String)session.getAttribute("BusinessExceptionBean.sourceList");
    }

    public void setSourceList(String sourceList) {
        HttpSession session = WebContextFactory.get().getSession();
        session.setAttribute("BusinessExceptionBean.sourceList", sourceList);
    }
	
	public String getRoleInclude() {
        HttpSession session = WebContextFactory.get().getSession();
        return (String)session.getAttribute("BusinessExceptionBean.roleInclude");
    }
    
    public void setRoleInclude(String roleInclude) {
        HttpSession session = WebContextFactory.get().getSession();
        session.setAttribute("BusinessExceptionBean.roleInclude", roleInclude);
    }

    public void setRoleIncludeDates(String startDate, String endDate) {
        HttpSession session = WebContextFactory.get().getSession();
        session.setAttribute("BusinessExceptionBean.roleIncludeDates", new RoleDates(startDate, endDate));
    }
    
    public void removeRoleInclude() {
        HttpSession session = WebContextFactory.get().getSession();
        session.removeAttribute("BusinessExceptionBean.roleInclude");
    }

    public void removeRoleIncludeDates() {
        HttpSession session = WebContextFactory.get().getSession();
        session.removeAttribute("BusinessExceptionBean.roleIncludeDates");
    }
    
    public String getRoleExclude() {
        HttpSession session = WebContextFactory.get().getSession();
        return (String)session.getAttribute("BusinessExceptionBean.roleExclude");
    }
    
    public void setRoleExclude(String roleExclude) {
        HttpSession session = WebContextFactory.get().getSession();
        session.setAttribute("BusinessExceptionBean.roleExclude", roleExclude);
    }

    public void setRoleExcludeDates(String startDate, String endDate) {
        HttpSession session = WebContextFactory.get().getSession();
        session.setAttribute("BusinessExceptionBean.roleExcludeDates", new RoleDates(startDate, endDate));
    }
    
    public void removeRoleExclude() {
        HttpSession session = WebContextFactory.get().getSession();
        session.removeAttribute("BusinessExceptionBean.roleExclude");
    }

    public void removeRoleExcludeDates() {
        HttpSession session = WebContextFactory.get().getSession();
        session.removeAttribute("BusinessExceptionBean.roleExcludeDates");
    }
    
    public String getSourceFilter() {
        HttpSession session = WebContextFactory.get().getSession();
        return (String)session.getAttribute("BusinessExceptionBean.sourceFilter");
    }
    
    public void setSourceFilter(String sourceFilter) {
        HttpSession session = WebContextFactory.get().getSession();
        session.setAttribute("BusinessExceptionBean.sourceFilter", sourceFilter);
    }

    public String getRuleFilter() {
        HttpSession session = WebContextFactory.get().getSession();
        return (String)session.getAttribute("BusinessExceptionBean.ruleFilter");
    }
    
    public void setRuleFilter(String ruleFilter) {
        HttpSession session = WebContextFactory.get().getSession();
        session.setAttribute("BusinessExceptionBean.ruleFilter", ruleFilter);
    }
}
