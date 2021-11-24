package org.gs4tr.termmanager.model.xls.report;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * XlsReportCell is an model that is responsible for holding information about
 * particular XLS cell, position and the style that includes font, color, text,
 * etc.
 * 
 * @since 5.0
 */
public class XlsReportCell implements Serializable {

    private static final String FONT_NAME = "Ariel";

    /**
     * 
     */
    private static final long serialVersionUID = 254499387380195723L;

    private boolean _bold = false;

    private int _column = 0;

    private ReportColor _fontColor = ReportColor.BLACK;

    private String _fontName = FONT_NAME;

    private int _fontSize = 11;

    private ReportColor _foregroundColor = ReportColor.WHITE;

    private int _mergeColumns = 10;

    private int _mergeRows = 5;

    private int _row = 0;

    private boolean _strikeout = false;

    private String _text = StringUtils.EMPTY;

    private List<CellTextStyle> _textStyle;

    public XlsReportCell() {
	_bold = false;
	_column = 0;
	_fontColor = ReportColor.BLACK;
	_fontName = FONT_NAME;
	_fontSize = 11;
	_foregroundColor = ReportColor.WHITE;
	_mergeColumns = 10;
	_mergeRows = 5;
	_row = 0;
	_strikeout = false;
	_text = StringUtils.EMPTY;
    }

    public XlsReportCell(int row, int column, boolean bold, ReportColor fontColor, int fontSize,
	    ReportColor foregroundColor, String text) {
	this();
	_bold = bold;
	_column = column;
	_fontColor = fontColor;
	_fontSize = fontSize;
	_foregroundColor = foregroundColor;
	_row = row;
	_text = text;
    }

    public XlsReportCell(int row, int column, List<CellTextStyle> textStyle, String text) {
	this();
	_bold = false;
	_column = column;
	_fontSize = 11;
	_foregroundColor = ReportColor.WHITE;
	_row = row;
	_text = text;
	_textStyle = textStyle;
    }

    public XlsReportCell(int row, int column, ReportColor fontColor, String text) {
	this();
	_bold = false;
	_column = column;
	_fontColor = fontColor;
	_fontSize = 11;
	_foregroundColor = ReportColor.WHITE;
	_row = row;
	_text = text;
    }

    public int getColumn() {
	return _column;
    }

    public ReportColor getFontColor() {
	return _fontColor;
    }

    public String getFontName() {
	return _fontName;
    }

    public int getFontSize() {
	return _fontSize;
    }

    public ReportColor getForegroundColor() {
	return _foregroundColor;
    }

    public int getMergeColumns() {
	return _mergeColumns;
    }

    public int getMergeRows() {
	return _mergeRows;
    }

    public int getRow() {
	return _row;
    }

    public String getText() {
	return _text;
    }

    public List<CellTextStyle> getTextStyle() {
	return _textStyle;
    }

    public boolean isBold() {
	return _bold;
    }

    public boolean isStrikeout() {
	return _strikeout;
    }

    public void setBold(boolean bold) {
	_bold = bold;
    }

    public void setColumn(int column) {
	_column = column;
    }

    public void setFontColor(ReportColor fontColor) {
	_fontColor = fontColor;
    }

    public void setFontName(String fontName) {
	_fontName = fontName;
    }

    public void setFontSize(int fontSize) {
	_fontSize = fontSize;
    }

    public void setForegroundColor(ReportColor foregroundColor) {
	_foregroundColor = foregroundColor;
    }

    public void setMergeColumns(int mergeColumns) {
	_mergeColumns = mergeColumns;
    }

    public void setMergeRows(int mergeRows) {
	_mergeRows = mergeRows;
    }

    public void setRow(int row) {
	_row = row;
    }

    public void setStrikeout(boolean strikeout) {
	_strikeout = strikeout;
    }

    public void setText(String text) {
	_text = text;
    }

    public void setTextStyle(List<CellTextStyle> textStyle) {
	_textStyle = textStyle;
    }

    @Override
    public String toString() {
	return "XlsReportCell [_row=" + _row + ", _column=" + _column + ", _text=" + _text + "]";
    }
}
