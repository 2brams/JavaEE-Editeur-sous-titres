package com.subtitlor.beans;

public class Traduction {
	
	private String nomFichier;
	private String codeLigne;
	private int ligne;
	private String text;
	

	
	public String getNomFichier() {
		return nomFichier;
	}
	public void setNomFichier(String nomFichier) {
		this.nomFichier = nomFichier;
	}
	
	public String getCodeLigne() {
		return codeLigne;
	}
	public void setCodeLigne(String codeLigne) {
		this.codeLigne = codeLigne;
	}
	public int getLigne() {
		return ligne;
	}
	public void setLigne(int ligne) {
		this.ligne = ligne;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
	
}
