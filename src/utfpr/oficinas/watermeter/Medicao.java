package utfpr.oficinas.watermeter;

public class Medicao {

	// private variables
	int _id;
	int dia;
	int mes;
	int ano;
	int hora;
	int litro;
	int ml;
	public Medicao(){		
	}
	public Medicao(int _id, int dia, int mes, int ano, int hora, int litro, int ml) {
		this._id = _id;
		this.dia = dia;
		this.mes = mes;
		this.ano = ano;
		this.hora = hora;
		this.litro = litro;
		this.ml = ml;
	}	
	public Medicao(int dia, int mes, int ano, int hora, int litro, int ml) {
		this.dia = dia;
		this.mes = mes;
		this.ano = ano;
		this.hora = hora;
		this.litro = litro;
		this.ml = ml;
	}	
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public int getDia() {
		return dia;
	}
	public void setDia(int dia) {
		this.dia = dia;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	public int getHora() {
		return hora;
	}
	public void setHora(int hora) {
		this.hora = hora;
	}
	public int getLitro() {
		return litro;
	}
	public void setLitro(int litro) {
		this.litro = litro;
	}
	public int getMl() {
		return ml;
	}
	public void setMl(int ml) {
		this.ml = ml;
	}


}
