package com.subtitlor.dao;

import java.util.List;

import com.subtitlor.beans.Traduction;

public interface TraductionDao {

	void ajouterOuMettreAJour(Traduction traduction);

	List<Traduction> lister(String nomduFichier);

	boolean isExist(String nom);

	public void supprimer(String nomFichier);
}
