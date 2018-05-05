package com.fsj.util;

import com.fsj.common.Excel;
import com.fsj.common.Pair;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.util.ReflectionUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelUtils {
	private static final Logger LOG = LoggerFactory.getLogger(ExcelUtils.class);
	public enum ExcelType{
		IMPORT,EXPORT
	}


	/**
	 * 根excelWriter生成wb，然后生成file
	 *
	 * @param lists
	 * @param filePatch
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	public static <T> File writeToFile(List<T> lists, String filePatch) throws IOException {
		ExcelWriter<T> excelWriter = new ExcelWriter<>(lists);
		Workbook wb = excelWriter.write();
		File file = new File(filePatch);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileOutputStream out = new FileOutputStream(file);
		wb.write(out);
		out.flush();
		out.close();
		return file;
	}


	/**
	 * 初始化entity字段并排序
	 *
	 * @throws Exception
	 */
	public static <T> Map<String, Field> initSortNameFieldMap(Class<T> clazz, ExcelType excelType) throws Exception {
		final Field[] fields = ReflectUtils.getFieldsRemoveSame(clazz);
		Excel excel = null;
		Map<String, Field> nameMap = new HashMap<>();
		for (Field field : fields) {
			excel = field.getAnnotation(Excel.class);
			if(excelType.equals(ExcelType.IMPORT)) {
				if (null == excel || excel.skip()) {
					continue;
				}
			}else if(ExcelType.EXPORT.equals(excelType)){
				if (null == excel) {
					continue;
				}
			}
			nameMap.put(excel.name(), field);
		}
		return MapUtils.sortMapByValue(nameMap, new Comparator<Map.Entry<String, Field>>() {
			@Override
			public int compare(Map.Entry<String, Field> o1, Map.Entry<String, Field> o2) {
				int seq1 = o1.getValue().getAnnotation(Excel.class).seq();
				int seq2 = o2.getValue().getAnnotation(Excel.class).seq();
				return seq1 >= seq2 ? 1 : -1;
			}
		});
	}

	public static Object readCellContent(Cell cell) {
		Object value = null;
		if (cell == null) {
			return value;
		}
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN: {
				value = cell.getBooleanCellValue();
				break;
			}
			case Cell.CELL_TYPE_NUMERIC: {
				if (!org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
					DecimalFormat df = new DecimalFormat("#.######");
					value = df.format(cell.getNumericCellValue());
				} else {
					value = cell.getNumericCellValue();
					java.util.Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate((double) value);
					value = DateUtil.date2StrByDefault(date);
				}
				break;
			}
			case Cell.CELL_TYPE_STRING: {
				value = cell.getStringCellValue().trim();
				break;
			}
			case Cell.CELL_TYPE_ERROR: {
				value = cell.getErrorCellValue();
				break;
			}
			case Cell.CELL_TYPE_BLANK: {
				value = null;
				break;
			}
			case Cell.CELL_TYPE_FORMULA: {
				value = cell.getCellFormula();
				break;
			}
			default: {
				value = null;
				break;
			}
		}
		return value;
	}

	public static <E> void setFieldValue(Field field, E e, ExcelDataFormatter edf, Object value)
			throws IllegalAccessException, ParseException {
		if (field != null && value != null) {
			field.setAccessible(true);
			Boolean bool = true;
			Map<String, String> map = null;
			if (null == edf) {
				bool = false;
			} else {
				map = edf.get(field.getName());
				if (null == map) {
					bool = false;
				}
			}
			//spel赋值
			Excel.SpELMethod spELMethod = field.getAnnotation(Excel.SpELMethod.class);
			if (spELMethod != null) {
				if (!"".equals(spELMethod.targetField())) {
					Object val = SpEL.val(spELMethod.expression(),
							new BeanFactoryResolver(SpringContextHolder.getApplicationContext()), null, value,
							spELMethod.result());
					Field targetField = ReflectionUtils.findField(e.getClass(), spELMethod.targetField());
					targetField.set(e, val);
					return;
				}
			}
			if (field.getType().equals(Date.class)) {//Date
				if (value.getClass().equals(Date.class)) {
					field.set(e, value);
				} else {
					field.set(e, DateUtil.str2DateByDefault(value.toString()));
				}
			} else if (field.getType().equals(String.class)) {//String
				if (value.getClass().equals(String.class)) {
					field.set(e, value);
				} else {
					field.set(e, value.toString());
				}
			} else if (field.getType().equals(Long.class)) {
				if (value.getClass().equals(Long.class)) {
					field.set(e, value);
				} else {
					field.set(e, Long.parseLong(value.toString()));
				}
			} else if (field.getType().equals(Integer.class)) {
				if (value.getClass().equals(Integer.class)) {
					field.set(e, value);
				} else {
					// 检查是否需要转换
					if (bool) {
						field.set(e, map.get(value.toString()) != null ? Integer.parseInt(map.get(value.toString()))
								: Integer.parseInt(value.toString()));
					} else {
						field.set(e, Integer.parseInt(value.toString()));
					}

				}
			} else if (field.getType().equals(BigDecimal.class)) {
				if (value.getClass().equals(BigDecimal.class)) {
					field.set(e, value);
				} else {
					field.set(e, new BigDecimal(value.toString()));
				}
			} else if (field.getType().equals(Boolean.class)) {
				if (value.getClass().equals(Boolean.class)) {
					field.set(e, value);
				} else {
					// 检查是否需要转换
					if (bool) {
						field.set(e, map.get(value.toString()) != null ? Boolean.parseBoolean(map.get(value.toString()))
								: Boolean.parseBoolean(value.toString()));
					} else {
						field.set(e, Boolean.parseBoolean(value.toString()));
					}
				}
			} else if (field.getType().equals(Float.class)) {
				if (value.getClass().equals(Float.class)) {
					field.set(e, value);
				} else {
					field.set(e, Float.parseFloat(value.toString()));
				}
			} else if (field.getType().equals(Double.class)) {
				if (value.getClass().equals(Double.class)) {
					field.set(e, value);
				} else {
					field.set(e, Double.parseDouble(value.toString()));
				}

			}
		}
	}


	public static XSSFCellStyle getTitleCellStyle(Workbook wb) {
		XSSFCellStyle titleStyle = (XSSFCellStyle) wb.createCellStyle();
		titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		// 设置前景色
		titleStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(159, 213, 183)));
		titleStyle.setAlignment(CellStyle.ALIGN_CENTER);

		Font font = wb.createFont();
		font.setColor(HSSFColor.BROWN.index);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		// 设置字体
		titleStyle.setFont(font);
		return titleStyle;
	}

	private static boolean isSkipExport(Excel.SkipExportType skipexport, Field field) {
		if (skipexport != null) {
			Excel.SkipExport skipExportField = field.getAnnotation(Excel.SkipExport.class);
			if (skipExportField != null) {
				for (Excel.SkipExportType skipExportType : Excel.SkipExportType.values()) {
					skipexport.equals(skipExportType);
					return true;
				}
			}
		}
		return false;
	}

	public String getNumbers(String s) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(s);
		return m.replaceAll("").trim();
	}

	/**
	 * 把List<Map<>>类型写入到excel中 fsj 2017.11.2
	 *
	 * @param mapList
	 *            数据
	 * @param titleRow
	 *            标题是一行*N列，标题行,故称title row
	 * @param fullfilename
	 *            文件全路径名
	 */
	public static void writeToExcel(List<LinkedHashMap<String, String>> mapList, ArrayList<String> titleRow,
			String fullfilename) throws IOException {
		assert fullfilename != null && fullfilename != "";

		File file = new File(fullfilename);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		OutputStream out = new FileOutputStream(file);

		Workbook workbook = new SXSSFWorkbook();
		Sheet sheet = workbook.createSheet();

		CellStyle style = workbook.createCellStyle(); // 标题样式对象
		// 设置默认风格单元格的背景颜色为白色
		style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直
		style.setAlignment(CellStyle.ALIGN_CENTER);// 水平
		style.setWrapText(true);// 指定当单元格内容显示不下时自动换行
		CellStyle style_context = workbook.createCellStyle();// 正文样式对象
		style_context.cloneStyleFrom(style);

		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontName("宋体");
		font.setFontHeight((short) 280);
		style.setFont(font); // 粗体

		CellStyle style_blue = workbook.createCellStyle();
		style_blue.cloneStyleFrom(style);
		style_blue.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());

		CellStyle style_green = workbook.createCellStyle();
		style_green.cloneStyleFrom(style);
		style_green.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());

		CellStyle style_grey = workbook.createCellStyle();
		style_grey.cloneStyleFrom(style);
		style_grey.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());// 灰色

		//http://blog.csdn.net/downkang/article/details/14164811
		CellStyle style_deepgrey = workbook.createCellStyle();
		style_deepgrey.cloneStyleFrom(style);
		style_deepgrey.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());//深灰
		CellStyle style_red = workbook.createCellStyle();
		style_red.cloneStyleFrom(style);
		style_red.setFillForegroundColor(IndexedColors.RED.getIndex());

		CellStyle[] styles = { style, style_context, style_blue, style_green, style_grey, style_deepgrey, style_red };
		for (int i = 0; i < styles.length; i++) {
			styles[i].setBorderBottom(CellStyle.BORDER_THIN);
			styles[i].setBorderTop(CellStyle.BORDER_THIN);
			styles[i].setBorderLeft(CellStyle.BORDER_THIN);
			styles[i].setBorderRight(CellStyle.BORDER_THIN);
			styles[i].setFillPattern(CellStyle.SOLID_FOREGROUND);
		}

		// 单元格合并
		int colIndexStartMerge = 4;//前4列title简单输出，后面的要分割输出到三行
		//添加表头
		Row row = sheet.createRow(0);
		row.setHeight((short) 540);
		Cell cell = row.createCell(colIndexStartMerge);
		cell.setCellValue("功能菜单"); //创建第一行
		cell.setCellStyle(style_blue); // 样式，居中, 蓝底

		// 四个参数分别是：起始行，起始列，结束行，结束列
		sheet.addMergedRegion(new CellRangeAddress(0, 0, colIndexStartMerge, titleRow.size() - 2));

		Row row2 = sheet.createRow(1); //创建第二行
		Row row3 = sheet.createRow(2);
		Row row4 = sheet.createRow(3);
		row.setHeight((short) 540);
		Cell cell2, cell3, cell4;

		ArrayList<String> line1 = new ArrayList<>();// title line. use for merge
		ArrayList<String> line2 = new ArrayList<>();

		for (int i = 0; i < colIndexStartMerge; i++) {
			cell = row.createCell(i);
			cell2 = row2.createCell(i);
			cell3 = row3.createCell(i);
			cell4 = row4.createCell(i);
			cell.setCellValue(titleRow.get(i));
			cell.setCellStyle(style);
			cell2.setCellStyle(style);
			cell3.setCellStyle(style);
			cell4.setCellStyle(style);

			line1.add(titleRow.get(i));
			line2.add(titleRow.get(i));
		}
		// merge first 4 col
		for (int c = 0; c < colIndexStartMerge; c++) {
			sheet.addMergedRegion(new CellRangeAddress(0, 3, c, c));
		}

		for (int i = colIndexStartMerge; i < titleRow.size() - 1; i++) {
			cell2 = row2.createCell(i);
			cell3 = row3.createCell(i);
			cell4 = row4.createCell(i);

			ArrayList<String> col = new ArrayList<>(Arrays.asList(titleRow.get(i).split(":")));// eg. 5:1:2
			if (col.get(0).contains("权限管理")){
				cell2.setCellValue(col.get(0)+"(暂不开放)");// line1
				cell2.setCellStyle(style_red);
			}else {
				cell2.setCellValue(col.get(0));
				cell2.setCellStyle(style_green);
			}

			cell3.setCellValue(col.get(1));// line2
			cell4.setCellValue(col.get(2));// line3

			cell3.setCellStyle(style_grey);
			cell4.setCellStyle(style_grey);
			sheet.setColumnWidth(i, 20 * 256);

			line1.add(col.get(0));
			line2.add(col.get(1));
		}
		line1.add("|");
		line2.add("|");

		// 合并1、2行的单元格
		ArrayList<Pair<Integer, Integer>> borders = new ArrayList<>();
		borders.add(new Pair<>(colIndexStartMerge, titleRow.size() - 2));
		ArrayList<Pair<Integer, Integer>> borders_line1 = GetSpan(line1, borders);
		ArrayList<Pair<Integer, Integer>> borders_line2 = GetSpan(line2, borders_line1);
		for (Pair<Integer, Integer> p : borders_line1) {
			sheet.addMergedRegion(new CellRangeAddress(1, 1, p.getKey(), p.getValue()));
		}
		for (Pair<Integer, Integer> p : borders_line2) {
			sheet.addMergedRegion(new CellRangeAddress(2, 2, p.getKey(), p.getValue()));
		}
		/*
		 * for (int i = 1; i < 3; i++) { Row r = sheet.getRow(i); Cell left =
		 * r.getCell(colIndexStartMerge); for (int j = colIndexStartMerge + 1; j <
		 * r.getLastCellNum(); j++) { Cell cursor = r.getCell(j); if
		 * (!cursor.getStringCellValue().equals(left.getStringCellValue())) { if
		 * (left.getColumnIndex() != cursor.getColumnIndex() - 1) {
		 * sheet.addMergedRegion(new CellRangeAddress(i, i, left.getColumnIndex(),
		 * cursor.getColumnIndex() - 1)); } left = cursor; } } }
		 */

		//循环写入行数据
		for (int i = 0; i < mapList.size(); i++) {
			LinkedHashMap<String, String> linkedHashMap = mapList.get(i);
			row = sheet.createRow(i + 4);
			row.setHeight((short) 500);

			Iterator it = linkedHashMap.keySet().iterator();
			int j = 0;
			while (it.hasNext()) {
				cell = row.createCell(j);
				j++;
				String key = it.next().toString();
				if (key.contains("权限管理")){
					cell.setCellStyle(style_deepgrey);
				}
				else{
					cell.setCellStyle(style_context);
					cell.setCellValue(linkedHashMap.get(key));
				}
			}
		}

		//完工
		workbook.write(out);
		out.flush();
		out.close();
	}

	/**
	 * 找出一行单元格合并的边界
	 * 
	 * @param line
	 *            行
	 * @param borders
	 *            已有的边界
	 * @return 应该合并的边界
	 */
	private static ArrayList<Pair<Integer, Integer>> GetSpan(ArrayList<String> line,
			ArrayList<Pair<Integer, Integer>> borders) {
		ArrayList<Pair<Integer, Integer>> res = new ArrayList<>();

		for (Pair<Integer, Integer> p : borders) {
			Integer left = p.getKey();
			Integer right = p.getValue() + 1;
			for (int mid = left; mid <= right; mid++) {
				if (!line.get(mid).equals(line.get(left))) {
					if (mid - 1 > left) {
						res.add(new Pair<>(left, mid - 1));
					}
					left = mid;
				}
			}
			if (left.equals(p.getKey())) {
				res.add(new Pair<>(left, right - 1));
			}
		}
		return res;
	}

	public static void main(String[] args) {

	}

}
