package com.subtitlor.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.subtitlor.beans.Traduction;

public class SubtitlesHandler {
	private ArrayList<String> originalSubtitles = null;
	private ArrayList<String> translatedSubtitles = null;

	public SubtitlesHandler(String fileName) {
		originalSubtitles = new ArrayList<String>();
		translatedSubtitles = new ArrayList<String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty() && !isNumeric(line) && !isTime(line)) {
					originalSubtitles.add(line);
				}

			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateTraduction(String fileName, List<Traduction> traductions) {
		try {
			BufferedReader br;
			br = new BufferedReader(new FileReader(fileName));
			String line;
			int k = 0;
			while ((line = br.readLine()) != null) {

				if (!line.isEmpty() && !isNumeric(line) && !isTime(line)) {
					if (traductions.get(k).getText().isEmpty())
						translatedSubtitles.add(line);
					else
						translatedSubtitles.add(traductions.get(k).getText());

					k++;
				} else {
					translatedSubtitles.add(line);
				}

			}
			br.close();

			String name = fileName.replace(".srt", ".traduit.srt");
			File file = new File(name);

			// créer le fichier s'il n'existe pas
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			String newLine = System.getProperty("line.separator");

			for (int i = 0; i < translatedSubtitles.size(); i++) {

				bw.write(translatedSubtitles.get(i) + newLine);
			}

			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getSubtitles() {
		return originalSubtitles;
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isTime(String str) {

		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		String[] ligne = str.split(",");
		try {
			sdf.parse(ligne[0]);
			return true;
		} catch (ParseException e) {
			return false;
		}

	}

}
