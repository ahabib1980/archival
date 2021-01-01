<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="Archive Patient"
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
<link type="text/css" rel="stylesheet"
	href="${pageContext.request.contextPath}/moduleResources/archival/css/archival.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/moduleResources/archival/themes/default.min.css"
	id="toggleCSS" />
<link type="text/css" rel="stylesheet"
	href="${pageContext.request.contextPath}/moduleResources/archival/css/jquery-confirm.min.css" />
	
<link type="text/css" rel="stylesheet"
	href="${pageContext.request.contextPath}/moduleResources/archival/css/font-awesome.min.css"/>

<style type="text/css">
body {
	font-family: Verdana !important;
	font-size: 0.8em !important;
}
</style>

<div>
	<div id="archivalHeading">
		<h2>
			<spring:message code="archival.title" />
		</h2>
	</div>
	<div id="archivalAlert">
		<span id="successSpan" class="success" style="display: none">Success!
			A total of <span id="count">0</span> patients were successfully
			archived <a id="downloadLink">Download</a>
		</span><span id="errorSpan" class="error" style="display: none"></span>
	</div>
</div>

<div class="boxHeader${model.patientVariation}">
	<spring:message code="archival.queryBox" />
</div>
<div class="box${model.patientVariation}">
	<%-- action="${pageContext.request.contextPath}/module/archival/executeQuery.form" --%>
	<div class="container" id="queryDiv">
		<form class="form-horizontal" method="get" id="execute_query_form">
			<div class="row">
				<div class="col-md-12">
					<span id="queryError" class="text-danger "> </span>
					<textarea class="form-control" name="query" id="query" rows="4" cols="182"></textarea>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<input type="submit" id="analyze"
						value="<spring:message code="archival.querySubmit" />"></input> <img
						id="analyzeSpinner" src="/openmrs/images/loading.gif"
						style="visibility: hidden;">
				</div>
			</div>
		</form>
	</div>
</div>

<div class="boxHeader${model.patientVariation}" id="queryResultDiv">
	<spring:message code="archival.queryResults" />
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
		var alertDiv = document.getElementById("archivalAlert");
		alertDiv.style.display = "none";

	})

	function isValid() {
		var sqlQuery = document.getElementById("query").value;
		if(sqlQuery == null || sqlQuery.trim() == "") {
			jQuery("#queryError").text('<spring:message code="archival.requiredQueryError" />');
			return false;
		}
		else {
			jQuery("#queryError").text("");
		}
		return true;
	}
	
	jQuery(function() {

		jQuery("[id*='analyze']").click(function(e) {
			e.preventDefault();
			
			var alertDiv = document.getElementById("archivalAlert");
			var successSpan = document.getElementById("successSpan");
			var errorSpan = document.getElementById("errorSpan");
			alertDiv.style.display = "none";
			errorSpan.style.display = "none";
			successSpan.style.display = "none";
			
			if(isValid()) {
				var patientTableHtml = "";
				document.getElementById("patientTableDiv").innerHTML = patientTableHtml;
				var analyzeLoader = document.getElementById('analyzeSpinner');
				analyzeLoader.style.visibility = "visible";
				var errorMsg = "";
				var sqlQuery = document.getElementById("query").value;
				jQuery.ajax({
					type : "GET",
					contentType : "application/json",
					url : '${pageContext.request.contextPath}/module/archival/executeQuery.form',
					dataType : "json",
					data : {
						query : sqlQuery
					},
					async : true,
					success : function(data) {
						console.log(data);
						if(data.status != null) {
							if(data.status == "fail") { //sometimes ajax doesn't handle the response correctly
								errorMsg = '<spring:message code="archival.validQueryError"/>';
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
							patientTableHtml = patientTableHtml.concat('<th hidden="true"><spring:message code="archival.patientId" /></th>');
							patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.id" /></th>');
							patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.name" /></th>');
							patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.gender" /></th>');
							patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.dateOfBirth" /></th>');
							patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.remove" /></th>');
							//patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.enrolledPrograms" /></th>');
							patientTableHtml = patientTableHtml.concat('</tr></thead><tbody>');
							jQuery(data).each(function() {
								if(this.patientName != undefined) {
									patientTableHtml = patientTableHtml.concat('<tr>');
									patientTableHtml = patientTableHtml.concat('<td hidden="true" id="uuid">'+ this.patientId + '</td>');
									patientTableHtml = patientTableHtml.concat('<td><a style="text-decoration: none" target="_blank" href="${pageContext.request.contextPath}/patientDashboard.form?patientId=' + this.patientId + '" class="hvr-icon-grow">'+ this.identifier+ '</a></td>');
									patientTableHtml = patientTableHtml.concat('<td>'+ this.patientName + '</td>');
									patientTableHtml = patientTableHtml.concat('<td>'+ this.gender + '</td>');
									patientTableHtml = patientTableHtml.concat('<td>' + this.dob + '</td>');
									//patientTableHtml = patientTableHtml.concat('<td>'+this.enrolledPrograms+'</td>');
									patientTableHtml = patientTableHtml.concat('<td ><img class="removeRow" title="Remove" src="/openmrs/moduleResources/archival/img/negative.png" alt="remove" border="0" /></td>');
									patientTableHtml = patientTableHtml.concat('</tr>');
								}
							});
							
							patientTableHtml = patientTableHtml.concat('</tbody></table>');
							patientTableHtml = patientTableHtml.concat('<openmrs:hasPrivilege privilege="Archive Patient">');
							patientTableHtml = patientTableHtml.concat('<button id="archive" onclick="archivePatients()"><spring:message code="archival.archive"/></button>');
							//patientTableHtml = patientTableHtml.concat('<button id="archive" ><spring:message code="archival.archive"/></button>');
							patientTableHtml = patientTableHtml.concat('<img id="archiveSpinner" src="/openmrs/images/loading.gif" style="visibility: hidden;">');
							patientTableHtml = patientTableHtml.concat('</openmrs:hasPrivilege>');
							document.getElementById("patientTableDiv").innerHTML = patientTableHtml;
		
							$('#searchedPatientList').dataTable({
										"bPaginate" : true,
										"pageLength" : 50
							});
							
							var table = $('#searchedPatientList').DataTable();
							$('#searchedPatientList tbody').on( 'click', 'img.removeRow', function () {
							    table
							        .row( $(this).parents('tr') )
							        .remove()
							        .draw();
							} );
		
						}
						analyzeLoader.style.visibility = "hidden";
					},
					error : function(data) {
						console.log("fail  : " + data);
						analyzeLoader.style.visibility = "hidden";
					},
					done : function(e) {
						console.log("DONE");
						analyzeLoader.style.visibility = "hidden";
					}
				});
			}
		});
	})

	function archivePatients() {
			var patientIds = [];
			var dataTable = $('#searchedPatientList').DataTable();
		    
			var downloadLink = document.getElementById("downloadLink");
			var archiveLoader = document.getElementById('archiveSpinner');
			var errorMsg = "";
			var alertDiv = document.getElementById("archivalAlert");
			var successSpan = document.getElementById("successSpan");
			var errorSpan = document.getElementById("errorSpan");
			alertDiv.style.display = "none";
			errorSpan.style.display = "none";
			successSpan.style.display = "none";
			archiveLoader.style.visibility = "visible";
			for(var r = 0, n = dataTable.rows().count(); r < n; r++) {
				var id = dataTable.row(r).data()[0];
				patientIds.push(id);
			}
			
			$.confirm({
			    title: 'Confirmation!',
			    theme: 'material',
			    content: 'Are you sure you want to archive these ' + patientIds.length + ' records?',
			    buttons: {
			        confirm: function () {
			        	jQuery.ajax({
			    			type : "POST",
			    			contentType : "application/json",
			    			url : '${pageContext.request.contextPath}/module/archival/archivePatients.form?patients='
			    					+ patientIds,
			    			dataType : "json",
			    			data : {
			    				patients : patientIds
			    			},
			    			async : true,
			    			success : function(data) {
			    				console.log(data);
			    				if(data != null) {
					    			if(data.status != "fail") {
					    				var hrefValue = href = "${pageContext.request.contextPath}/module/archival/downloadReport.form?patientList=" + data.patientIds;
					    				downloadLink.href = hrefValue;
					    				var idArray = [];
					    				var patientsIdString = data.patientIds;
					    				if (patientsIdString.indexOf(',') > -1)
					    					idArray = data.patientIds.split(',');
					    				else 
					    					idArray.push(data.patientIds);
					    				console.log(idArray.length);
					    				var totalCount = idArray.length;
					    				jQuery("#count").text(totalCount.toString()); //javascript was not working
					    				archiveLoader.style.visibility = "hidden";
					    				alertDiv.style.display = "block";
					    				successSpan.style.display = "block";
					    				document.documentElement.scrollTop = 0;
				    				}
					    			else { // handling because ajax does not handle error appropriately
					    				archiveLoader.style.visibility = "hidden";
					    				errorMsg = '<spring:message code="archival.archiveError" />';
					    				jQuery("#errorSpan").text(errorMsg);
					    				alertDiv.style.display = "block";
					    				errorSpan.style.display = "block";
					    				document.documentElement.scrollTop = 0;
					    			}
			    				}
			    				
			    			},
			    			error : function(data) {
			    				console.log("fail  : " + data);
			    				console.log(data);
			    				archiveLoader.style.visibility = "hidden";
			    				errorMsg = '<spring:message code="archival.archiveError" />';
			    				jQuery("#errorSpan").text(errorMsg);
			    				alertDiv.style.display = "block";
			    				errorSpan.style.display = "block";
			    				document.documentElement.scrollTop = 0;
			    			},
			    			done : function(e) {
			    				console.log("DONE");
			    				archiveLoader.style.visibility = "hidden";
			    			}
			    		});
			        	document.documentElement.scrollTop = 0;
			        },
			        cancel: function () {
			        	archiveLoader.style.visibility = "hidden"; // stop the loader
			        }
			    }
			});
	}
</script>

<%@ include file="/WEB-INF/template/footer.jsp"%>
