package com.zebra.rfidreader.nonghang.zhongyou.model;

public class DistrictModel {
	private String Piecearea;
	private String PieceareaID;

	public DistrictModel() {
		super();
	}

	public DistrictModel(String piecearea, String pieceareaID) {
		Piecearea = piecearea;
		PieceareaID = pieceareaID;
	}

	public String getPiecearea() {
		return Piecearea;
	}

	public void setPiecearea(String piecearea) {
		Piecearea = piecearea;
	}

	public String getPieceareaID() {
		return PieceareaID;
	}

	public void setPieceareaID(String pieceareaID) {
		PieceareaID = pieceareaID;
	}

	@Override
	public String toString() {
		return "DistrictModel [Piecearea=" + Piecearea + ", PieceareaID=" + PieceareaID + "]";
	}

}
