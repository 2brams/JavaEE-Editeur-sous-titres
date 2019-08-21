<!DOCTYPE html>
<html>
<head>
<%@ include file="css.jsp"%>
<title>Editer les sous-titres</title>
</head>
<body>
	<%@ include file="menu.jsp"%>

	<div class="container">
		<div class="alert alert-info alert-dismissible fade show">
			<h4 class="alert-heading">
				<i class="fa fa-info"></i> Info
			</h4>
			<hr>
			<p class="mb-0">
				Pour retourner à la liste des fichiers cliquer sur <strong>Accueil</strong>
				ou sur le <strong>logo</strong>
			</p>
			<button type="button" class="close" data-dismiss="alert">&times;</button>
		</div>
		<div class="card ">
			<div class="card-header bg-info">Lignes du fichier</div>

			<div class="card-body">
				<form method="post" action="edit">
					<input type="text" name="generate" hidden="true" value="generation" />
					<button type="submit" class="btn btn-success mb-2 float-ri">Exporter</button>
				</form>
				<form method="post" action="edit" id="idForm">

					<input type="text" name="save" hidden="true"
						value="soumission du formulaire" />
					<button type="submit" class="btn btn-info ml-2 mb-2 float-right">Enregistrer</button>

					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th class="col-sm-6">Texte original</th>
								<th class="col-sm-6">Traduction</th>
							</tr>
						</thead>
						<tbody>

							<c:forEach items="${ subtitles }" var="line" varStatus="status">
								<tr>
									<td class="text-left"><c:out value="${ line }" /></td>
									<td><input type="text" name="line${ status.index }"
										value="${ traductions[status.index].text }" size="35" /></td>
								</tr>
							</c:forEach>

						</tbody>
					</table>
				</form>
			</div>
		</div>
	</div>



	<div class="modal">
		<h4 class="text-center  mt-5">
			Enregistrement en cours. <br> Patientez SVP!!!
		</h4>
	</div>
	<%@ include file="js.jsp"%>

	<script type="text/javascript">
		$(document).ready(function() {

			$body = $("body");
			var notifyShow = 0;
			$("#idForm").submit(function(event) {
				event.preventDefault();

				$.ajax({
					url : '',
					type : 'POST',
					beforeSend : function() {
						$body.addClass("loading");
					},
					complete : function() {
						$body.removeClass("loading");
					},
					data : $("#idForm").serialize(),

					success : function(data) {
						console.log(data);
						$.notify({
							title : '<strong>Succcès</strong><br>',
							message : 'Enregistrement réussi'
						}, {
							// settings
							element : 'body',
							type : 'success',
							showProgressbar : true,
							delay : 2000,
							timer : 1000,
							mouse_over : null,
							animate : {
								enter : 'animated fadeInDown',
								exit : 'animated fadeOutUp'
							}
						});
						$.post('', {
							nomFichier : data
						}, function(data) {
							//alert(data);
						});
					},
					error : function(jqXHR, exception) {
						console.log('Error occured!!');
					}
				});
			});
		});
	</script>

</body>
</html>