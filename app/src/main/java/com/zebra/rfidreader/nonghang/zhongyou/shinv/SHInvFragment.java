package com.zebra.rfidreader.nonghang.zhongyou.shinv;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.multilevel.treelist.Node;
import com.multilevel.treelist.TreeRecyclerAdapter;
import com.zebra.rfidreader.nonghang.R;
import com.zebra.rfidreader.nonghang.application.Application;
import com.zebra.rfidreader.nonghang.common.ResponseHandlerInterfaces;
import com.zebra.rfidreader.nonghang.home.MainActivity;
import com.zebra.rfidreader.nonghang.inventory.InventoryListItem;
import com.zebra.rfidreader.nonghang.nongshanghang.datebase.DemoDatabase;
import com.zebra.rfidreader.nonghang.zhongyou.model.AreaBean;
import com.zebra.rfidreader.nonghang.zhongyou.model.InvBean;
import com.zebra.rfidreader.nonghang.zhongyou.model.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SHInvFragment extends Fragment implements ResponseHandlerInterfaces.ResponseTagHandler, ResponseHandlerInterfaces.TriggerEventHandler {
    private static String TAG = "SHInvFragment";
    private final static String patternRegx = ".*?\\|.*?\\|(.*?)\\|\\|\\|(.*?)\\|.*?\\|.*?\\|.*?\\|.*?\\|.*?\\|.*?";
    private final static Pattern pattern = Pattern.compile(patternRegx);
    private FloatingActionButton inventoryButton;
    private Button chooseAreas;
    private TextView allNum;
    private TextView wrongNum;
    private CheckBox locCb;
    private EditText fileName;
    private List<AreaBean> areas = new ArrayList<>();
    private View multiContentView;
    private MaterialDialog multipleDialog;
    private RecyclerView multiRecycle;
    protected List<Node> multiDatas = new ArrayList<>();
    private HashMap<String, Node> idAndNodes = new HashMap<>();
    private TreeRecyclerAdapter multiAdapter;
    private ArrayList<String> selectAreas = new ArrayList<>();
    private ArrayList<InvBean> invWrongBeans = new ArrayList<>();
    private ArrayList<InvBean> invAllBeans = new ArrayList<>();
    private RecyclerView areaRecycle;
    private AreaBeanAdapter areaBeanAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initData() {
        new AsyncTask<Void, Void, List<AreaBean>>() {
            @Override
            protected List<AreaBean> doInBackground(Void... voids) {
                List<AreaBean> allAreaBeans = DemoDatabase.getInstance().getAreaBeanDao().getAllAreaBeans();
                return allAreaBeans;
            }

            @Override
            protected void onPostExecute(List<AreaBean> allAreaBeans) {
                super.onPostExecute(allAreaBeans);
                areas.clear();
                areas.addAll(allAreaBeans);
                multiDatas.add(new Node("-1", "-100", "全部"));
                for (AreaBean area : areas) {
                    Node node = new Node(area.getAreaId(), area.getSuperAreaId(), area.getAreaName());
                    multiDatas.add(node);
                    idAndNodes.put(area.getAreaId(), node);
                }
                multiAdapter.addData(multiDatas);
            }
        }.execute();

    }

    public static SHInvFragment newInstance() {
        return new SHInvFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_zhongyou_inv, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        allNum = (TextView) getActivity().findViewById(R.id.all_num);
        wrongNum = (TextView) getActivity().findViewById(R.id.error_num);
        chooseAreas = (Button) getActivity().findViewById(R.id.bt_choose_area);
        locCb = (CheckBox) getActivity().findViewById(R.id.cb_loc);
        fileName = (EditText) getActivity().findViewById(R.id.file_code);
        inventoryButton = (FloatingActionButton) getActivity().findViewById(R.id.inventoryButton);
        areaRecycle = (RecyclerView) getActivity().findViewById(R.id.file_recycle);
        areaBeanAdapter = new AreaBeanAdapter(invWrongBeans, getActivity());
        areaRecycle = (RecyclerView) getActivity().findViewById(R.id.file_recycle);
        areaRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        areaRecycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        areaRecycle.setAdapter(areaBeanAdapter);
        initMultiDialogView();
        initData();
        chooseAreas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultipleDialog();
            }
        });
        locCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    invWrongBeans.clear();
                    areaBeanAdapter.notifyDataSetChanged();
                    allNum.setText("0");
                    wrongNum.setText("0");
                }
            }
        });
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
        multiContentView = LayoutInflater.from(getActivity()).inflate(R.layout.multiple_choice_area_dialog, null);
        TextView tvSubmit = (TextView) multiContentView.findViewById(R.id.tv_finish);
        TextView tvCancel = (TextView) multiContentView.findViewById(R.id.tv_cancle);
        multiRecycle = (RecyclerView) multiContentView.findViewById(R.id.multi_recycle);
        multiRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        multiAdapter = new SimpleTreeRecyclerAdapter(multiRecycle, getActivity(),
                multiDatas, 1, R.drawable.tree_ex, R.drawable.tree_ec);
        multiRecycle.setAdapter(multiAdapter);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Node> allNodes = multiAdapter.getAllNodes();
                selectAreas.clear();
                for (Node node : allNodes) {
                    if (node.isChecked()) {
                        selectAreas.add((String) node.getId());
                    }
                }
                invWrongBeans.clear();
                invAllBeans.clear();
                allNum.setText("0");
                wrongNum.setText("0");
                areaBeanAdapter.notifyDataSetChanged();
                multipleDialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleDialog.dismiss();
            }
        });

    }

    @Override
    public void handleTagResponse(InventoryListItem inventoryListItem, boolean isAddedToList) {
        //hex
        String hexEpc = inventoryListItem.getTagID();
        //ascii
        String epcCipher = hex2ascii(hexEpc);
        /*String[] split = epc.split("\\|");
        String epcHead = split[0];*/
        if (epcCipher.length() >= 60) {
            List<Integer> initList = new ArrayList<>();
            for (int i = 0; i < epcCipher.length() - 1; i = i + 2) {
                int i1 = Integer.parseInt(epcCipher.substring(i, i + 2), 16);
                initList.add(i1);
            }
            String decompress = LzwDecodeUtils.decompress(initList);
            Post post = new Post();
            post.setEpcCipher(epcCipher);
            post.setEpcPlant(decompress);
            Matcher matcher = pattern.matcher(decompress);
            if (matcher.find()) {
                post.setPostCode(matcher.group(1));
                post.setPostalcode(matcher.group(2));
            }
            String postaLcode = post.getPostalcode();
            Node invNode = idAndNodes.get(postaLcode);
            if (invNode != null) {
                String ezName = fileName.getText().toString();
                if (locCb.isChecked() ) {
                    if( ezName.length() > 0){
                        if (ezName.equals(post.getPostCode())) {
                            String epcCipher1 = post.getEpcCipher();
                            if(epcCipher1.length() > 64){
                                epcCipher1 = epcCipher1.substring(0,64);
                            }
                            Application.locateTag = epcCipher1;
                            InvBean invBean = new InvBean(invNode.getName(), post.getEpcPlant());
                            if (!invWrongBeans.contains(invBean)) {
                                invWrongBeans.add(invBean);
                                areaBeanAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    //盘点到所有的数据
                    InvBean invBean = new InvBean(invNode.getName(), post.getEpcPlant());
                    if (!invAllBeans.contains(invBean)) {
                        invAllBeans.add(invBean);
                        allNum.setText(String.valueOf(invAllBeans.size()));
                    }
                    //盘点到的异常数据
                    if (!selectAreas.contains(postaLcode)) {
                        if (!invWrongBeans.contains(invBean)) {
                            invWrongBeans.add(invBean);
                            wrongNum.setText(String.valueOf(invWrongBeans.size()));
                            areaBeanAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            //盘点到的异常数据
           /* if (!selectAreas.contains(postaLcode)) {
                Node node = idAndNodes.get(postaLcode);
                if (node != null) {
                    InvBean invBean = new InvBean(node.getName(), post.getEpcPlant());
                    if (!invWrongBeans.contains(invBean)) {
                        invWrongBeans.add(invBean);
                        areaBeanAdapter.notifyDataSetChanged();
                    }

                }
            }*/
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

    public boolean isInteger(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
