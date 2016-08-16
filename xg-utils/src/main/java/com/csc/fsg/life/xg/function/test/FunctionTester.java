package com.csc.fsg.life.xg.function.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import com.csc.fsg.life.xg.XGNumber;
import com.csc.fsg.life.xg.api.IXGContext;
import com.csc.fsg.life.xg.exceptions.XGException;
import com.csc.fsg.life.xg.function.transformations.CDataString;
import com.csc.fsg.life.xg.function.transformations.ConvertDate;
import com.csc.fsg.life.xg.function.transformations.CurrentDate;
import com.csc.fsg.life.xg.function.transformations.CurrentTime;
import com.csc.fsg.life.xg.function.transformations.Divide;
import com.csc.fsg.life.xg.function.transformations.GetProductCode;
import com.csc.fsg.life.xg.function.transformations.LeftFillDecimal;
import com.csc.fsg.life.xg.function.transformations.Multiply;
import com.csc.fsg.life.xg.function.transformations.NormalizeString;
import com.csc.fsg.life.xg.function.transformations.RightFillDecimal;
import com.csc.fsg.life.xg.function.transformations.Sum;
import com.csc.fsg.life.xg.function.transformations.ToDecimalString;
import com.csc.fsg.life.xg.function.transformations.ToIntValue;

public class FunctionTester {

	private static final Log log = LogFactory.getLog(FunctionTester.class);

	/**
	 * Function Unit Test Driver
	 */
	public static void main(String argv[]) {
		try {
			testFunction(argv);
			// convertDateTest();
			// currentDateTest();
			// currentTimeTest();
			// getProductCodeTest();
		} catch (Exception e) {
			log.error("", e);
		}
	}

	private static void testFunction(String[] argv)
			throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {

		log.info("Function Test Driver1: ");
		String functionName = argv[0];
		log.info("Testing function: " + functionName);

		// Get the function class.
		Class<?> funcClass = Class.forName("com.csc.fsg.life.xg.function.transformations."
				+ functionName);

		// Get the method to call.
		Class<?>[] clzParams = new Class[2];
		clzParams[0] = IXGContext.class;
		clzParams[1] = List.class;
		Method m = funcClass.getMethod("process", clzParams);

		List<Object> params = new ArrayList<Object>();
		for (int i = 1; i < argv.length; i++) {

			String nextp = argv[i];
			log.info("arg[" + i + "] '" + nextp + "'");
			if (nextp.startsWith("[") && nextp.endsWith("]")) {
				nextp = nextp.substring(1, nextp.length() - 1);
				nextp = nextp.trim();

				List<String> arglist = new ArrayList<String>();

				if (nextp.length() > 0) {
					StringTokenizer tok = new StringTokenizer(nextp, ",");
					while (tok.hasMoreTokens()) {
						String nextTok = tok.nextToken();
						arglist.add(nextTok);
					}
				}

				params.add(arglist);
			} else {
				params.add(nextp);
			}
		}

		// Invoke the process method.
		Object[] p = new Object[2];
		p[0] = null;
		p[1] = params;
		log.info("Result: '" + m.invoke(null, p) + "'");
	}

	@Test
	public void cDataStringTest() throws XGException {
		// final String dataString =
		// "The hen is > the egg but < than the \"elephant\"";
		final String dataString = "2 F560 SUM RTND & NET S/B EQL SUSP AM SA";

		List<String> list = new ArrayList<String>();
		list.add(dataString);
		Object obj = CDataString.process(null, list);
		String expected = "<![CDATA[" + dataString + "]]>";
		Assert.assertEquals(obj, expected);
	}

	public static void convertDateTest() {
		ConvertDate convertDate = new ConvertDate();
		String[] formats = convertDate.getControlValues();
		final Log log = LogFactory.getLog(ConvertDate.class);
		for (int i = 3; i < formats.length; i++) {

			String iFormat = formats[i];
			// String date =
			// iFormat.replace('y','a').replace('m','b').replace('d','c');
			String date = iFormat.replaceAll("yyyy", "2007").replaceAll("mm", "05").replaceAll("dd", "03");
			date = date.replaceAll("yy", "07");
			log.info("Input Format " + iFormat + ", Date " + date);
			for (String oFormat : formats) {
				log.info("Output Format " + oFormat);
				// String[] formats = {iFormat, oFormat, date};
				String[] input = { iFormat, oFormat, date };
				try {
					log.info("The final String "
							+ ConvertDate.process(input).toString());
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
	}

	public static void currentDateTest() {
		log.info("CurrentDate:" + CurrentDate.process(null, null).toString());
	}

	public static void currentTimeTest() {
		log.info("Current Time :" + CurrentTime.process(null, null).toString());
	}

	@Test
	public void divideTest() throws XGException {

		List<String> list = new ArrayList<String>();
		list.add("100");
		list.add("2.55");
		Object mul = Multiply.process(list);
		log.info(mul);
		List<String> list1 = new ArrayList<String>();
		list1.add(mul.toString());
		list1.add("85");
		Object result = Divide.process(null, list1);
		log.info(result);
		Assert.assertEquals(3.0, result);
	}

	public static void getProductCodeTest() throws XGException {
		List<String> list = new ArrayList<String>();
		list.add("vcs");
		list.add("       annuity1");
		// list.add (" stss");
		String pc = (String) GetProductCode.process(null, list);
		log.info(pc);
	}

	@Test
	public void multiplyTest() throws XGException {

		ArrayList<String> list = new ArrayList<String>();
		list.add("100");
		list.add("2.55");
		Assert.assertEquals(255.0, Multiply.process(null, list));
	}

	@Test
	public void normalizeStringTest() throws XGException {

		List<String> list = new ArrayList<String>();
		// list.add("2 F560 SUM RTND & NET S/B EQL SUSP AM SA ");
		list.add("The hen is > the egg but < than the \"elephant\"");
		String expected = "The hen is &gt; the egg but &lt; than the &quot;elephant&quot;";
		Object result = NormalizeString.process(null, list);
		log.info("normalize:" + result);
		Assert.assertEquals(expected, result);
	}

	@Test
	public void rightFillDecimalTest() throws XGException {

		List<List<String>> list = new ArrayList<List<String>>();
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		List<String> list3 = new ArrayList<String>();

		list1.add("337.");
		list2.add("2");
		list3.add("0");

		list.add(list1);
		list.add(list2);
		list.add(list3);
		Object result = RightFillDecimal.process(null, list);
		log.info("right fill:" + result);
		String expected = "337.00";
		Assert.assertEquals(expected, result);
	}

	@Test
	public void leftFillDecimalTest() throws XGException {

		List<List<String>> list = new ArrayList<List<String>>();
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		List<String> list3 = new ArrayList<String>();

		list1.add("37");
		list2.add("4");
		list3.add("1");

		list.add(list1);
		list.add(list2);
		list.add(list3);
		Object result = LeftFillDecimal.process(null, list);
		String expected = "37";
		log.info("leftfill:" + result);
		Assert.assertEquals(expected, result);
	}

	@Test
	public void sumTest() throws XGException {

		List<String> list = new ArrayList<String>();
		list.add("723921.76");
		list.add("317106.41");
		XGNumber expected = new XGNumber(1041028.17);
		Object result = Sum.process(null, list);
		log.info("sum:" + result);
		Assert.assertEquals(expected.toString(), result.toString());
	}

	@Test
	public void toDecimalStringTest() throws XGException {

		// String a = new String("16.A2371234234E5");
		String a = "16.2371234234E5";
		// Double a = new Double(16.2371234234E5);

		Object[] input = new Object[1];
		input[0] = a;
		Object object = ToDecimalString.process(input);
		String expected = "1623712.34234";
		log.info("decimal string:" + object);
		Assert.assertEquals(expected, object.toString());
	}

	@Test
	public void toIntValueTest() throws XGException {

		Double a = new Double(16.237);
		Double[] input = new Double[1];
		input[0] = a;
		String expected = "16";
		Object output = ToIntValue.process(input);
		log.info("int value :" + output);
		Assert.assertEquals(expected, output.toString());
	}
}
