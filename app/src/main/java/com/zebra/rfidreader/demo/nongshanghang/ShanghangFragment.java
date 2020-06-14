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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.multilevel.treelist.Node;
import com.multilevel.treelist.TreeRecyclerAdapter;
import com.zebra.rfidreader.demo.R;
import com.zebra.rfidreader.demo.application.Application;
import com.zebra.rfidreader.demo.common.ResponseHandlerInterfaces;
import com.zebra.rfidreader.demo.home.MainActivity;
import com.zebra.rfidreader.demo.inventory.InventoryFragment;
import com.zebra.rfidreader.demo.inventory.InventoryListItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ShanghangFragment extends Fragment implements ResponseHandlerInterfaces.ResponseTagHandler, ResponseHandlerInterfaces.TriggerEventHandler {
    private Button btRead;
    private Button btWrite;
    private Button chooseBox;
    ArrayList<FileBean> currentFileList = new ArrayList<>();
    private FloatingActionButton inventoryButton;
    private MaterialDialog multipleDialog;
    private View multiContentView;
    private RecyclerView multiRecycle;
    private RecyclerView fileRecycle;
    protected List<Node> multiDatas = new ArrayList<>();
    private TreeRecyclerAdapter multiAdapter;
    private List<FileBean> excelfileBeans = new ArrayList<>();
    private FileBeanAdapter fileBeanAdapter;
    private HashSet<String> selectBoxs = new HashSet<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initData() {
        List<FileBean> allFileBeans = DemoDatabase.getInstance().getFileBeanDao().getAllFileBeans();
        excelfileBeans.clear();
        excelfileBeans.addAll(allFileBeans);
        divideByBoxcode(excelfileBeans);
        currentFileList.clear();
        currentFileList.addAll(excelfileBeans);
        fileBeanAdapter.notifyDataSetChanged();
    }

    public static ShanghangFragment newInstance() {
        return new ShanghangFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shanghang, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btRead = (Button) getActivity().findViewById(R.id.bt_read);
        btWrite = (Button) getActivity().findViewById(R.id.bt_write);
        chooseBox = (Button) getActivity().findViewById(R.id.bt_choose_box);
        inventoryButton = (FloatingActionButton) getActivity().findViewById(R.id.inventoryButton);
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
                try {
                    ExcelUtils.writeExcel(getContext(),excelfileBeans,"excelTest03.xls");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        chooseBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultipleDialog();
            }
        });
        fileBeanAdapter = new FileBeanAdapter(currentFileList,getActivity());
        fileRecycle = (RecyclerView)getActivity().findViewById(R.id.file_recycle);
        fileRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        fileRecycle.setAdapter(fileBeanAdapter);
        initMultiDialogView();
        initData();
    }

    public void showMultipleDialog(){
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

    public void initMultiDialogView(){
        multiContentView = LayoutInflater.from(getActivity()).inflate(R.layout.multiple_choice_dialog, null);
        TextView tvSubmit = (TextView) multiContentView.findViewById(R.id.tv_finish);
        TextView tvCancel = (TextView) multiContentView.findViewById(R.id.tv_cancle);
        multiRecycle = (RecyclerView) multiContentView.findViewById(R.id.multi_recycle);
        multiRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        multiAdapter = new SimpleTreeRecyclerAdapter(multiRecycle, getActivity(),
                multiDatas, 2,R.drawable.tree_ex,R.drawable.tree_ec);
        multiRecycle.setAdapter(multiAdapter);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Node> allNodes = multiAdapter.getAllNodes();
                selectBoxs.clear();
                for (Node node : allNodes) {
                    if(node.isChecked()){
                        selectBoxs.add(node.getName());
                    }
                }
                List<FileBean> fileBeansByBoxCode = DemoDatabase.getInstance().getFileBeanDao().getFileBeansByBoxCode(selectBoxs);
                currentFileList.clear();
                currentFileList.addAll(fileBeansByBoxCode);
                fileBeanAdapter.notifyDataSetChanged();
                if(multipleDialog != null){
                    multipleDialog.dismiss();
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multipleDialog != null){
                    multipleDialog.dismiss();
                }
            }
        });


    }

    @Override
    public void handleTagResponse(InventoryListItem inventoryListItem, boolean isAddedToList) {

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
     * */
    public void importExcle(Intent data){
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
                    int suffixIndex = strArray.length -1;
                    File dir = new File(upLoadFilePath);
                    //调用查询方法
                    //ExcelUtil.QueryUser(this, dir);
                    List<FileBean> fileBeans = ExcelUtils.read2DB(dir, getActivity());
                    excelfileBeans.clear();
                    excelfileBeans.addAll(fileBeans);
                    divideByBoxcode(excelfileBeans);
                    DemoDatabase.getInstance().getFileBeanDao().deleteAllData();
                    DemoDatabase.getInstance().getFileBeanDao().insertItems(excelfileBeans);
                    Toast.makeText(getActivity(), excelfileBeans.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void divideByBoxcode(List<FileBean> fileBeans) {
        multiAdapter.removeData(multiDatas);
        multiDatas.clear();
        multiDatas.add(new Node("100","-1","全部"));
        ArrayList<String> boxCodes = new ArrayList<>();
        for (FileBean fileBean : fileBeans) {
            if(!boxCodes.contains(fileBean.getBoxCode())){
                boxCodes.add(fileBean.getBoxCode());
                multiDatas.add(new Node(fileBean.getBoxCode(),"100",fileBean.getBoxCode()));
            }
        }
        multiAdapter.addData(multiDatas);
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
            }
            else if (isDownloadsDocument(uri)) {
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



}
