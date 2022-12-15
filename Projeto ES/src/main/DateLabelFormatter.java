package main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * 
 * Classe de suporte à component <b>JDatePicker</b>
 *
 */

@SuppressWarnings("serial")
public class DateLabelFormatter extends AbstractFormatter {

	private String datePattern = "yyyy-MM-dd";
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

	@Override
	public Object stringToValue(String text) throws ParseException {
		return dateFormatter.parseObject(text);
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (value != null) {
			Calendar cal = (Calendar) value;
			return dateFormatter.format(cal.getTime());
		}

		return "";
	}

	/**
	 * Retorna um objeto <b>Properties</b> com as propriedades pretendidas
	 * 
	 * @return as propriedades da data de acordo com o formato pretendido
	 */

	public static Properties fillProperties() {
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		return p;
	}

}
