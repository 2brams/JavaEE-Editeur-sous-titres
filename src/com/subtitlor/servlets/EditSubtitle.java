package com.subtitlor.servlets;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.subtitlor.beans.Traduction;
import com.subtitlor.dao.DaoFactory;
import com.subtitlor.dao.TraductionDao;
import com.subtitlor.utilities.SubtitlesHandler;

@WebServlet("/EditSubtitle")
public class EditSubtitle extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SAVE_DIR = "uploadFiles";
	private static final String VUE = "/WEB-INF/edit_subtitle.jsp";
	private static final String FICHIER_TRANSMIS = "nomFichier";
	private static final String CHAMP_SOUMISSION_ENREGISTREMENT = "save";
	private static final String CHAMP_GENERER = "generate";
//	private static final String FILE_NAME = "/WEB-INF/password_presentation.srt";

	private TraductionDao traductionDao;

	private SubtitlesHandler subtitles;
	private String CHEMIN;
	private String NOM_FICHIER;

	public void init() throws ServletException {
		DaoFactory daoFactory = DaoFactory.getInstance();
		this.traductionDao = daoFactory.getTraductionDao();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Seule la methode doPost est utilisé, on fait une redirection vers la page
		// d'accueil car on n'a besoin du nom du fichier dans cette VUE

		response.sendRedirect("/Subtitlor/");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String appPath = request.getServletContext().getRealPath("");
		CHEMIN = appPath + SAVE_DIR;

		if (request.getParameter(FICHIER_TRANSMIS) != null) {
			NOM_FICHIER = request.getParameter(FICHIER_TRANSMIS);

			subtitles = new SubtitlesHandler(CHEMIN + File.separator + NOM_FICHIER);

			request.setAttribute("traductions", traductionDao.lister(NOM_FICHIER));
			request.setAttribute("subtitles", subtitles.getSubtitles());

			this.getServletContext().getRequestDispatcher(VUE).forward(request, response);

		} else if (request.getParameter(CHAMP_SOUMISSION_ENREGISTREMENT) != null) {

			List<Traduction> traductions = traductionDao.lister(NOM_FICHIER);
			for (int i = 0; i < traductions.size(); i++) {

				if (!traductions.get(i).getText().equalsIgnoreCase(request.getParameter("line" + i))) {
					Traduction traduction = new Traduction();
					traduction.setNomFichier(NOM_FICHIER);
					traduction.setCodeLigne(NOM_FICHIER + "_" + i);
					traduction.setLigne(i);
					traduction.setText(request.getParameter("line" + i));
					traductionDao.ajouterOuMettreAJour(traduction);
				}
			}

			response.setHeader("Content-Type", "text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print(NOM_FICHIER);

		} else if (request.getParameter(CHAMP_GENERER) != null) {

			subtitles.generateTraduction(CHEMIN + File.separator + NOM_FICHIER, traductionDao.lister(NOM_FICHIER));

			String name = NOM_FICHIER.replace(".srt", ".traduit.srt");
			String filePath = CHEMIN + File.separator + name;
			File downloadFile = new File(filePath);
			FileInputStream inStream = new FileInputStream(downloadFile);

			// obtains ServletContext
			ServletContext context = getServletContext();

			// gets MIME type of the file
			String mimeType = context.getMimeType(filePath);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}
			System.out.println("MIME type: " + mimeType);

			// modifies response
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			// forces download
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
			response.setHeader(headerKey, headerValue);

			// obtains response's output stream
			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[4096];
			int bytesRead = -1;

			while ((bytesRead = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inStream.close();
			outStream.close();
		}

	}

}
