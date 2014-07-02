package utfpr.oficinas.watermeter;

public class Medicao {

	private int id;
	private int dia;
	private int mes;
	private int ano;
	private int hora;
	private int litro;
	private int miliLitro;
	
	public Medicao(int id, int dia, int mes, int ano, int hora, int litro, int miliLitro) {
		super();
		this.id = id;
		this.dia = dia;
		this.mes = mes;
		this.ano = ano;
		this.hora = hora;
		this.litro = litro;
		this.miliLitro = miliLitro;
	}
	
	public int getId() {
		return id;
	}
	public int getDia() {
		return dia;
	}
	public int getMes() {
		return mes;
	}
	public int getAno() {
		return ano;
	}
	public int getHora() {
		return hora;
	}
	public int getLitro() {
		return litro;
	}
	public int getMiliLitro() {
		return miliLitro;
	}
	
	
}
