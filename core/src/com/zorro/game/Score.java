package com.zorro.game;

import java.util.Date;

public class Score {
	private int score;
	private Date date;

	private String time;

	public Score(long l, Date utilDate) {
		this.score = (int) l;
		this.date = utilDate;
	}

	public Score() {
	}

	public int getScore() {
		return score;
	}

	public String getTime() {
		return time;
	}

	public Date getDate() {
		return date;
	}

	public long getDateEpoch() {
		return date.getTime();
	}

	public Date getDateSQL() {
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		return sqlDate;
	}

	public void setCreationTime(String string) {
		this.time = string;
	}

	public void setScore(int x) {
		this.score = x;
	}
}
