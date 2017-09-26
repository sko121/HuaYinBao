package com.thtfit.pos.iChart.parameter;

/**
 * 图表的脚注
 * @author SQ
 *
 */
public class Footnote {
	private String footnote; //脚注配置项
	private String footnote_align; //脚注的对齐方式,可选项有：'left'、'center'、'right'
	
	/**
	 * 图表的脚注
	 * @param footnote 脚注
	 * @param footnote_align 脚注的对齐方式,可选项有：'left'、'center'、'right'
	 */
	public Footnote(String footnote, String footnote_align) {
		super();
		this.footnote = footnote;
		this.footnote_align = footnote_align;
	}

	public String getFootnote() {
		return footnote;
	}

	public void setFootnote(String footnote) {
		this.footnote = footnote;
	}

	public String getFootnote_align() {
		return footnote_align;
	}

	public void setFootnote_align(String footnote_align) {
		this.footnote_align = footnote_align;
	}
	
	
}
