<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude
	file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude
	file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<link type="text/css" rel="stylesheet"
	href="/openmrs/moduleResources/archival/css/archival.css" />
<link
	href="/openmrs/moduleResources/archival/css/dataTables.bootstrap4.min.css"
	rel="stylesheet" />

<h2><spring:message code="archival.title" /></h2>

<div class="boxHeader${model.patientVariation}"><spring:message code="archival.queryBox" /></div>
<div class="box${model.patientVariation}">
<%-- action="${pageContext.request.contextPath}/module/archival/executeQuery.form" --%>
	<div class="container" id="queryDiv">
		<form class="form-horizontal" method="get" id="execute_query_form">
			<div class="row">
				<div class="col-md-12">
					<textarea class="form-control" name="query" id="query" rows="4" cols="182"></textarea>
					<span id="queryError" class="text-danger "> </span>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<input type="submit" id="analyze"
						value="<spring:message code="archival.querySubmit" />"></input>
				</div>
			</div>
		</form>
	</div>
</div>

<div class="boxHeader${model.patientVariation}" id="queryResultDiv"><spring:message code="archival.queryResults" /></div>
<div class="box${model.patientVariation}" id="patientTableDiv">
</div>

<script src="/openmrs/moduleResources/archival/js/jquery-3.3.1.min.js"></script>
<script
	src="/openmrs/moduleResources/archival/js/jquery.dataTables.min.js"></script>
<script
	src="/openmrs/moduleResources/archival/js/dataTables.bootstrap4.min.js"></script>
	
<script type="text/javascript" th:inline="javascript">

jQuery(document).ready(function() {
	// console.log("working");	
})

jQuery(function() {
	jQuery("[id*='analyze']").click(function(e) {
	      e.preventDefault();
		
	      var sqlQuery = document.getElementById("query").value;
	      jQuery.ajax({
				type : "GET",
				contentType : "application/json",
				url : '${pageContext.request.contextPath}/module/archival/executeQuery.form',
				dataType : "json",
				data: { query: sqlQuery },
				async: false,
				success : function(data) {
				   console.log(data);
				   var patientTableHtml = "";
				   patientTableHtml = patientTableHtml.concat("<table id='searchedPatientList' class='table table-striped table-bordered' style='width: 100%'>");
				   patientTableHtml = patientTableHtml.concat('<thead><tr>');
				   patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.id" /></th>');
					patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.name" /></th>');
					patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.gender" /></th>');
					patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.dateOfBirth" /></th>');
					patientTableHtml = patientTableHtml.concat('<th><spring:message code="archival.enrolledPrograms" /></th>');
					patientTableHtml = patientTableHtml.concat('</tr></thead>');
					jQuery(data.patientList).each(function() {
						patientTableHtml = patientTableHtml.concat('<tbody><tr>'); 
						patientTableHtml = patientTableHtml.concat('<td>'+this.patientId+'</td>');
					 });
					patientTableHtml = patientTableHtml.concat('</tr></tbody>');
	                patientTableHtml = patientTableHtml.concat('</table>');
	                document.getElementById("patientTableDiv").innerHTML = patientTableHtml;
				},
				error : function(data) {
					  console.log("fail  : " + data);
				},
				done : function(e) {
					console.log("DONE");
				}
		});
	});
})
</script>

<%@ include file="/WEB-INF/template/footer.jsp"%>
