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
	private List<Rilevamento> soluzioneMigliore;
	private List<Rilevamento> partenza;
	private int costo = 0;
	

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



	public List<Rilevamento> trovaSequenza(int mese) {
		soluzioneMigliore = new ArrayList<Rilevamento>();
		partenza = mDao.getRilevamentiMese(mese);
		List<Rilevamento> parziale = new ArrayList<Rilevamento>();
		
		cerca(parziale, 0);
		return soluzioneMigliore;
	}



	private void cerca(List<Rilevamento> parziale, int livello) {
		if(livello == NUMERO_GIORNI_TOTALI) {
			costo = this.calcolaCosto(parziale);
			if(costo < calcolaCosto(soluzioneMigliore)) {
				soluzioneMigliore = new ArrayList<Rilevamento>(parziale);
			}
		}
			
		for(Rilevamento r : partenza) {
			if(sequenzaAmmissibile(r, parziale)) {
				parziale.add(r);
				cerca(parziale, livello+1);
				parziale.remove(r);
			}
		}
	}



	private boolean sequenzaAmmissibile(Rilevamento r, List<Rilevamento> parziale) {
		Citta c = cercaCitta(r.getLocalita());
		
		if(parziale.size() == 0) {
			return true;
		}
		if(c.getCounter() == 6) {
			return false;
		}
		else {
			if(c.getCounter() < 3 && !r.getLocalita().equals(parziale.get(parziale.size()-1).getLocalita())) {
				return false;
			}
			c.increaseCounter();
			return true;
		}
	}
	
	private Citta cercaCitta(String nome) {
		for(Citta c : mDao.getCitta()) {
			if(c.getNome().equals(nome)) {
				return c;
			}
		}
		return null;
	}



	private int calcolaCosto(List<Rilevamento> sequenza) {
		int c = 0;
		int indice;
		for(Rilevamento r : sequenza) {
			indice = 0;
			if(indice > 0) {
				if(!r.equals(sequenza.get(indice-1))) {
					c += 100;
				}
			}
			c += r.getUmidita();
			indice++;
		}
		return c;
	}
	
	
	
}
