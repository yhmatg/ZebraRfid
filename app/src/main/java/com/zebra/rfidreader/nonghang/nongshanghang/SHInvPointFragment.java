package com.zebra.rfidreader.nonghang.nongshanghang;

import android.os.Bundle;
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
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.multilevel.treelist.Node;
import com.multilevel.treelist.TreeRecyclerAdapter;
import com.zebra.rfidreader.nonghang.R;
import com.zebra.rfidreader.nonghang.application.Application;
import com.zebra.rfidreader.nonghang.common.ResponseHandlerInterfaces;
import com.zebra.rfidreader.nonghang.home.MainActivity;
import com.zebra.rfidreader.nonghang.inventory.InventoryListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SHInvPointFragment extends Fragment implements ResponseHandlerInterfaces.ResponseTagHandler, ResponseHandlerInterfaces.TriggerEventHandler, FileBeanAdapter.OnItemClickListener, EpcBeanAdapter.OnItemClickListener {
    private Button btWrite;
    private Button chooseBox;
    ArrayList<FileBean> currentFileList = new ArrayList<>();
    ArrayList<FileBean> currentInvedFileList = new ArrayList<>();
    private FloatingActionButton inventoryButton;
    private MaterialDialog multipleDialog;
    private View multiContentView;
    private RecyclerView multiRecycle;
    private RecyclerView fileRecycle;
    protected List<Node> multiDatas = new ArrayList<>();
    private TreeRecyclerAdapter multiAdapter;
    //没有按照封袋划分的文件集合
    private List<FileBean> excelfileBeans = new ArrayList<>();
    //按照封袋划分的文件集合
    private List<FileBean> bagFileBeans = new ArrayList<>();
    private FileBeanAdapter fileBeanAdapter;
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
    private TextView bookNum;
    private TextView invedBookNum;
    private List<String> currentEpcs = new ArrayList<>();
    private List<String> currentInvedEpcs = new ArrayList<>();

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

    public static SHInvPointFragment newInstance() {
        return new SHInvPointFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shanghang, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btWrite = (Button) getActivity().findViewById(R.id.bt_write);
        chooseBox = (Button) getActivity().findViewById(R.id.bt_choose_box);
        inventoryButton = (FloatingActionButton) getActivity().findViewById(R.id.inventoryButton);
        selectFileNum = (TextView) getActivity().findViewById(R.id.file_num);
        invNum = (TextView) getActivity().findViewById(R.id.inv_num);
        bookNum = (TextView) getActivity().findViewById(R.id.book_num);
        invedBookNum = (TextView) getActivity().findViewById(R.id.inved_book_num);

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
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");
                    Date d = new Date(System.currentTimeMillis());
                    String dataStr = sdf.format(d);
                    ExcelUtils.writeExcel(getContext(), writeBeans,  dataStr + ".xls");
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
        fileBeanAdapter = new FileBeanAdapter(currentFileList, getActivity());
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
        multiAdapter = new SimpleTreeRecyclerAdapter(multiRecycle, getActivity(),
                multiDatas, 2, R.drawable.tree_ex, R.drawable.tree_ec);
        multiRecycle.setAdapter(multiAdapter);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Node> allNodes = multiAdapter.getAllNodes();
                selectBoxs.clear();
                for (Node node : allNodes) {
                    if (node.isChecked()) {
                        selectBoxs.add(node.getName());
                    }
                }
                List<FileBean> fileBeansByBoxCode = DemoDatabase.getInstance().getFileBeanDao().getFileBeansByBoxCode(selectBoxs);
                divideByBag(fileBeansByBoxCode);
                fileBeanAdapter.notifyDataSetChanged();
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
        /*String[] split = epc.split("\\|");
        String epcHead = split[0];*/
        if(epc.length() >= 14){
            String epcHead = epc.substring(0,14);
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
                if(!currentInvedFileList.contains(fileBean)){
                    currentInvedFileList.add(fileBean);
                }
                fileBeanAdapter.notifyDataSetChanged();
               /* int invedNum = 0;
                for (FileBean bean : currentFileList) {
                    if(bean.getInvStatus()){
                        invedNum++;
                    }
                }
                invNum.setText(String.valueOf(invedNum));*/
                invNum.setText(String.valueOf(currentInvedFileList.size()));
                if(currentEpcs.contains(epc) && !currentInvedEpcs.contains(epc)){
                    currentInvedEpcs.add(epc);
                }
                invedBookNum.setText(String.valueOf(currentInvedEpcs.size()));
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

    private void divideByBoxcode(List<FileBean> fileBeans) {
        multiAdapter.removeData(multiDatas);
        multiDatas.clear();
        multiDatas.add(new Node("100", "-1", "全部"));
        ArrayList<String> boxCodes = new ArrayList<>();
        for (FileBean fileBean : fileBeans) {
            if (!boxCodes.contains(fileBean.getBoxCode())) {
                boxCodes.add(fileBean.getBoxCode());
                multiDatas.add(new Node(fileBean.getBoxCode(), "100", fileBean.getBoxCode()));
            }
        }
        multiAdapter.addData(multiDatas);
    }

    public void divideByBag(List<FileBean> fileBeans) {
        bagMap.clear();
        epcFileMap.clear();
        currentFileList.clear();
        currentEpcs.clear();
        currentInvedEpcs.clear();
        currentInvedFileList.clear();
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
            currentEpcs.add(fileBean.getEpcCode());
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
        selectFileNum.setText(String.valueOf(currentFileList.size()));
        invNum.setText("0");
        invedBookNum.setText("0");
        bookNum.setText(String.valueOf(fileBeans.size()));
    }

    public void copyFileBean(FileBean fromBean, FileBean toBean) {
        //toBean.setEpcCode(fromBean.getEpcCode().split("\\|")[0]);
        toBean.setEpcCode(fromBean.getEpcCode().substring(0,14));
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