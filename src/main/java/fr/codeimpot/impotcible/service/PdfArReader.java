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

		if (contenu == null || contenu.indexOf(beginString) == 0 || contenu.indexOf(endString)  == 0) {
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


}
