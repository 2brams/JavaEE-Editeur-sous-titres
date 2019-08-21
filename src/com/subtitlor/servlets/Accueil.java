package com.subtitlor.servlets;

import java.io.File;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.subtitlor.beans.Fichier;
import com.subtitlor.beans.Traduction;
import com.subtitlor.dao.DaoFactory;
import com.subtitlor.dao.FichierDao;
import com.subtitlor.dao.TraductionDao;
import com.subtitlor.forms.UploadForm;
import com.subtitlor.utilities.SubtitlesHandler;

/**
 * Servlet implementation class Accueil
 */
@WebServlet("/Accueil")

public class Accueil extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SAVE_DIR = "uploadFiles";
	private static final String VUE = "/WEB-INF/accueil.jsp";
	private static final String CHAMP_SOUMISSION_FICHIER = "fichierSubmit";
	private static final String NOM_FICHIER_A_EDITER = "nomFichier";

	private String CHEMIN;

	private FichierDao fichierdao;
	private TraductionDao traductiondao;

	public void init() throws ServletException {
		DaoFactory daoFactory = DaoFactory.getInstance();
		this.fichierdao = daoFactory.getFichierDao();
		this.traductiondao = daoFactory.getTraductionDao();
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Accueil() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* Dans la methode Get */
		// On récupère le chemin dans le context de l'application
		String appPath = request.getServletContext().getRealPath("");
		// On definie le chemin du dossier des fichiers uploader
		CHEMIN = appPath + SAVE_DIR;

		// Si ce chemin n'existe pas on le crée
		File fileSaveDir = new File(CHEMIN);
		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdir();
		}

		// On désactive les notifications
		request.setAttribute("notifyShow", 0);
		// On envoie la liste des fichiers sur la page d'accueil
		request.setAttribute("fichiers", fichierdao.lister());
		// On envoie l'emplacement des fichiers sur le server
		request.setAttribute("emplacement", CHEMIN);
		this.getServletContext().getRequestDispatcher(VUE).forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* Dans la methode POST */

		// On vérifie que l'utilisateur a soumis le formulaire de fichier
		if (request.getParameter(CHAMP_SOUMISSION_FICHIER) != null) {

			// Si le formulaire de fichier est soumit, on active les notifications
			request.setAttribute("notifyShow", 1);

			/*
			 * On initialise la class qui contient toute les methodes de traitement des
			 * fichiers uploader
			 */
			UploadForm form = new UploadForm();

			/*
			 * On envoie la request et le chemin où on souhaite enregistrer du ficher Et on
			 * récupère le fichier après toutes les opérations de contrôle
			 * 
			 */
			Fichier fichier = form.enregistrerFichier(request, CHEMIN);

			// Si on a réçu aucune erreur
			if (form.getErreurs().isEmpty()) {
				// On vérifie que le fichier n'est pas déja présent dans la base de données
				if (fichierdao.isExist(fichier.getNom())) {
					// Si le fichier existe on envoie un message
					notification(request, "Attention!!!", "Le fichier " + fichier.getNom() + " existe deja", "warning",
							5000);
				} else {
					// Si le fichier n'existe pas, on l'ajoute, et on envoie un message
					fichierdao.ajouter(fichier);
					notification(request, "Succès", "Enregistrement réussi du fichier " + fichier.getNom(), "success",
							5000);
				}
				// Si au contraire on à réçu une erreur après les traitements sur le fichier
			} else {
				// On récupère les erreurs, et on les transferts à l'utilisateurs
				Map<String, String> errors = form.getErreurs();
				notification(request, "Echec", errors.get("fichier"), "danger", 5000);

			}
			/*
			 * On transfert à nouveau la liste au cas où on a fait un nouvelle
			 * enregistrement
			 */
			request.setAttribute("fichiers", fichierdao.lister());
			request.setAttribute("emplacement", CHEMIN);
			this.getServletContext().getRequestDispatcher(VUE).forward(request, response);

			// Si le formulaire de fichier n'a pas été soumis,
			/*
			 * On vérifie que l'utilisateur a soumis le nom d'un fichier à éditer et si la
			 * traduction de ce fichier n'est pas deja en cours. Si elle n'est pas deja en
			 * cours, on enregistre les lignes à traduire du fichier dans la base de
			 * données, puis on fait une redirection vers la page d'édition. Si elle est
			 * deja en cours, on redirectionne directement
			 * 
			 * Cette soumission est faite en AJAX, et la redirection aussi
			 * 
			 */
		} else if (request.getParameter("action") != null) {

			if (request.getParameter("action").equals("edit")) {

				if (!traductiondao.isExist(request.getParameter(NOM_FICHIER_A_EDITER))) {
					SubtitlesHandler subtitles = new SubtitlesHandler(
							CHEMIN + File.separator + request.getParameter(NOM_FICHIER_A_EDITER));

					for (int i = 0; i < subtitles.getSubtitles().size(); i++) {
						Traduction traduction = new Traduction();
						traduction.setNomFichier(request.getParameter(NOM_FICHIER_A_EDITER));
						traduction.setCodeLigne(request.getParameter(NOM_FICHIER_A_EDITER) + "_" + i);
						traduction.setLigne(i);
						traduction.setText("");
						traductiondao.ajouterOuMettreAJour(traduction);
					}
				}

				response.getWriter().print("ajout");

			} else if (request.getParameter("action").equals("delete")) {

				if (traductiondao.isExist(request.getParameter(NOM_FICHIER_A_EDITER)))
					traductiondao.supprimer(request.getParameter(NOM_FICHIER_A_EDITER));

				fichierdao.supprimer(request.getParameter(NOM_FICHIER_A_EDITER), CHEMIN);

				response.getWriter().print(request.getParameter(NOM_FICHIER_A_EDITER));
			}
			response.setHeader("Content-Type", "text/plain");
			response.setCharacterEncoding("UTF-8");

		} else if (request.getParameter("apresSupression") != null) {
			request.setAttribute("notifyShow", 1);

			request.setAttribute("fichiers", fichierdao.lister());
			request.setAttribute("emplacement", CHEMIN);
			notification(request, "Succès",
					"Supression réussite du fichier <strong>" + request.getParameter("apresSupression") + "</strong>",
					"success", 5000);
			this.getServletContext().getRequestDispatcher(VUE).forward(request, response);

		}

	}

	protected void notification(HttpServletRequest request, String title, String message, String type, int delay) {
		request.setAttribute("title", title);
		request.setAttribute("message", message);
		request.setAttribute("type", type);
		request.setAttribute("delay", delay);
	}

}
