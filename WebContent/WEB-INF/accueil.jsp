<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="css.jsp"%>
<title>Accueil</title>

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
				<strong>Étape 1:</strong> Importer <br> <strong>Étape
					2:</strong> Éditer <br> <strong>Étape 3:</strong> Enregistrer <br>
				<strong>Étape 4:</strong> Télécharger <br>
			</p>
			<button type="button" class="close" data-dismiss="alert">&times;</button>
		</div>
		<div class="alert alert-warning alert-dismissible fade show">
			<h4 class="alert-heading">
				<i class="fa fa-warning"></i> Avertissement
			</h4>
			<hr>
			<p class="mb-0">
				Seuls les fichiers <strong>srt</strong> sont acceptés <br> Il
				n'est pas possible d'ajouter deux fois le même fichier <br> La
				suppression d'un fichier entraine automatiquement la suppression de
				toutes ses références dans la base de données<br>
			</p>
			<button type="button" class="close" data-dismiss="alert">&times;</button>
		</div>
		<div class="card mb-4">
			<div class="card-header bg-info">Importer vos fichiers</div>
			<div class="card-body">
				<form class="" method="post" action="" enctype="multipart/form-data">
					<input type="text" hidden="true" name="fichierSubmit"
						value="FICHIER_SAVE" />
					<div class="input-group mb-3 ">
						<div class="custom-file col-sm-12">
							<input type="file" name="fichier"
								class="custom-file-input text-left" id="inputGroupFile02" /> <label
								class="custom-file-label" for="inputGroupFile02">Choisir
								un fichier </label>
						</div>
						<button type="submit" class="btn btn-primary">Importer</button>
					</div>
				</form>
			</div>
		</div>
		<div class="card ">
			<div class="card-header bg-info">Liste des fichiers à traduire</div>
			<div class="card-body">

				<table class="table table-striped table-bordered table-hover ">
					<thead>
						<tr>
							<th class="col-sm-1">#</th>
							<th class="col-sm-7">Chemin du fichier sur le Server Tomcat</th>
							<th class="col-sm-4">Actions</th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${empty fichiers }">
							<tr>
								<td class="text-center" colspan="3">Aucun fichier dans la
									liste, importez d'abord un fichier</td>

							</tr>
						</c:if>

						<c:forEach items="${ fichiers }" var="fichier" varStatus="status">
							<tr>
								<td class="col-sm-1"><c:out value="${ status.count }" /></td>
								<td class="col-sm-7"><c:out
										value="${ emplacement }/${ fichier.nom }" /></td>
								<td class="col-sm-4 text-right">
									<button type="button" onclick="send('${ fichier.nom }','edit')"
										class="btn btn-info">Editer</button>
									<button type="button"
										onclick="send('${ fichier.nom }','delete')"
										class="btn btn-danger">Supprimer</button>
								</td>
							</tr>
						</c:forEach>

					</tbody>
				</table>

			</div>
		</div>
	</div>

	<div class="modal">
		<h4 class="text-center  mt-5">
			Préparation de la base de données en cours. <br> Patientez
			SVP!!!
		</h4>
	</div>

	<%@ include file="js.jsp"%>

	<script type="text/javascript">
		function send(nomFichier, action) {
			$body = $("body");
			//$(".dem").click(function(e) {
			//var name = $('#demarrer').val();
			$.ajax({
				url : '',
				type : 'POST',
				beforeSend : function() {
					$body.addClass("loading");
				},
				complete : function() {
					$body.removeClass("loading");
				},
				data : {
					nomFichier : nomFichier,
					action : action
				},
				success : function(resultText) {
					console.log(resultText);
					if (resultText == 'ajout') {
						$.redirect('/Subtitlor/edit', {
							'nomFichier' : nomFichier
						});
					} else if (resultText == nomFichier) {
						$.redirect('/Subtitlor/', {
							'apresSupression' : nomFichier
						});
					}

				},
				error : function(jqXHR, exception) {
					console.log('Error occured!!');
				}
			});
		};
		$(document).ready(function() {

			$('input[type="file"]').change(function(e) {
				var fileName = e.target.files[0].name;
				$('.custom-file-label').html(fileName);
			});

		});

		if ('${notifyShow }' == 1) {
			$.notify({
				title : '<strong>${title }!</strong><br>',
				message : '${message }'
			}, {
				// settings
				element : 'body',
				type : '${ type }',
				showProgressbar : true,
				delay : '${delay }',
				timer : 1000,
				mouse_over : null,
				animate : {
					enter : 'animated fadeInDown',
					exit : 'animated fadeOutUp'
				}
			});
		}
	</script>
</body>
</html>