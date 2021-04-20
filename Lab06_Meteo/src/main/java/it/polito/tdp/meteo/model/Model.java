package it.polito.tdp.meteo.model;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	MeteoDAO mDao;

	public Model() {
		mDao = new MeteoDAO();
	}
	
	
	
	// of course you can change the String output with what you think works best
	public Map<String, Integer> getUmiditaMedia(int mese) {
		Map<String, Integer> umidita = new HashMap<String, Integer>();
		
		for(Citta c : mDao.getCitta()) {
			int um = 0;
			int count = 0;
			for(Rilevamento r : c.getRilevamenti()) {
				if(r.getData().getMonthValue() == mese) {
					count++;
					um += r.getUmidita();
				}
			}
			um = um/(count);
			umidita.put(c.getNome(), um);
		}
		return umidita;
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		return "TODO!";
	}
	

}
