<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="org.wso2.carbon.jndi.resource.ui.JNDIResourceConfigurationClient" %>
<%@ page import="org.wso2.carbon.jndi.resource.mgt.data.xsd.Resource" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>


<script language="javascript">
    function jndiSave(form){
        // validation should go here
        form.submit();
        return true;
    }
</script>

<form method="post" name="jndicreationform" id="jndicreationform"
      action="savejndiresource.jsp">

    <div id="middle">
        <h2>
            New JNDI Resource
        </h2>


        <div id="workArea">
            <table class="styledLeft noBorders" cellspacing="0" cellpadding="0" border="0"
                   style="border-bottom:none">
                <thead>
                <tr>
                    <th colspan="3">
                        New JNDI Resource
                    </th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td style="width:170px;">JNDI Name<span class='required'>*</span></td>
                    <td align="left">
                        <input type="text" id="name" name="name"/>
                    </td>
                </tr>
                <tr>
                    <td style="width:170px;">Auth<span class='required'>*</span></td>
                    <td align="left">
                        <input type="text" id="auth" name="auth"/>
                    </td>
                </tr>
                <tr>
                    <td style="width:170px;">Type<span class='required'>*</span></td>
                    <td align="left">
                        <input type="text" id="type" name="type"/>
                    </td>
                </tr>

                <tr>
                    <td style="width:170px;">Description<span class='required'>*</span></td>
                    <td align="left">
                        <input type="text" id="description" name="description"/>
                    </td>
                </tr>
                <tr>
                    <td style="width:170px;">Factory<span class='required'>*</span></td>
                    <td align="left">
                        <input type="text" id="factory" name="factory"/>
                    </td>
                </tr>
                <tr>
                    <td style="width:170px;">Physical Name<span class='required'>*</span></td>
                    <td align="left">
                        <input type="text" id="physicalName" name="physicalName"/>
                    </td>
                </tr>
                </tbody>
            </table>

            <table class="styledLeft noBorders" cellspacing="0" cellpadding="0" border="0"
                   style="border-bottom:none">
                <tbody>
                <tr>
                    <td class="buttonRow" colspan="2">
                        <input class="button" value="Save" onclick="jndiSave(document.jndicreationform)" type="button">
                        <input class="button" value="Cancel"
                               onclick="document.location.href='index.jsp'" type="reset">
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</form>
