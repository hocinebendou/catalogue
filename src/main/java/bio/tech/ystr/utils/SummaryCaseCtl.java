package bio.tech.ystr.utils;

public class SummaryCaseCtl {
	
	private int countCases;
	private int countCasesMale;
	private int countCasesFemale;
	private double casesVolume;
	private int casesWithVolume;
	
	private int countCtls;
	private int countCtlsMale;
	private int countCtlsFemale;
	private double ctlsVolume;
	private int ctlsWithVolume;
	private String biobankName;
	
	public SummaryCaseCtl() {
		countCases = 0;
		countCasesMale = 0;
		countCasesFemale = 0;
		casesVolume = 0;
		casesWithVolume = 0;
		countCtls = 0;
		countCtlsMale = 0;
		countCtlsFemale = 0;
		biobankName = "";
	}
	
	public SummaryCaseCtl(int countCases, int countCasesMale, int countCasesFemale, double casesVolume, int casesWithVolume,
						  int countCtls, int countCtlsMale, int countCtlsFemale, double ctlsVolume, int ctlsWithVolume) {
		this.countCases = countCases;
		this.countCasesMale = countCasesMale;
		this.countCasesFemale = countCasesFemale;
		this.casesVolume = casesVolume;
		this.casesWithVolume = casesWithVolume;
		this.countCtls = countCtls;
		this.countCtlsMale = countCtlsMale;
		this.countCtlsFemale = countCtlsFemale;
		this.ctlsVolume = ctlsVolume;
		this.ctlsWithVolume = ctlsWithVolume;
	}

	public int getCountCases() {
		return countCases;
	}

	public int getCountCasesMale() {
		return countCasesMale;
	}

	public int getCountCasesFemale() {
		return countCasesFemale;
	}
	
	public double getCasesVolume() {
		return casesVolume;
	}

	public int getCasesWithVolume() {
		return casesWithVolume;
	}
	
	public int getCountCtls() {
		return countCtls;
	}

	public int getCountCtlsMale() {
		return countCtlsMale;
	}

	public int getCountCtlsFemale() {
		return countCtlsFemale;
	}

	public double getCtlsVolume() {
		return ctlsVolume;
	}

	public int getCtlsWithVolume() {
		return ctlsWithVolume;
	}
	
	public String getBiobankName() {
		return biobankName;
	}

	// setters
	public void setCountCases(int countCases) {
		this.countCases = countCases;
	}

	public void setCountCasesMale(int countCasesMale) {
		this.countCasesMale = countCasesMale;
	}

	public void setCountCasesFemale(int countCasesFemale) {
		this.countCasesFemale = countCasesFemale;
	}

	public void setCasesVolume(double casesVolume) {
		this.casesVolume = casesVolume;
	}

	public void setCasesWithVolume(int casesWithVolume) {
		this.casesWithVolume = casesWithVolume;
	}

	public void setCountCtls(int countCtls) {
		this.countCtls = countCtls;
	}

	public void setCountCtlsMale(int countCtlsMale) {
		this.countCtlsMale = countCtlsMale;
	}

	public void setCountCtlsFemale(int countCtlsFemale) {
		this.countCtlsFemale = countCtlsFemale;
	}

	public void setCtlsVolume(double ctlsVolume) {
		this.ctlsVolume = ctlsVolume;
	}

	public void setCtlsWithVolume(int ctlsWithVolume) {
		this.ctlsWithVolume = ctlsWithVolume;
	}
	
	public void setBiobankName(String biobankName) {
		this.biobankName = biobankName;
	}
}
