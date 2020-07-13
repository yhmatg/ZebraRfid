package com.zebra.rfidreader.demo.nongshanghang;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.multilevel.treelist.Node;
import com.multilevel.treelist.TreeRecyclerAdapter;
import com.zebra.rfidreader.demo.R;
import com.zebra.rfidreader.demo.application.Application;
import com.zebra.rfidreader.demo.common.ResponseHandlerInterfaces;
import com.zebra.rfidreader.demo.home.MainActivity;
import com.zebra.rfidreader.demo.inventory.InventoryListItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class SHVertifyFragment extends Fragment implements ResponseHandlerInterfaces.ResponseTagHandler, ResponseHandlerInterfaces.TriggerEventHandler, FileBeanVertifyAdapter.OnItemClickListener, EpcBeanAdapter.OnItemClickListener {
    private Button btRead;
    private Button btWrite;
    private Button chooseBox;
    //当前按封袋编号处理过的集合
    ArrayList<FileBean> currentFileList = new ArrayList<>();
    //当前所有册的EPC集合
    ArrayList<String> currentEpcList = new ArrayList<>();
    //当前盘点到的EPC集合
    ArrayList<String> currentInvedList = new ArrayList<>();
    private FloatingActionButton inventoryButton;
    private MaterialDialog multipleDialog;
    private View multiContentView;
    private RecyclerView multiRecycle;
    private RecyclerView fileRecycle;
    protected List<BoxBean> multiDatas = new ArrayList<>();
    private SingleSelectAdapter multiAdapter;
    //没有按照封袋划分的文件集合
    private List<FileBean> excelfileBeans = new ArrayList<>();
    //按照封袋划分的文件集合
    private List<FileBean> bagFileBeans = new ArrayList<>();
    private FileBeanVertifyAdapter fileBeanAdapter;
    private HashSet<String> selectBoxs = new HashSet<>();
    //封袋号和对应的档案
    HashMap<String, ArrayList<FileBean>> bagMap = new HashMap<>();
    //epc和对应的档案
    HashMap<String, FileBean> epcFileMap = new HashMap<>();
    private MaterialDialog updateDialog;
    private TextView tvBagCode;
    private TextView tvEpc;
    private RecyclerView EpcRecycle;
    private EpcBeanAdapter epcBeanAdapter;
    private List<EpcBean> epcBeans = new ArrayList<>();
    private View contentView;
    private TextView selectFileNum;
    private TextView invNum;
    private EditText mSearchData;
    private Button searchButton;
    private TextView result;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initData() {
        List<FileBean> allFileBeans = DemoDatabase.getInstance().getFileBeanDao().getAllFileBeans();
        excelfileBeans.clear();
        excelfileBeans.addAll(allFileBeans);
        divideByBoxcode(excelfileBeans);
        //divideByBag(excelfileBeans);
        fileBeanAdapter.notifyDataSetChanged();
    }

    public static SHVertifyFragment newInstance() {
        return new SHVertifyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hn_vertify, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btRead = (Button) getActivity().findViewById(R.id.bt_read);
        btWrite = (Button) getActivity().findViewById(R.id.bt_write);
        chooseBox = (Button) getActivity().findViewById(R.id.bt_choose_box);
        inventoryButton = (FloatingActionButton) getActivity().findViewById(R.id.inventoryButton);
        selectFileNum = (TextView) getActivity().findViewById(R.id.file_num);
        invNum = (TextView) getActivity().findViewById(R.id.inv_num);
        mSearchData =  getActivity().findViewById(R.id.et_searvh);
        searchButton =  getActivity().findViewById(R.id.bt_search);
        result =  getActivity().findViewById(R.id.tv_result);
        btRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置任意类型
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });

        btWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<FileBean> writeBeans = new ArrayList<>();
                for (FileBean fileBean : currentFileList) {
                    for (EpcBean epc : fileBean.getEpcs()) {
                        FileBean toFileBean = new FileBean();
                        copyFileBean(fileBean, toFileBean);
                        toFileBean.setEpcCode(epc.getEpc());
                        toFileBean.setInvStatus(epc.isInved());
                        writeBeans.add(toFileBean);
                    }
                }
                try {
                    ExcelUtils.writeExcel(getContext(), writeBeans, "excelResult.xls");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchData = mSearchData.getText().toString();
                if(searchData.isEmpty()){
                    Toast.makeText(getActivity(),"请输入箱号再查询",Toast.LENGTH_SHORT).show();
                }else {
                    List<FileBean> fileBeansByBoxCode = DemoDatabase.getInstance().getFileBeanDao().getFileBeanByBoxCode(searchData);
                    divideByBag(fileBeansByBoxCode);
                    fileBeanAdapter.notifyDataSetChanged();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }

            }
        });

        chooseBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultipleDialog();
            }
        });
        fileBeanAdapter = new FileBeanVertifyAdapter(currentFileList, getActivity());
        fileBeanAdapter.setOnItemClickListener(this);
        fileRecycle = (RecyclerView) getActivity().findViewById(R.id.file_recycle);
        fileRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        fileRecycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        fileRecycle.setAdapter(fileBeanAdapter);
        initMultiDialogView();
        initData();
        //初始化epcdialog布局
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.file_epc_dialog, null);
        tvBagCode = (TextView) contentView.findViewById(R.id.tv_bagCode);
        //tvEpc = (TextView) contentView.findViewById(R.id.tv_epc);
        epcBeanAdapter = new EpcBeanAdapter(epcBeans,getActivity());
        epcBeanAdapter.setOnItemClickListener(this);
        EpcRecycle = (RecyclerView) contentView.findViewById(R.id.epc_recycle);
        EpcRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        EpcRecycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        EpcRecycle.setAdapter(epcBeanAdapter);
    }

    public void showMultipleDialog() {
        if (multipleDialog != null) {
            multipleDialog.show();
        } else {
            multipleDialog = new MaterialDialog.Builder(getActivity())
                    .customView(multiContentView, false)
                    .show();
            Window dialogWindow = multipleDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.3f);
            }
            //设置dialog宽度沾满全屏
            Window window = multipleDialog.getWindow();
            // 把 DecorView 的默认 padding 取消，同时 DecorView 的默认大小也会取消
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            // 设置宽度
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
    }

    public void initMultiDialogView() {
        multiContentView = LayoutInflater.from(getActivity()).inflate(R.layout.multiple_choice_dialog, null);
        TextView tvSubmit = (TextView) multiContentView.findViewById(R.id.tv_finish);
        TextView tvCancel = (TextView) multiContentView.findViewById(R.id.tv_cancle);
        multiRecycle = (RecyclerView) multiContentView.findViewById(R.id.multi_recycle);
        multiRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        multiAdapter = new SingleSelectAdapter(multiDatas, getActivity());
        multiRecycle.setAdapter(multiAdapter);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoxBean selectedBean = multiAdapter.getSelectedBoxBean();
                if(selectedBean != null && selectedBean.isChecked()){
                    List<FileBean> fileBeansByBoxCode = DemoDatabase.getInstance().getFileBeanDao().getFileBeanByBoxCode(selectedBean.getBoxName());
                    divideByBag(fileBeansByBoxCode);
                    fileBeanAdapter.notifyDataSetChanged();
                    result.setText("缺失");
                    result.setTextColor(getResources().getColor(R.color.red));
                }else {
                    divideByBag(new ArrayList<FileBean>());
                    fileBeanAdapter.notifyDataSetChanged();
                    result.setText("无");
                    result.setTextColor(getResources().getColor(R.color.red));
                }
                if (multipleDialog != null) {
                    multipleDialog.dismiss();
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multipleDialog != null) {
                    multipleDialog.dismiss();
                }
            }
        });


    }

    @Override
    public void handleTagResponse(InventoryListItem inventoryListItem, boolean isAddedToList) {
        //hex
        String hexEpc = inventoryListItem.getTagID();
        //ascii
        String epc = hex2ascii(hexEpc);
        String[] split = epc.split("\\|");
        String epcHead = split[0];
        Log.e("epc1=====", hexEpc);
        Log.e("epc2=====", epcHead);
        FileBean fileBean = epcFileMap.get(epcHead);
        boolean isInved = true;
        if (fileBean != null) {
            for (EpcBean fileBeanEpc : fileBean.getEpcs()) {
                if (epc.equals(fileBeanEpc.getEpc())) {
                    fileBeanEpc.setInved(true);
                }
                if (!fileBeanEpc.isInved()) {
                    isInved = false;
                }
            }
            fileBean.setInvStatus(isInved);
            fileBeanAdapter.notifyDataSetChanged();
            if(currentEpcList.contains(epc) && !currentInvedList.contains(epc)){
                currentInvedList.add(epc);
            }
            invNum.setText(String.valueOf(currentInvedList.size()));
            if(currentEpcList.size() == currentInvedList.size()){
                result.setText("完整");
                result.setTextColor(getResources().getColor(R.color.blue));
            }else {
                result.setText("缺失");
                result.setTextColor(getResources().getColor(R.color.red));
            }
        }
    }

    private static String hex2ascii(String tagID) {
        if (tagID != null && !tagID.equals("")) {
            String hex = tagID;
            int n = hex.length();
            if (((n % 2) > 0)) {
                return tagID;
            }
            StringBuilder sb = new StringBuilder(n / 2);
            try {
                sb = new StringBuilder((n / 2) + 2);
                //prefexing the ascii representation with a single quote
                for (int i = 0; i < n; i += 2) {
                    char a = hex.charAt(i);
                    char b = hex.charAt(i + 1);
                    char c = (char) ((hexToInt(a) << 4) | hexToInt(b));
                    if (a == '0' && b == '0') {
                        continue;
                    }
                    if (hexToInt(a) <= 7 && hexToInt(b) <= 0xf && c >= 0x20 && c <= 0x7f)
                        sb.append(c);
                    else
                        return tagID;
                }
            } catch (IllegalArgumentException iae) {
                return tagID;
            }
            //prefexing the ascii representation with a single quote
            return sb.toString();
        } else
            return tagID;
    }

    private static int hexToInt(char ch) {
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        throw new IllegalArgumentException(String.valueOf(ch));
    }

    public static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }
        return hex.toString();
    }

    @Override
    public void triggerPressEventRecieved() {
        if (!Application.mIsInventoryRunning && getActivity() != null) {
            Application.mInventoryStartPending = true;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null) {
                        activity.inventoryStartOrStop(inventoryButton);
                    }
                }
            });
        }
    }

    @Override
    public void triggerReleaseEventRecieved() {
        if (((Application.mIsInventoryRunning == true) || (Application.mInventoryStartPending == true)) && getActivity() != null) {
            Application.mInventoryStartPending = false;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null) {
                        activity.inventoryStartOrStop(inventoryButton);
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                importExcle(data);
            }
        }
    }

    /**
     * 导入选中文件
     */
    public void importExcle(Intent data) {
        Uri uri = data.getData();
        if (uri != null) {
            String path = getPath(getContext(), uri);
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    //获取的路径
                    String upLoadFilePath = file.toString();
                    //文件名
                    String upLoadFileName = file.getName();
                    String[] strArray = upLoadFileName.split("\\.");
                    int suffixIndex = strArray.length - 1;
                    File dir = new File(upLoadFilePath);
                    //调用查询方法
                    //ExcelUtil.QueryUser(this, dir);
                    List<FileBean> fileBeans = ExcelUtils.read2DB(dir, getActivity());
                    excelfileBeans.clear();
                    excelfileBeans.addAll(fileBeans);
                    divideByBoxcode(excelfileBeans);
                    divideByBag(excelfileBeans);
                    fileBeanAdapter.notifyDataSetChanged();
                    DemoDatabase.getInstance().getFileBeanDao().deleteAllData();
                    DemoDatabase.getInstance().getFileBeanDao().insertItems(excelfileBeans);
                    Toast.makeText(getActivity(), excelfileBeans.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void divideByBoxcode(List<FileBean> fileBeans) {
        multiDatas.clear();
        ArrayList<String> boxCodes = new ArrayList<>();
        for (FileBean fileBean : fileBeans) {
            if (!boxCodes.contains(fileBean.getBoxCode())) {
                boxCodes.add(fileBean.getBoxCode());
                multiDatas.add(new BoxBean(fileBean.getBoxCode(),false));
            }
        }
        multiAdapter.notifyDataSetChanged();
    }

    public String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"content***"+uri.toString());
            return getDataColumn(context, uri, null, null);
        }
        // Files
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"file***"+uri.toString());
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void divideByBag(List<FileBean> fileBeans) {
        bagMap.clear();
        epcFileMap.clear();
        currentFileList.clear();
        currentEpcList.clear();
        currentInvedList.clear();
        //按照封袋编号划分档案
        for (FileBean fileBean : fileBeans) {
            if (!bagMap.containsKey(fileBean.getBagCode())) {
                ArrayList<FileBean> oneBeans = new ArrayList<>();
                oneBeans.add(fileBean);
                bagMap.put(fileBean.getBagCode(), oneBeans);
            } else {
                ArrayList<FileBean> twoBeans = bagMap.get(fileBean.getBagCode());
                twoBeans.add(fileBean);
            }
            currentEpcList.add(fileBean.getEpcCode());
        }
        Set<Map.Entry<String, ArrayList<FileBean>>> entries = bagMap.entrySet();
        for (Map.Entry<String, ArrayList<FileBean>> entry : entries) {
            FileBean toBean = new FileBean();
            FileBean fromBean = entry.getValue().get(0);
            copyFileBean(fromBean, toBean);
            for (FileBean fileBean : entry.getValue()) {
                toBean.getEpcs().add(new EpcBean(fileBean.getEpcCode()));
            }
            currentFileList.add(toBean);
            epcFileMap.put(toBean.getEpcCode(), toBean);
        }
        selectFileNum.setText(String.valueOf(fileBeans.size()));
        invNum.setText("0");
    }

    public void copyFileBean(FileBean fromBean, FileBean toBean) {
        toBean.setEpcCode(fromBean.getEpcCode().split("\\|")[0]);
        toBean.setBatchCode(fromBean.getBatchCode());
        toBean.setStartDate(fromBean.getStartDate());
        toBean.setEndDate(fromBean.getEndDate());
        toBean.setBagSealDate(fromBean.getBagSealDate());
        toBean.setBoxSealDate(fromBean.getBoxSealDate());
        toBean.setInHouseDate(fromBean.getInHouseDate());
        toBean.setOutHouseDate(fromBean.getOutHouseDate());
        toBean.setDestoryDate(fromBean.getDestoryDate());
        toBean.setBagCode(fromBean.getBagCode());
        toBean.setBarNumber(fromBean.getBarNumber());
        toBean.setRegisterCode(fromBean.getRegisterCode());
        toBean.setOrgName(fromBean.getOrgName());
        toBean.setFileType(fromBean.getFileType());
        toBean.setFileName(fromBean.getFileName());
        toBean.setFileNumber(fromBean.getFileNumber());
        toBean.setAreaCode(fromBean.getAreaCode());
        toBean.setShelfCode(fromBean.getShelfCode());
        toBean.setShelfFloorCode(fromBean.getShelfFloorCode());
        toBean.setShelfColumCode(fromBean.getShelfColumCode());
        toBean.setFileNumber(fromBean.getFileNumber());
        toBean.setBoxCode(fromBean.getBoxCode());
    }

    @Override
    public void onItemClick(FileBean fileBean) {
        tvBagCode.setText("封袋编号： " + fileBean.getBagCode());
        String epc = "";
        String isInved = "";
        for (EpcBean fileBeanEpc : fileBean.getEpcs()) {
            if (fileBeanEpc.isInved()) {
                isInved = "已盘";
            } else {
                isInved = "未盘";
            }
            epc = epc + "EPC     " + fileBeanEpc.getEpc() + "     " + isInved + "\n";
        }
        //tvEpc.setText(epc);
        epcBeans.clear();
        epcBeans.addAll(fileBean.getEpcs());
        epcBeanAdapter.notifyDataSetChanged();
        showEpcDialog();
    }

    public void showEpcDialog() {
        if (updateDialog != null) {
            updateDialog.show();
        } else {
            updateDialog = new MaterialDialog.Builder(getActivity())
                    .customView(contentView, false)
                    .show();
            Window window = updateDialog.getWindow();
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onEpcItemClick(EpcBean fileBean) {
        Application.locateTag = asciiToHex(fileBean.getEpc());
        updateDialog.dismiss();
    }
}
