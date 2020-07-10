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
            WritableSheet sheet = workbook.createSheet("家庭帐务表", 0);
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
                Toast.makeText(c, "导出到手机存储中文件夹Family成功", Toast.LENGTH_SHORT).show();
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
                tc.setEpcCode(cell.getContents());
                cell = sheet.getCell(1, i);
                tc.setBatchCode(cell.getContents());
                cell = sheet.getCell(2, i);
                tc.setStartDate(cell.getContents());
                cell = sheet.getCell(3, i);
                tc.setEndDate(cell.getContents());
                cell = sheet.getCell(4, i);
                tc.setBagSealDate(cell.getContents());
                cell = sheet.getCell(5, i);
                tc.setBoxSealDate(cell.getContents());
                cell = sheet.getCell(6, i);
                tc.setInHouseDate(cell.getContents());
                cell = sheet.getCell(7, i);
                tc.setOutHouseDate(cell.getContents());
                cell = sheet.getCell(8, i);
                tc.setDestoryDate(cell.getContents());
                cell = sheet.getCell(9, i);
                tc.setBagCode(cell.getContents());
                cell = sheet.getCell(10, i);
                tc.setBarNumber(cell.getContents());
                cell = sheet.getCell(11, i);
                tc.setRegisterCode(cell.getContents());
                cell = sheet.getCell(12, i);
                tc.setOrgName(cell.getContents());
                cell = sheet.getCell(13, i);
                tc.setFileType(cell.getContents());
                cell = sheet.getCell(14, i);
                tc.setFileName(cell.getContents());
                cell = sheet.getCell(15, i);
                tc.setFileNumber(cell.getContents());
                cell = sheet.getCell(16, i);
                tc.setAreaCode(cell.getContents());
                cell = sheet.getCell(17, i);
                tc.setShelfCode(cell.getContents());
                cell = sheet.getCell(18, i);
                tc.setShelfFloorCode(cell.getContents());
                cell = sheet.getCell(19, i);
                tc.setShelfColumCode(cell.getContents());
                cell = sheet.getCell(20, i);
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
            Toast.makeText(context, "SD卡不可用", Toast.LENGTH_LONG).show();
            return;
        }

        //这些是你要导出的字段
        String[] title = {"EPC编码","批次号","业务开始日期", "业务结束日期", "封袋日期","封箱日期","入库日期","出库日期","销毁日期", "封袋编号", "条码编号","登记机构号", "档案所属机构名称", "档案种类", "档案名称", "档案本数","区域编号","档案架号","层号","列号", "档案袋/箱编号","盘点状态"};
        File file;
        File dir = new File(Environment.getExternalStorageDirectory() + "/新文件夹/");
        file = new File(dir, fileName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        // 创建Excel工作表
        WritableWorkbook wwb;
        OutputStream os = new FileOutputStream(file);
        wwb = Workbook.createWorkbook(os);
        // 添加第一个工作表并设置第一个Sheet的名字
        WritableSheet sheet = wwb.createSheet("a", 0);
        Label label;
        for (int i = 0; i < title.length; i++) {
            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
            // 在Label对象的子对象中指明单元格的位置和内容
            label = new Label(i, 0, title[i], getHeader());
            // 将定义好的单元格添加到工作表中
            sheet.addCell(label);
        }
        //exportOrder就是你要导出的对应字段值
        for (int i = 0; i < exportOrder.size(); i++) {
            FileBean order = exportOrder.get(i);
            Label epcLable = new Label(0, i + 1, order.getEpcCode());
            Label batchLabel = new Label(1, i + 1, order.getBatchCode());
            Label startLabel = new Label(2, i + 1, order.getStartDate());
            Label endLable = new Label(3, i + 1, order.getEndDate());
            Label bagSealLable = new Label(4, i + 1, order.getBagSealDate());
            Label boxSealLable = new Label(5, i + 1, order.getBoxSealDate());
            Label inHouseLable = new Label(6, i + 1, order.getInHouseDate());
            Label outHouseLable = new Label(7, i + 1, order.getOutHouseDate());
            Label destoryDateLable = new Label(8, i + 1, order.getDestoryDate());
            Label bagLable = new Label(9, i + 1, order.getBagCode());
            Label barNumberLable = new Label(10, i + 1, order.getBarNumber());
            Label registerCodeLable = new Label(11, i + 1, order.getRegisterCode());
            Label orgNameLable = new Label(12, i + 1, order.getOrgName());
            Label fileTypeLable = new Label(13, i + 1, order.getFileType());
            Label fileNameLable = new Label(14, i + 1, order.getFileName());
            Label fileNumberLable = new Label(15, i + 1, order.getFileNumber());
            Label areaCodeLable = new Label(16, i + 1, order.getAreaCode());
            Label shelfCodeLable = new Label(17, i + 1, order.getShelfCode());
            Label shelfFloorLable = new Label(18, i + 1, order.getShelfFloorCode());
            Label shelfColumLable = new Label(19, i + 1, order.getShelfColumCode());
            Label boxLable = new Label(20, i + 1, order.getBoxCode());
            Label statusLable = new Label(21, i + 1, order.getInvStatus().toString());
            sheet.addCell(epcLable);
            sheet.addCell(batchLabel);
            sheet.addCell(startLabel);
            sheet.addCell(endLable);
            sheet.addCell(bagSealLable);
            sheet.addCell(boxSealLable);
            sheet.addCell(inHouseLable);
            sheet.addCell(outHouseLable);
            sheet.addCell(destoryDateLable);
            sheet.addCell(bagLable);
            sheet.addCell(barNumberLable);
            sheet.addCell(registerCodeLable);
            sheet.addCell(orgNameLable);
            sheet.addCell(fileTypeLable);
            sheet.addCell(fileNameLable);
            sheet.addCell(fileNumberLable);
            sheet.addCell(areaCodeLable);
            sheet.addCell(shelfCodeLable);
            sheet.addCell(shelfFloorLable);
            sheet.addCell(shelfColumLable);
            sheet.addCell(boxLable);
            sheet.addCell(statusLable);
        }
        // 写入数据
        wwb.write();
        // 关闭文件
        wwb.close();
        Toast.makeText(context, "写入成功", Toast.LENGTH_LONG).show();
        //QueryUser(new File(dir, "采矿权信息表" + ".xls"));
    }

    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10,
                WritableFont.BOLD);// 定义字体
        try {
            font.setColour(Colour.BLUE);// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            // format.setBorder(Border.ALL, BorderLineStyle.THIN,
            // Colour.BLACK);// 黑色边框
            // format.setBackground(Colour.YELLOW);// 黄色背景
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