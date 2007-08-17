
//$Id$

package net.sf.persist.tests.common;

import net.sf.persist.annotations.Column;

// doesn't specify a table and guessed names won't work -- will blow
public class Simple05 {

	private long id;
	private String stringCol;
	private long intCol;

	@Column(autoGenerated=true)
	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public String getStringCol() { return stringCol; }
	public void setStringCol(String stringCol) { this.stringCol = stringCol; }

	public long getIntCol() { return intCol; }
	public void setIntCol(long intCol) { this.intCol = intCol; }

}

