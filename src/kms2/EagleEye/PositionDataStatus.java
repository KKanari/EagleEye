package kms2.EagleEye;

public class PositionDataStatus {
	private int colorFlg = MainForm.FLG_DEFAULT;
	private int color;
    private String row1;   
    private String row2;
    private String row3;

	public int getColorFlg() {
		return colorFlg;
	}
	public int getColor() {
		return color;
	}
	public String getRow1() {
		return row1;
	}
	public String getRow2() {
		return row2;
	}
	public String getRow3() {
		return row3;
	}
	public void setColorFlg(int colorFlg) {
		this.colorFlg = colorFlg;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public void setRow1(String row1) {
		this.row1 = row1;
	}
	public void setRow2(String row2) {
		this.row2 = row2;
	}
	public void setRow3(String row3) {
		this.row3 = row3;
	}
}