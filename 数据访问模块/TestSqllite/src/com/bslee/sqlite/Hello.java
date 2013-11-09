package com.bslee.sqlite;

import com.j256.ormlite.field.DatabaseField;

public class Hello {

	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField
	String word;

	public Hello() {
		super();
	}

	public Hello(String word) {
		this.word = word;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(id);
		sb.append(" ,word=").append(word);
		return sb.toString();
	}

}
