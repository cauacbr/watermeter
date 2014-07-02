package utfpr.oficinas.watermeter;

import java.util.ArrayList;

public class FakeManager {

	ArrayList<Medicao> medicoes = new ArrayList<Medicao>();
	
	public FakeManager() {
		
		Medicao aux;
		
		int dia, mes, ano, hora, litro, mililitro;
		
		for(int i=0; i<1000; i++) {
			
			dia = (int)(Math.random() * (30 + 1));
			mes = (int)(Math.random() * (12 + 1));
			ano = 2004 + (int)(Math.random() * (10 + 1));
			hora = (int)(Math.random() * (23 + 1));
			litro = (int)(Math.random() * (20 + 1));
			mililitro = (int)(Math.random() * (999 + 1));
			
			aux = new Medicao(i, dia, mes, ano, hora, litro, mililitro);
			
			medicoes.add(aux);
			
		}
		
		
	}
	
	public ArrayList<Medicao> getMedicoes() {
		return this.medicoes;
	}
	
	public ArrayList<Medicao> getMedicoesByYear(int year) {

		ArrayList<Medicao> medicoesAux = new ArrayList<Medicao>();
		
		for(Medicao medicao : medicoes) 
			if(medicao.getAno() == year) 
				medicoesAux.add(medicao);
		
		return medicoesAux;
		
	}
	
	public ArrayList<Medicao> getMedicoesByMonth(int month, int year) {

		ArrayList<Medicao> medicoesAux = new ArrayList<Medicao>();
		
		for(Medicao medicao : getMedicoesByYear(year)) 
			if(medicao.getMes() == month) 
				medicoesAux.add(medicao);
		
		return medicoesAux;
		
	}
	
	public ArrayList<Medicao> getMedicoesByDay(int day, int month, int year) {

		ArrayList<Medicao> medicoesAux = new ArrayList<Medicao>();
		
		for(Medicao medicao : getMedicoesByMonth(month, year)) 
			if(medicao.getDia() == day) 
				medicoesAux.add(medicao);
		
		return medicoesAux;
		
	}
	
	public Integer getMinYear() {

		Integer min = null;
		
		for(Medicao medicao : medicoes) {
			if(min == null) {
				min = medicao.getAno();
				continue;
			}
			
			if(medicao.getAno() < min)
				min = medicao.getAno();
		}

		return min;
		
	}
	
	public Integer getMaxYear() {

		Integer max = null;
		
		for(Medicao medicao : medicoes) {
			if(max == null) {
				max = medicao.getAno();
				continue;
			}
			
			if(medicao.getAno() > max)
				max = medicao.getAno();
		}
		
		return max;
	}
	
	public Integer getMinMonth(int year) {

		Integer min = null;
		ArrayList<Medicao> medicoesAno = getMedicoesByYear(year);
		
		for(Medicao medicao : medicoesAno) {
			if(min == null) {
				min = medicao.getMes();
				continue;
			}
			
			if(medicao.getMes() < min)
				min = medicao.getMes();
		}

		return min;
		
	}
	
	public Integer getMaxMonth(int year) {

		Integer max = null;
		
		ArrayList<Medicao> medicoesAno = getMedicoesByYear(year);
		
		for(Medicao medicao : medicoesAno) {
			if(max == null) {
				max = medicao.getMes();
				continue;
			}
			
			if(medicao.getMes() > max)
				max = medicao.getMes();
		}
		
		return max;
	}
	
	public Integer getMinDay(int month, int year) {

		Integer min = null;
		ArrayList<Medicao> medicoesMes = getMedicoesByMonth(month, year);
		
		for(Medicao medicao : medicoesMes) {
			if(min == null) {
				min = medicao.getDia();
				continue;
			}
			
			if(medicao.getDia() < min)
				min = medicao.getDia();
		}

		return min;
		
	}
	
	public Integer getMaxDay(int month, int year) {

		Integer max = null;
		
		ArrayList<Medicao> medicoesMes = getMedicoesByMonth(month, year);
		
		for(Medicao medicao : medicoesMes) {
			if(max == null) {
				max = medicao.getDia();
				continue;
			}
			
			if(medicao.getDia() > max)
				max = medicao.getDia();
		}
		
		return max;
	}
	
	public Integer getMinHour(int day, int month, int year) {

		Integer min = null;
		ArrayList<Medicao> medicoesDia = getMedicoesByDay(day, month, year);
		
		for(Medicao medicao : medicoesDia) {
			if(min == null) {
				min = medicao.getHora();
				continue;
			}
			
			if(medicao.getHora() < min)
				min = medicao.getHora();
		}

		return min;
		
	}
	
	public Integer getMaxHour(int day, int month, int year) {

		Integer max = null;
		
		ArrayList<Medicao> medicoesDia = getMedicoesByDay(day, month, year);
		
		for(Medicao medicao : medicoesDia) {
			if(max == null) {
				max = medicao.getHora();
				continue;
			}
			
			if(medicao.getHora() > max)
				max = medicao.getHora();
		}
		
		return max;
	}

}
