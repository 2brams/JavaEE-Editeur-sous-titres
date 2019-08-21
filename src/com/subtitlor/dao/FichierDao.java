package com.subtitlor.dao;

import java.util.List;

import com.subtitlor.beans.Fichier;

public interface FichierDao {

	void ajouter(Fichier fichier);

	List<Fichier> lister();

	public boolean isExist(String nom);

	public void supprimer(String nomFichier, String chemin);

}
