package it.polito.tdp.meteo.model;

import java.text.DateFormat;
import java.util.ArrayList;
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
	List<List<Rilevamento>> rilevamenti;
	List<Citta> sequenza;
	Map<Citta, List<Rilevamento>> mappa;
	int ricorsioni = 0;
	int livRic = 0;
	int costo = 0;

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
		sequenza = new ArrayList<Citta>();
		List<Citta> parziale = new ArrayList<Citta>();
		cerca(parziale, 0, mese);
		return sequenza;
	}
	
	private void cerca(List<Citta> sequenzaParziale, int livello, int mese) {
		ricorsioni ++;
	
		if(sequenza.size() == NUMERO_GIORNI_TOTALI) {
			//caso terminale
			return;
		}
		else {
			int sommaUmidita = 0;
			int umidita = 0;
			for(livRic = livello; livRic < ricorsioni*NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN; livRic++) {
				for(Citta c : rilevamentiCitta(mese).keySet()) {
					List<Rilevamento> r = rilevamentiCitta(mese).get(c);
					if(umidita == 0) {
						umidita = r.get(livello).getUmidita();
						c.setSomma(umidita);
					}
					else {
						if(umidita > r.get(livello).getUmidita()) {
							umidita = r.get(livello).getUmidita();
							c.setSomma(umidita);
						}
					}
				}
			}
			
				
			if(livello == ricorsioni*NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
				for(Citta i : mDao.getCitta()) {
					if(sommaUmidita == 0) {
						sommaUmidita = i.getSomma();
						sequenzaParziale.add(i);
						sequenzaParziale.add(i);
						sequenzaParziale.add(i);
					}
					else {
						if(sommaUmidita > i.getSomma()) {
							sommaUmidita = i.getSomma();
							sequenzaParziale.set(livello-3, i);
							sequenzaParziale.set(livello-2, i);
							sequenzaParziale.set(livello-1, i);
						}
					}
				}
				sommaUmidita = 0;
				livello++;
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
