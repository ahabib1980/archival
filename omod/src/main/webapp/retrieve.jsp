<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:require privilege="Retrieve Patient"
	otherwise="/login.htm"
	redirect="/module/archival/archive.form" />

<openmrs:htmlInclude
	file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude
	file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<link
	href="${pageContext.request.contextPath}/moduleResources/archival/css/bootstrap.min.css"
	rel="stylesheet" />
<link
	href="${pageContext.request.contextPath}/moduleResources/archival/css/dataTables.bootstrap4.min.css"
	rel="stylesheet" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/moduleResources/archival/themes/default.min.css"
	id="toggleCSS" />
<link type="text/css" rel="stylesheet"
	href="${pageContext.request.contextPath}/moduleResources/archival/css/jquery-confirm.min.css" />
<link type="text/css" rel="stylesheet"
	href="${pageContext.request.contextPath}/moduleResources/archival/css/archival.css" />

<style type="text/css">
body {
	font-family: Verdana !important;
	font-size: 0.8em !important;
}
</style>

<div>
	<div id="archivalHeading">
		<h2>
			<spring:message code="archival.dataRetrieval" />
		</h2>
	</div>
	<div id="retrievalAlert">
		<span id="successSpan" class="success" style="display: none"><spring:message code="archival.retrieveSuccess" /></span>
			<span id="errorSpan" class="error" style="display: none"></span>
	</div>
</div>

<div class="boxHeader${model.patientVariation}">
	<spring:message code="archival.dataRetrieval" />
</div>
<div class="box${model.patientVariation}">
	<%-- action="${pageContext.request.contextPath}/module/archival/executeQuery.form" --%>
	<div class="container" id="searchDiv">
		<form class="form-horizontal" method="get" id="search_patient_form">
			<div class="row">
				<div class="col-md-3">
					<label class="labelClass"><spring:message code="archival.id"/></label>
					<input type="text" class="form-control" name="patientIdentifier" id="patientIdentifier"></input>
					<span id="identifierError" class="text-danger"></span>
				</div>
				<div class="col-md-3">
					<label class="labelClass"><spring:message code="archival.name"/></label>
					<input type="text" class="form-control" name="patientName" id="patientName"></input>
					<span id="nameError" class="text-danger"></span>
				</div>
				<div class="col-md-3">
					<label class="labelClass"><spring:message code="archival.gender"/></label>
					<select class="form-control" name="gender" id="gender">
						<option value="">Select...</option>
						<option value="Male">Male</option>
  						<option value="Female">Female</option>
					</select>
					<span id="genderError" class="text-danger"></span>
				</div>
				<div class="col-md-3" id="searchButtonDiv">
					<button id="search" type="button" onclick="searchPatients()"><spring:message code="archival.searchBox"/></button>
					<img id="searchSpinner" src="/openmrs/images/loading.gif" style="visibility: hidden;">
				</div>
				<!-- <div class="col-md-2">
					<label class="labelClass"><spring:message code="archival.dateFrom"/></label>
					<input type="date" class="form-control" name="fromDate" id="fromDate"></input>
					<span id="fromDateError" class="text-danger"></span>
				</div>
				<div class="col-md-2">
					<label class="labelClass"><spring:message code="archival.dateTo"/></label>
					<input type="date" class="form-control" name="toDate" id="toDate"></input>
					<span id="toDateError" class="text-danger"></span>
				</div>
				<div class="col-md-2">
					<label class="labelClass"><spring:message code="archival.archivingUser"/></label>
					<select class="form-control" name="archivingUser" id="archivingUser">
						<option value="male">Select...</option>
					</select>
					<span id="queryError" class="text-danger"></span>
				</div>  -->
			</div>
		</form>
	</div>
</div>

<div class="boxHeader${model.patientVariation}" id="searchResultDiv">
	<spring:message code="archival.searchResults" />
</div>
<div class="box${model.patientVariation}" id="patientTableDiv"></div>

<script
	src="${pageContext.request.contextPath}/moduleResources/archival/js/jquery-3.3.1.min.js"></script>
<script
	src="${pageContext.request.contextPath}/moduleResources/archival/js/bootstrap.min.js"></script>
<script
	src="${pageContext.request.contextPath}/moduleResources/archival/js/jquery-ui.min.js"></script>
<script
	src="${pageContext.request.contextPath}/moduleResources/archival/js/jquery.dataTables.min.js"></script>
<script
	src="${pageContext.request.contextPath}/moduleResources/archival/js/dataTables.bootstrap4.min.js"></script>
<script
	src="${pageContext.request.contextPath}/moduleResources/archival/js/jquery-confirm.min.js"></script>

<!-- <script type="text/javascript" th:inline="javascript">  -->
<script>
	jQuery(document).ready(function() {
		console.log("document ready");
		var alertDiv = document.getElementById("retrievalAlert");
		alertDiv.style.display = "none";
	});
	
	function isValid() {
		var identifier = document.getElementById("patientIdentifier").value;
		var name = document.getElementById("patientName").value;
		var gender = document.getElementById("gender");
		var alertDiv = document.getElementById("retrievalAlert");
		var errorSpan = document.getElementById("errorSpan");
		alertDiv.style.display = "none";
		errorSpan.style.display = "none";
		successSpan.style.display = "none";
		
		console.log(identifier);
		console.log(name);
		if((identifier == null || identifier == "") && (name == null || name == "") && gender.selectedIndex == 0) {
			alertDiv.style.display = "block";
			errorSpan.style.display = "block";
			jQuery("#errorSpan").text("Please input filters to search patients");
			return false;
		}
		else {
			
			let regexName =new RegExp("^[a-zA-Z]{3,}(?: [a-zA-Z]+){0,2}$");
			let regexId =new RegExp(("^[a-zA-Z0-9]{5}[-]{1}[0-9]{1}$");
			if(name != null && name != "") {
				if(!regInt.test(name)) {
					jQuery("#nameError").text('<spring:message code="archival.invalid" />');
					return false;
				}
			}
			else {
				jQuery("#nameError").text("");
			}
			
			if(identifier != null && identifier != "") {
				if(!regInt.test(identifier)) {
					jQuery("#identifierError").text('<spring:message code="archival.invalid" />');
					return false;
				}
			}
			else {
				jQuery("#identifierError").text("");
			}
			
			jQuery("#errorSpan").text("");
			alertDiv.style.display = "none";
			errorSpan.style.display = "none";
		}
		return true;
	}
	
	function searchPatients() { 
		if(isValid()) {
			var patientTableHtml = "";
			document.getElementById("patientTableDiv").innerHTML = patientTableHtml;
			var searchSpinner = document.getElementById('searchSpinner');
			searchSpinner.style.visibility = "visible";
			var errorMsg = "";
			var alertDiv = document.getElementById("retrievalAlert");
			var successSpan = document.getElementById("successSpan");
			var errorSpan = document.getElementById("errorSpan");
			alertDiv.style.display = "none";
			errorSpan.style.display = "none";
			successSpan.style.display = "none";
			var identifierValue = document.getElementById("patientIdentifier").value;
			var nameValue = document.getElementById("patientName").value;
			var genderValue = document.getElementById("gender").value;
			jQuery.ajax({
				type : "GET",
				contentType : "application/json",
				url : '${pageContext.request.contextPath}/module/archival/searchArchivedPatients.form',
				dataType : "json",
				data : {
					identifier : identifierValue,
					name: nameValue,
					gender: genderValue
				},
				async : true,
				success : function(data) {
					console.log(data);
					if(data.status != null) {
						if(data.status == "fail") {
							errorMsg = '<spring:message code="archival.getArchivedPatientsError"/>';
		    				jQuery("#errorSpan").text(errorMsg);
							alertDiv.style.display = "block";
		    				errorSpan.style.display = "block";
						} else if (data.status == "empty") {
							errorMsg = '<spring:message code="archival.noDataFound"/>';
		    				jQuery("#errorSpan").text(errorMsg);
							alertDiv.style.display = "block";
		    				errorSpan.style.display = "block";
						}
						document.documentElement.scrollTop = 0;
					}
					else {
						console.log(data);
						patientTableHtml = patientTableHtml.concat("<table id='searchedPatientList' class='table table-striped table-bordered' style='width: 100%'>");
						patientTableHtml = patientTableHtml.concat('<thead><tr>');
						patientTableHtml = patientTableHtml.concat('<th hidden="true"><spring:message code="Patient ID" /></th>');
						patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.id" /></th>');
						patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.name" /></th>');
						patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.gender" /></th>');
						patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.dateOfBirth" /></th>');
						//patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.enrolledPrograms" /></th>');
						patientTableHtml = patientTableHtml.concat('<openmrs:hasPrivilege privilege="Retrieve Patient"><th><spring:message code="archival.retrieval" /></th></openmrs:hasPrivilege>');
						patientTableHtml = patientTableHtml.concat('</tr></thead><tbody>');
						jQuery(data).each(function() {
							if(this.patientName != undefined) {
								patientTableHtml = patientTableHtml.concat('<tr>');
								patientTableHtml = patientTableHtml.concat('<td hidden="true" id="uuid">'+ this.patientId + '</td>');
								patientTableHtml = patientTableHtml.concat('<td><a style="text-decoration: none" target="_blank" href="${pageContext.request.contextPath}/patientDashboard.form?patientId=' + this.patientId + '" class="hvr-icon-grow"><span><i class="fa fa-edit hvr-icon"></i></span>'+ this.identifier+ '</a></td>');
								patientTableHtml = patientTableHtml.concat('<td>'+ this.patientName + '</td>');
								patientTableHtml = patientTableHtml.concat('<td>'+ this.gender + '</td>');
								patientTableHtml = patientTableHtml.concat('<td>' + this.dob + '</td>');
								//patientTableHtml = patientTableHtml.concat('<td>'+this.enrolledPrograms+'</td>');
								patientTableHtml = patientTableHtml.concat('<openmrs:hasPrivilege privilege="Retrieve Patient"><td><button type="button" id="retrieve" onclick="retrievePatient('+ this.patientId + ')" style="background-color: #1aac9b; color: white; padding: 4px 11px; margin-left: 2px; border: none; border-radius: 2px; cursor: pointer;"><spring:message code="archival.retrieval"/></button></td></openmrs:hasPrivilege>');
								patientTableHtml = patientTableHtml.concat('</tr>');
							}
						});
						
						patientTableHtml = patientTableHtml.concat('</tbody></table>');
						//patientTableHtml = patientTableHtml.concat('<button id="archive" onclick="archivePatients()"><spring:message code="archival.archive"/></button>');
						//patientTableHtml = patientTableHtml.concat('<img id="archiveSpinner" src="/openmrs/images/loading.gif" style="visibility: hidden;">');
						document.getElementById("patientTableDiv").innerHTML = patientTableHtml;
	
						$('#searchedPatientList').dataTable({
								"bPaginate" : true,
								"pageLength" : 50
						});
	
					}
					searchSpinner.style.visibility = "hidden";
				},
				error : function(data) {
					console.log("fail  : " + data);
					searchSpinner.style.visibility = "hidden";
				},
				done : function(e) {
					console.log("DONE");
					searchSpinner.style.visibility = "hidden";
				}
			});
		}
	}

	function retrievePatient(id) {
		
		jQuery.ajax({
			type : "POST",
			contentType : "application/json",
			url : '${pageContext.request.contextPath}/module/archival/retievePatient.form?patientId='+ id,
			dataType : "json",
			data : {
				patientId : id
			},
			async : true,
			success : function(data) {
				console.log(data);
				if(data.status != null) {
					if(data.status == "fail") { //sometimes ajax doesn't handle the response correctly
						errorMsg = '<spring:message code="archival.patientRetrieveError"/>';
	    				jQuery("#errorSpan").text(errorMsg);
						alertDiv.style.display = "block";
						errorSpan.style.display = "block";
	    				document.documentElement.scrollTop = 0;
					}
					else if(data.status == "success") {
						alertDiv.style.display = "block";
	    				successSpan.style.display = "block";
	    				document.documentElement.scrollTop = 0;
					}
				}
				searchSpinner.style.visibility = "hidden";
			},
			error : function(data) {
				console.log("fail");
				console.log(data);
				errorMsg = '<spring:message code="archival.patientRetrieveError"/>';
				jQuery("#errorSpan").text(errorMsg);
				alertDiv.style.display = "block";
				errorSpan.style.display = "block";
				searchSpinner.style.visibility = "hidden";
				document.documentElement.scrollTop = 0;
			},
			done : function(e) {
				console.log("DONE");
				searchSpinner.style.visibility = "hidden";
			}
		});
	}
	
	jQuery(function() {

	})
</script>

<%@ include file="/WEB-INF/template/footer.jsp"%>
