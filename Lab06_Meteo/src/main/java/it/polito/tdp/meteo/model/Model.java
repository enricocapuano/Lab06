package it.polito.tdp.meteo.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
	List<List<Rilevamento>> rilevamenti;
	List<Citta> sequenza;
	Map<Citta, List<Rilevamento>> mappa;
	int cambi = 0;
	

	public Model() {
		mDao = new MeteoDAO();
		mappa = new HashMap<Citta, List<Rilevamento>>();
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
	public List<Citta> trovaSequenza(int mese) {
		sequenza = new LinkedList<Citta>();
		LinkedList<Citta> parzialeC = new LinkedList<Citta>();
		Map<List<Citta>, Integer> parziale = new HashMap<List<Citta>, Integer>();
		cerca(parziale, 0, mese, parzialeC);
		return sequenza;
	}
	
	private void cerca(Map<List<Citta>, Integer> mappaParziale, int livello, int mese, LinkedList<Citta> cittaParziale) {
		
		if(cittaParziale.size() == NUMERO_GIORNI_TOTALI) {
			//caso terminale
			return;
		}
		else {
			for(Citta c : mDao.getCitta()) {	
				if(cittaParziale.size() < 15) {
					cittaParziale.add(c);
					cerca(mappaParziale, livello+1, mese, cittaParziale);
					cittaParziale.remove(c);
				}	
			}
		}
	}

	public Map<Citta, List<Rilevamento>> rilevamentiCitta(int mese) {
		
		for(Citta c : mDao.getCitta()) {
			mappa.put(c, mDao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		
		return mappa;
	}
	
}
