package com.subtitlor.dao;

import java.io.File;

import java.sql.*;
import java.util.ArrayList;

import java.util.List;

import com.subtitlor.beans.Fichier;

public class FichierDaoImpl implements FichierDao {

	private DaoFactory daoFactory;

	FichierDaoImpl(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	public void ajouter(Fichier fichier) {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = daoFactory.getConnection();
			preparedStatement = connexion.prepareStatement(
					"INSERT INTO fichiers(nom) VALUES(?) ON DUPLICATE KEY UPDATE   nom = VALUES(nom);");
			preparedStatement.setString(1, fichier.getNom());

			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void supprimer(String nomFichier, String chemin) {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = daoFactory.getConnection();
			preparedStatement = connexion.prepareStatement("DELETE FROM fichiers WHERE nom = ?;");
			preparedStatement.setString(1, nomFichier);

			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		supprimerFichier(nomFichier, chemin);
		String fichiertraduit = nomFichier.replace(".srt", ".traduit.srt");
		supprimerFichier(fichiertraduit, chemin);

	}

	@Override
	public List<Fichier> lister() {
		List<Fichier> fichiers = new ArrayList<Fichier>();
		Connection connexion = null;
		Statement statement = null;
		ResultSet resultat = null;

		try {
			connexion = daoFactory.getConnection();
			statement = connexion.createStatement();
			resultat = statement.executeQuery("SELECT * FROM fichiers;");

			while (resultat.next()) {
				String nom = resultat.getString("nom");

				Fichier fichier = new Fichier();
				fichier.setNom(nom);

				fichiers.add(fichier);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fichiers;
	}

	@Override
	public boolean isExist(String nom) {
		Fichier fichier = new Fichier();
		Connection connexion = null;
		ResultSet resultat = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = daoFactory.getConnection();
			preparedStatement = connexion.prepareStatement("SELECT * FROM fichiers WHERE nom =?;");
			preparedStatement.setString(1, nom);

			resultat = preparedStatement.executeQuery();
			while (resultat.next()) {
				String nomF = resultat.getString("nom");
				fichier.setNom(nomF);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (fichier.getNom() != null)
			return true;
		else
			return false;
	}

	public void supprimerFichier(String nomFichier, String chemin) {

		try {

			File file = new File(chemin + File.separator + nomFichier);

			if (file.exists()) {
				file.delete();
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
}
