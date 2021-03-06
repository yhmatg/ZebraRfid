package com.zebra.rfidreader.demo.nongshanghang;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtils {
    public static WritableFont arial14font = null;

    public static WritableCellFormat arial14format = null;
    public static WritableFont arial10font = null;
    public static WritableCellFormat arial10format = null;
    public static WritableFont arial12font = null;
    public static WritableCellFormat arial12format = null;

    public final static String UTF8_ENCODING = "UTF-8";
    public final static String GBK_ENCODING = "GBK";

    public static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14,
                    WritableFont.BOLD);
            arial14font.setColour(Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(Colour.VERY_LIGHT_YELLOW);
            arial10font = new WritableFont(WritableFont.ARIAL, 10,
                    WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.LIGHT_BLUE);
            arial12font = new WritableFont(WritableFont.ARIAL, 12);
            arial12format = new WritableCellFormat(arial12font);
            arial12format.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void initExcel(String fileName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("???????????????", 0);
            sheet.addCell((WritableCell) new Label(0, 0, fileName,
                    arial14format));
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], arial10format));
            }
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void writeObjListToExcel(List<T> objList,
                                               String fileName, Context c) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                File newFile = new File(fileName);
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
                in = new FileInputStream(newFile);
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(newFile,
                        workbook);
                WritableSheet sheet = writebook.getSheet(0);
                for (int j = 0; j < objList.size(); j++) {
                    ArrayList<String> list = (ArrayList<String>) objList.get(j);
                    for (int i = 0; i < list.size(); i++) {
                        sheet.addCell(new Label(i, j + 1, list.get(i),
                                arial12format));
                    }
                }
                writebook.write();
                Toast.makeText(c, "?????????????????????????????????Family??????", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static List<FileBean> read2DB(File f, Context con) {
        ArrayList<FileBean> billList = new ArrayList<>();
        try {
            Workbook course = null;
            course = Workbook.getWorkbook(f);
            Sheet sheet = course.getSheet(0);

            Cell cell = null;
            for (int i = 1; i < sheet.getRows(); i++) {
                FileBean tc = new FileBean();
                cell = sheet.getCell(0, i);
                tc.setBatchCode(cell.getContents());
                cell = sheet.getCell(1, i);
                tc.setStartDate(cell.getContents());
                cell = sheet.getCell(2, i);
                tc.setEndDate(cell.getContents());
                cell = sheet.getCell(3, i);
                tc.setEpcCode(cell.getContents());
                cell = sheet.getCell(4, i);
                tc.setBagCode(cell.getContents());
                cell = sheet.getCell(5, i);
                tc.setRegisterCode(cell.getContents());
                cell = sheet.getCell(6, i);
                tc.setOrgName(cell.getContents());
                cell = sheet.getCell(7, i);
                tc.setFileType(cell.getContents());
                cell = sheet.getCell(8, i);
                tc.setFileName(cell.getContents());
                cell = sheet.getCell(9, i);
                tc.setFileNumber(cell.getContents());
                cell = sheet.getCell(10, i);
                tc.setBoxCode(cell.getContents());
                Log.d("gaolei", "Row" + i + "---------" + tc.getEpcCode()
                        + tc.getBagCode() + tc.getBoxCode());
                billList.add(tc);

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return billList;
    }

    public static Object getValueByRef(Class cls, String fieldName) {
        Object value = null;
        fieldName = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName
                .substring(0, 1).toUpperCase());
        String getMethodName = "get" + fieldName;
        try {
            Method method = cls.getMethod(getMethodName);
            value = method.invoke(cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void writeExcel(Context context, List<FileBean> exportOrder,
                                  String fileName) throws Exception {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && getAvailableStorage() > 1000000) {
            Toast.makeText(context, "SD????????????", Toast.LENGTH_LONG).show();
            return;
        }

        //??????????????????????????????
        String[] title = {"?????????","??????????????????", "??????????????????", "EPC??????", "????????????", "???????????????", "????????????????????????", "????????????", "????????????", "????????????", "???????????????","????????????"};
        File file;
        File dir = new File(Environment.getExternalStorageDirectory() + "/????????????/");
        file = new File(dir, fileName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        // ??????Excel?????????
        WritableWorkbook wwb;
        OutputStream os = new FileOutputStream(file);
        wwb = Workbook.createWorkbook(os);
        // ??????????????????????????????????????????Sheet?????????
        WritableSheet sheet = wwb.createSheet("a", 0);
        Label label;
        for (int i = 0; i < title.length; i++) {
            // Label(x,y,z) ?????????????????????x+1?????????y+1???, ??????z
            // ???Label??????????????????????????????????????????????????????
            label = new Label(i, 0, title[i], getHeader());
            // ?????????????????????????????????????????????
            sheet.addCell(label);
        }
        //exportOrder????????????????????????????????????
        for (int i = 0; i < exportOrder.size(); i++) {
            FileBean order = exportOrder.get(i);
            Label batchLabel = new Label(0, i + 1, order.getBatchCode());
            Label startLabel = new Label(1, i + 1, order.getStartDate());
            Label endLable = new Label(2, i + 1, order.getEndDate());
            Label epcLable = new Label(3, i + 1, order.getEpcCode());
            Label bagLable = new Label(4, i + 1, order.getBagCode());
            Label registerLable = new Label(5, i + 1, order.getRegisterCode());
            Label orgLable = new Label(6, i + 1, order.getOrgName());
            Label typeLable = new Label(7, i + 1, order.getFileType());
            Label nameLable = new Label(8, i + 1, order.getFileName());
            Label numberLable = new Label(9, i + 1, order.getFileNumber());
            Label boxLable = new Label(10, i + 1, order.getBoxCode());
            Label statusLable = new Label(11, i + 1, order.getInvStatus().toString());
            sheet.addCell(batchLabel);
            sheet.addCell(startLabel);
            sheet.addCell(endLable);
            sheet.addCell(epcLable);
            sheet.addCell(bagLable);
            sheet.addCell(registerLable);
            sheet.addCell(orgLable);
            sheet.addCell(typeLable);
            sheet.addCell(nameLable);
            sheet.addCell(numberLable);
            sheet.addCell(boxLable);
            sheet.addCell(statusLable);
        }
        // ????????????
        wwb.write();
        // ????????????
        wwb.close();
        Toast.makeText(context, "????????????", Toast.LENGTH_LONG).show();
        //QueryUser(new File(dir, "??????????????????" + ".xls"));
    }

    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10,
                WritableFont.BOLD);// ????????????
        try {
            font.setColour(Colour.BLUE);// ????????????
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// ????????????
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// ????????????
            // format.setBorder(Border.ALL, BorderLineStyle.THIN,
            // Colour.BLACK);// ????????????
            // format.setBackground(Colour.YELLOW);// ????????????
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }

    private static long getAvailableStorage() {

        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        long availableSize = blockSize * availableBlocks;
        // Formatter.formatFileSize(context, availableSize);
        return availableSize;
    }

    public static String root = Environment.getExternalStorageDirectory()
            .getPath();
}