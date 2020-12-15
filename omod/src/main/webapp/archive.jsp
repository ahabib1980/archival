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
	
<script
	src="${pageContext.request.contextPath}/moduleResources/archival/js/jquery.dataTables.min.js"></script>
<script
	src="${pageContext.request.contextPath}/moduleResources/archival/js/dataTables.bootstrap4.min.js"></script>

<h2><spring:message code="archival.title" /></h2>

<div class="boxHeader${model.patientVariation}"><spring:message code="archival.queryBox" /></div>
<div class="box${model.patientVariation}">
	<div class="container" id="queryDiv">
		<form class="form-horizontal" method="get"
						action="${pageContext.request.contextPath}/module/archival/executeQuery.form"
						onsubmit="return retireValidate()">
			<div class="row">
				<div class="col-md-12">
					<textarea class="form-control" name="query" id="query" rows="4" cols="182"></textarea>
					<span id="queryError" class="text-danger "> </span>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<input type="submit"
						value="<spring:message code="archival.querySubmit" />"></input>
				</div>
			</div>
		</form>
	</div>
</div>

<div class="boxHeader${model.patientVariation}" id="queryResultDiv"><spring:message code="archival.queryResults" /></div>
<div class="box${model.patientVariation}">
	<table id="searchedPatientList"
			class="table table-striped table-bordered" style="width: 100%">
			<thead>
				<tr>
					<th><spring:message code="archival.id" /></th>
					<th><spring:message code="archival.name" /></th>
					<th><spring:message code="archival.gender" /></th>
					<th><spring:message code="archival.dateOfBirth" /></th>
					<th><spring:message code="archival.enrolledPrograms" /></th>
				</tr>
			</thead>
			<tbody>
			</tbody>
	</table>
</div>


<%@ include file="/WEB-INF/template/footer.jsp"%>
