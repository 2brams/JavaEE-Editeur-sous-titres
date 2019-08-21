package com.subtitlor.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.subtitlor.beans.Traduction;

public class TraductionDaoImpl implements TraductionDao {

	private DaoFactory daoFactory;

	TraductionDaoImpl(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	public void ajouterOuMettreAJour(Traduction traduction) {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = daoFactory.getConnection();
			preparedStatement = connexion.prepareStatement(
					"INSERT INTO traductions(nomFichier, ligne, text, codeLigne) VALUES(?, ?,?,?) ON DUPLICATE KEY UPDATE   nomFichier = VALUES(nomFichier),"
							+ "        ligne = VALUES(ligne),text = VALUES(text),codeLigne = VALUES(codeLigne);");
			preparedStatement.setString(1, traduction.getNomFichier());
			preparedStatement.setInt(2, traduction.getLigne());
			preparedStatement.setString(3, traduction.getText());
			preparedStatement.setString(4, traduction.getCodeLigne());

			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void supprimer(String nomFichier) {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = daoFactory.getConnection();
			preparedStatement = connexion.prepareStatement("DELETE FROM traductions WHERE nomFichier = ?;");
			preparedStatement.setString(1, nomFichier);

			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<Traduction> lister(String nomDuFichier) {
		List<Traduction> traductions = new ArrayList<Traduction>();
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultat = null;

		try {
			connexion = daoFactory.getConnection();
			preparedStatement = connexion.prepareStatement("SELECT * FROM traductions WHERE nomFichier =?;");
			preparedStatement.setString(1, nomDuFichier);

			resultat = preparedStatement.executeQuery();
			while (resultat.next()) {
				String nomFichier = resultat.getString("nomFichier");
				int ligne = resultat.getInt("ligne");
				String text = resultat.getString("text");
				String codeLigne = resultat.getString("codeLigne");

				Traduction traduction = new Traduction();
				traduction.setNomFichier(nomFichier);
				traduction.setLigne(ligne);
				traduction.setText(text);
				traduction.setCodeLigne(codeLigne);

				traductions.add(traduction);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return traductions;
	}

	@Override
	public boolean isExist(String nom) {
		int numberOfRows = 0;
		Connection connexion = null;
		ResultSet resultat = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = daoFactory.getConnection();
			preparedStatement = connexion.prepareStatement("SELECT COUNT(*) FROM traductions WHERE nomFichier =?;");
			preparedStatement.setString(1, nom);

			resultat = preparedStatement.executeQuery();
			if (resultat.next()) {
				numberOfRows = resultat.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (numberOfRows == 0)
			return false;
		else
			return true;
	}

}
