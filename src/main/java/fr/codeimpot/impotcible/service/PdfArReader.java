package fr.codeimpot.impotcible.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;

public class PdfArReader {

	@Value("${ar.beginString}")
	private String beginString;

	@Value("${ar.endString}")
	private String endString;

	@Value("${ar.keyPattern}")
	private String arKeyPattern;

	@Value("${ar.valuePattern}")
	private String arValuePattern;

	public Map<String, String> convertARToKeyMap(File fic) throws IOException {
		Map<String, String> resMap = new HashMap<>();
		if(!fic.isFile()){
			return resMap;
		}
		PDDocument pddDocument = PDDocument.load(fic);
		PDFTextStripper stripper = new PDFTextStripper();
		String contenu = stripper.getText(pddDocument);
		String toAnalyze = "";

		if(contenu == null){
			return resMap;
		}
		
		Pattern anneNaissance = Pattern.compile("née le ../../(\\d\\d\\d\\d)");
		Matcher anneeNaissanceMatch = anneNaissance.matcher(contenu);
		if(anneeNaissanceMatch.find()){
			System.out.println("Annee de naissance "+anneeNaissanceMatch.group(1));
			resMap.put("V_0DA", anneeNaissanceMatch.group(1));
		}
		
		Pattern sitFam  = Pattern.compile("Situation de famille : (.)*");
		
		Matcher matchSitFam = sitFam.matcher(contenu);
		if(matchSitFam.find()){
			System.out.println("J ai trouvé "+matchSitFam.group());
			if(matchSitFam.group().contains("Divor")){
				resMap.put("0AD", "1");
			}
		}
		
		
		Pattern persCharge  = Pattern.compile("Personnes à charge : (\\d)");
		
		Matcher matchpersCharge = persCharge.matcher(contenu);
		if(matchpersCharge.find()){
			System.out.println("J ai trouvé charge : "+matchpersCharge.group(1));
				resMap.put("0CF", matchpersCharge.group(1));
		}
		
		
		if ( contenu.indexOf(beginString) == 0 || contenu.indexOf(endString)  == 0) {
			return resMap;
		}
		toAnalyze = contenu.substring(contenu.indexOf(beginString) + beginString.length() + 1,
				contenu.indexOf(endString));
		String[] codeRev = toAnalyze.split("\n");
		Pattern code = Pattern.compile(arKeyPattern);
		Pattern value = Pattern.compile(arValuePattern);
		
		
		for (String ligne : codeRev) {
			String cle = "";
			String valeur = "";
			Matcher matchCode = code.matcher(ligne);
			Matcher matchValue = value.matcher(ligne);

			if (matchCode.find()) {
				cle = matchCode.group().replaceAll(" ", "");
			}

			if (matchValue.find()) {
				valeur = matchValue.group();
			}
			if ((!"".equals(cle)) && (!"".equals(value))) {
				resMap.put(cle, valeur);
			}
		}
		pddDocument.close();
		return resMap;
	}

	public static void main(String[] args) {
		try {
			File fic = new File("ar.pdf");
			PdfArReader reader = new PdfArReader();
			reader.beginString = "Code Revenu Libell� Valeur";
			reader.endString = "DISCORDANCES � V�RIFIER";
			reader.arKeyPattern = "\\d\\s(\\w)*\\b";
			reader.arValuePattern = "(\\d)*$";
			
		
			Map<String, String> res = reader.convertARToKeyMap(fic);
			for (String key : res.keySet()) {
				System.out.println("Cl� " + key + " valeur " + res.get(key));
			}

			// PDDocument pddDocument=PDDocument.load(fic);
			// PDFTextStripper stripper = new PDFTextStripper();
			// String contenu = stripper.getText(pddDocument);
			// System.out.println(contenu);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}