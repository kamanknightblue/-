package com.wei2.pager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.bikenavi.params.BikeRouteNodeInfo;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo;
import com.wei2.bikenavi.BNaviGuideActivity;
import com.wei2.bikenavi.R;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.wei2.bikenavi.WNaviGuideActivity;
import com.wei2.utils.GetRandomMatchCodeUtils;
import com.wei2.utils.LogUtils;
import com.wei2.utils.NetUtils;
import com.wei2.utils.StreamToStringUtils;
import com.wei2.utils.overlayutil.BikingRouteOverlay;
import com.wei2.utils.overlayutil.DrivingRouteOverlay;
import com.wei2.utils.overlayutil.WalkingRouteOverlay;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class ProtectionPager {
    public final String TAG = this.getClass().getSimpleName();
    public Activity mActivity;
    /**
     * 当前页面的标题
     */
    public TextView tvTitle;
    public FrameLayout flContent;
    public final View mRootView;
    /**
     * 地图将会更新到的状态
     */
    private MapStatusUpdate mMapStatusUpdate;
    /**
     * 我的当前位置信息
     */
    public static MyLocationData myLocationData;

    /**
     * 定位数据
     */
    public static BDLocation bdLocation;


    /**
     * 我的位置配对码
     */
    private String mMyLocationKey;
    /**
     * 是否已在上传位置的状态
     */
    private boolean isUploading = false;
    /**
     * 上传位置的定位器
     */
    private Timer mTimerForUploading;
    /**
     * 上传位置的按钮
     * 创建：在initView中
     */
    private TextView mBtnUploadLocatioin;
    /**
     * 我的配对码
     */
    String mMyKey;



    /**
     * 地图图层0
     */
    private MapView mMapView0;
    /**
     * 与地图图层0对应的TextView
     */
    private TextView mTextViewOfMap0;
    /**
     * 地图图层0是否在监听中
     */
    private boolean isListenerOfMap0 = false;
    /**
     * 地图图层0的计时器
     */
    private Timer mTimerOfMap0;
    /**
     * 地图图层0获取的经纬度信息
     */
    private LatLng mLatLngForMap0;
    /**
     * 地图图层0控制器
     */
    private BaiduMap mBaiduMapOfMap0;
    /**
     * 地图图层0的弹出菜单
     */
    View mPopOfMap0;
    /**
     * 地图图层0上的人名
     */
    private String mNamoOfMap0;
    /**
     * 地图图层0的标志覆盖物
     */
    private Marker mMarkerOfMap0;
    /**
     * 地图0的路线规划结果
     */
    ListView mLvRouteResultOfMap0;
    /**
     * 地图0的驾车覆盖物
     */
    private DrivingRouteOverlay mDrivingRouteOverlayOfMap0;
    /**
     * 地图图层0步行覆盖物
     */
    WalkingRouteOverlay mWalkingRouteOverlayOfMap0;
    /**
     * 地图0的骑行覆盖物
     */
    BikingRouteOverlay mBikingRouteOverlayOfMap0;
    /**
     * 地图0的出行方式对话框
     */
    private AlertDialog mSelectTripModeDialogOfMap0;
    /**
     * 地图0路线搜索结果对话框
     */
    private AlertDialog mDialogRouteResultOfMap0;
    /**
     * 显示位置点详细信息对话框
     */
    private AlertDialog mDetailDialogOfMap0;
    /**
     * 地图0的关闭按钮
     */
    Button mButtonColseOfMap0;
    /**
     * 地图0的定位按钮
     */
    private Button mButtionLocateOfMap0;
    /**
     * 被监护人的位置描述
     */
    String mAddressStrOfMap0;
    /**
     * 纬度
     */
    private double mLatOfMap0;
    /**
     * 经度
     */
    private double mLngOfMap0;
    /**
     * 路线搜索工具
     */
    RoutePlanSearch mRoutePlanSearch;

    /**
     * 地图图层1
     */
    private MapView mMapView1;
    /**
     * 与地图图层1对应的TextView
     */
    private TextView mTextViewOfMap1;
    /**
     * 地图图层1是否在监听中
     */
    private boolean isListenerOfMap1 = false;
    /**
     * 地图图层1的计时器
     */
    private Timer mTimerOfMap1;
    /**
     * 地图图层1获取的经纬度信息
     */
    private LatLng mLatLngForMap1;
    /**
     * 地图图层1控制器
     */
    private BaiduMap mBaiduMapOfMap1;
    /**
     * 地图图层1的弹出菜单
     */
    View mPopOfMap1;
    /**
     * 地图图层1上的人名
     */
    private String mNamoOfMap1;
    /**
     * 地图图层1的标志覆盖物
     */
    private Marker mMarkerOfMap1;
    /**
     * 地图1的路线规划结果
     */
    ListView mLvRouteResultOfMap1;
    /**
     * 地图1的驾车覆盖物
     */
    private DrivingRouteOverlay mDrivingRouteOverlayOfMap1;
    /**
     * 地图图层1步行覆盖物
     */
    WalkingRouteOverlay mWalkingRouteOverlayOfMap1;
    /**
     * 地图1的骑行覆盖物
     */
    BikingRouteOverlay mBikingRouteOverlayOfMap1;
    /**
     * 地图1的出行方式对话框
     */
    private AlertDialog mSelectTripModeDialogOfMap1;
    /**
     * 地图1路线搜索结果对话框
     */
    private AlertDialog mDialogRouteResultOfMap1;
    /**
     * 显示位置点详细信息对话框
     */
    private AlertDialog mDetailDialogOfMap1;
    /**
     * 地图1的关闭按钮
     */
    Button mButtonColseOfMap1;
    /**
     * 地图1的定位按钮
     */
    private Button mButtionLocateOfMap1;
    /**
     * 被监护人的位置描述
     */
    String mAddressStrOfMap1;
    /**
     * 纬度
     */
    private double mLatOfMap1;
    /**
     * 经度
     */
    private double mLngOfMap1;


    /**
     * 地图图层2
     */
    private MapView mMapView2;
    /**
     * 与地图图层2对应的TextView
     */
    private TextView mTextViewOfMap2;
    /**
     * 地图图层2是否在监听中
     */
    private boolean isListenerOfMap2 = false;
    /**
     * 地图图层2的计时器
     */
    private Timer mTimerOfMap2;
    /**
     * 地图图层2获取的经纬度信息
     */
    private LatLng mLatLngForMap2;
    /**
     * 地图图层2控制器
     */
    private BaiduMap mBaiduMapOfMap2;
    /**
     * 地图图层2的弹出菜单
     */
    View mPopOfMap2;
    /**
     * 地图图层2上的人名
     */
    private String mNamoOfMap2;
    /**
     * 地图图层2的标志覆盖物
     */
    private Marker mMarkerOfMap2;
    /**
     * 地图2的路线规划结果
     */
    ListView mLvRouteResultOfMap2;
    /**
     * 地图2的驾车覆盖物
     */
    private DrivingRouteOverlay mDrivingRouteOverlayOfMap2;
    /**
     * 地图图层2步行覆盖物
     */
    WalkingRouteOverlay mWalkingRouteOverlayOfMap2;
    /**
     * 地图2的骑行覆盖物
     */
    BikingRouteOverlay mBikingRouteOverlayOfMap2;
    /**
     * 地图2的出行方式对话框
     */
    private AlertDialog mSelectTripModeDialogOfMap2;
    /**
     * 地图2路线搜索结果对话框
     */
    private AlertDialog mDialogRouteResultOfMap2;
    /**
     * 显示位置点详细信息对话框
     */
    private AlertDialog mDetailDialogOfMap2;
    /**
     * 地图2的关闭按钮
     */
    Button mButtonColseOfMap2;
    /**
     * 地图2的定位按钮
     */
    private Button mButtionLocateOfMap2;
    /**
     * 被监护人的位置描述
     */
    String mAddressStrOfMap2;
    /**
     * 纬度
     */
    private double mLatOfMap2;
    /**
     * 经度
     */
    private double mLngOfMap2;


    /**
     * 地图图层3
     */
    private MapView mMapView3;
    /**
     * 与地图图层3对应的TextView
     */
    private TextView mTextViewOfMap3;
    /**
     * 地图图层3是否在监听中
     */
    private boolean isListenerOfMap3 = false;
    /**
     * 地图图层3的计时器
     */
    private Timer mTimerOfMap3;
    /**
     * 地图图层3获取的经纬度信息
     */
    private LatLng mLatLngForMap3;
    /**
     * 地图图层3控制器
     */
    private BaiduMap mBaiduMapOfMap3;
    /**
     * 地图图层3的弹出菜单
     */
    View mPopOfMap3;
    /**
     * 地图图层3上的人名
     */
    private String mNamoOfMap3;
    /**
     * 地图图层3的标志覆盖物
     */
    private Marker mMarkerOfMap3;
    /**
     * 地图3的路线规划结果
     */
    ListView mLvRouteResultOfMap3;
    /**
     * 地图3的驾车覆盖物
     */
    private DrivingRouteOverlay mDrivingRouteOverlayOfMap3;
    /**
     * 地图图层3步行覆盖物
     */
    WalkingRouteOverlay mWalkingRouteOverlayOfMap3;
    /**
     * 地图3的骑行覆盖物
     */
    BikingRouteOverlay mBikingRouteOverlayOfMap3;
    /**
     * 地图3的出行方式对话框
     */
    private AlertDialog mSelectTripModeDialogOfMap3;
    /**
     * 地图3路线搜索结果对话框
     */
    private AlertDialog mDialogRouteResultOfMap3;
    /**
     * 显示位置点详细信息对话框
     */
    private AlertDialog mDetailDialogOfMap3;
    /**
     * 地图3的关闭按钮
     */
    Button mButtonColseOfMap3;
    /**
     * 地图3的定位按钮
     */
    private Button mButtionLocateOfMap3;
    /**
     * 被监护人的位置描述
     */
    String mAddressStrOfMap3;
    /**
     * 纬度
     */
    private double mLatOfMap3;
    /**
     * 经度
     */
    private double mLngOfMap3;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.obj.toString().equals("未连接到服务器")) {
                Toast.makeText(mActivity, "未连接到服务器", Toast.LENGTH_LONG).show();
            }
            if (msg.obj.toString().equals("未连接到服务器，上传失败")) {
                Toast.makeText(mActivity, "未连接到服务器，上传失败", Toast.LENGTH_LONG).show();
                isUploading = false;
                mTimerForUploading.cancel();
            }
        }
    };


    public ProtectionPager(Activity activity) {
        mActivity = activity;
        mRootView = initView();
        initData();
        initListener();
        initListenerForMap1();
        initListenerForMap2();
        initListenerForMap3();
    }

    /**
     * 初始化View
     *
     * @return
     */
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_protection, null);

        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("家人监护");
        flContent = view.findViewById(R.id.fl_content);
        //上传位置的按钮
        mBtnUploadLocatioin = view.findViewById(R.id.btn_upload_locatioin);

        //初始化地图图层0
        mMapView0 = view.findViewById(R.id.bmapView0);
        //初始化地图图层0对应的TextView
        mTextViewOfMap0 = view.findViewById(R.id.tv_0);
        //地图0对应关闭按钮
        mButtonColseOfMap0 = view.findViewById(R.id.btn_close_map0);
        //地图0定位按钮
        mButtionLocateOfMap0 = view.findViewById(R.id.btn_locate_map0);


        //初始化地图图层1
        mMapView1 = view.findViewById(R.id.bmapView1);
        //初始化地图图层1对应的TextView
        mTextViewOfMap1 = view.findViewById(R.id.tv_1);
        //地图1对应关闭按钮
        mButtonColseOfMap1 = view.findViewById(R.id.btn_close_map1);
        //地图1定位按钮
        mButtionLocateOfMap1 = view.findViewById(R.id.btn_locate_map1);

        //初始化地图图层2
        mMapView2 = view.findViewById(R.id.bmapView2);
        //初始化地图图层2对应的TextView
        mTextViewOfMap2 = view.findViewById(R.id.tv_2);
        //地图2对应关闭按钮
        mButtonColseOfMap2 = view.findViewById(R.id.btn_close_map2);
        //地图2定位按钮
        mButtionLocateOfMap2 = view.findViewById(R.id.btn_locate_map2);

        //初始化地图图层3
        mMapView3 = view.findViewById(R.id.bmapView3);
        //初始化地图图层3对应的TextView
        mTextViewOfMap3 = view.findViewById(R.id.tv_3);
        //地图3对应关闭按钮
        mButtonColseOfMap3 = view.findViewById(R.id.btn_close_map3);
        //地图3定位按钮
        mButtionLocateOfMap3 = view.findViewById(R.id.btn_locate_map3);

        return view;
    }

    /**
     * 初始化数据
     */
    public void initData() {

        //实例化地图0控制器
        mBaiduMapOfMap0 = mMapView0.getMap();
        //开启定位图层
        mBaiduMapOfMap0.setMyLocationEnabled(true);
        //先把关闭按钮隐藏
        mButtonColseOfMap0.setVisibility(View.INVISIBLE);
        //把定位按钮隐藏
        mButtionLocateOfMap0.setVisibility(View.INVISIBLE);
        //路线搜索工具初始化
        mRoutePlanSearch = RoutePlanSearch.newInstance();

        //实例化地图1控制器
        mBaiduMapOfMap1 = mMapView1.getMap();
        //开启定位图层
        mBaiduMapOfMap1.setMyLocationEnabled(true);
        //先把关闭按钮隐藏
        mButtonColseOfMap1.setVisibility(View.INVISIBLE);
        //把定位按钮隐藏
        mButtionLocateOfMap1.setVisibility(View.INVISIBLE);


        //实例化地图2控制器
        mBaiduMapOfMap2 = mMapView2.getMap();
        //开启定位图层
        mBaiduMapOfMap2.setMyLocationEnabled(true);
        //先把关闭按钮隐藏
        mButtonColseOfMap2.setVisibility(View.INVISIBLE);
        //把定位按钮隐藏
        mButtionLocateOfMap2.setVisibility(View.INVISIBLE);

        //实例化地图3控制器
        mBaiduMapOfMap3 = mMapView3.getMap();
        //开启定位图层
        mBaiduMapOfMap3.setMyLocationEnabled(true);
        //先把关闭按钮隐藏
        mButtonColseOfMap3.setVisibility(View.INVISIBLE);
        //把定位按钮隐藏
        mButtionLocateOfMap3.setVisibility(View.INVISIBLE);


        //定位点配置
        MyLocationConfiguration myLocationConfiguration =
                new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                        true, BitmapDescriptorFactory.fromResource(R.mipmap.navigation_blue));


        //设置定时器，获取我的位置
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            //为第一次计时设置标识
            boolean isFirstTime = true;

            public void run() {
                //更新我的定位点数据
                myLocationData = LocationPager.mLocData;

                //更新定位数据
                bdLocation = LocationPager.bdLocation;
                if (bdLocation != null && myLocationData != null && isFirstTime) {
                    isFirstTime = false;
                }
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    /**
     * 初始化监听事件
     */
    public void initListener() {
        //位置上传按钮点击事件
        mBtnUploadLocatioin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //无网络状态不执行任何操作，直接return
                if (!NetUtils.networkConnected(mActivity)) {
                    Toast.makeText(mActivity, "无网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                //若正在上传位置弹出位置正在共享的提示
                if (isUploading) {
                    AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                            .setTitle("位置共享中")
                            .setMessage("我的配对码："+mMyKey)
                            .setPositiveButton("取消共享", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isUploading = false;
                                    mTimerForUploading.cancel();
                                }
                            })
                            .setNeutralButton("复制配对码到剪切板", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData mClipData = ClipData.newPlainText("Label", mMyKey);
                                    cm.setPrimaryClip(mClipData);
                                    Toast.makeText(mActivity,"复制成功",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create();
                    alertDialog.show();
                }
                //若没在共享位置需弹出是否上传位置的对话框
                else {
                    View view = View.inflate(mActivity, R.layout.dialog_upload_location, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                            .setTitle("上传位置")
                            .setView(view)
                            .create();
                    final EditText etServerAddress = view.findViewById(R.id.et_server_address);
                    final EditText etSetKey = view.findViewById(R.id.et_setkey);
                    mMyKey = GetRandomMatchCodeUtils.generate();
                    etSetKey.setText(mMyKey);
                    Button bu = view.findViewById(R.id.btn_submitkey);
                    alertDialog.show();
                    bu.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {

                            if (etSetKey.getText().toString().trim() == null || etSetKey.getText().toString().isEmpty()) {
                                Toast.makeText(mActivity, "配对码不能为空", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if (bdLocation != null && (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161)) {
                                    uploadLocation(etServerAddress.getText().toString().trim(), mMyKey);
                                }
                                else {
                                    Toast.makeText(mActivity, "未获取到当前位置，上传失败", Toast.LENGTH_LONG).show();
                                }
                            }
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });

        //为地图图层0上的TextView设置监听，未处于正在监听弹出请输入信息的对话框，输入信息后获取位置
        mTextViewOfMap0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //无网络状态不执行任何操作，直接return
                if (!NetUtils.networkConnected(mActivity)) {
                    Toast.makeText(mActivity, "无网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                //若未处于正在监听时所执行的操作：弹出请输入信息的对话框
                View view = View.inflate(mActivity, R.layout.dialog_get_info, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("请输入信息")
                        .setView(view)
                        .create();
                //设置姓名的EditView
                final EditText et_name = view.findViewById(R.id.et_name);
                //设置配对码的EditView
                final EditText et_key = view.findViewById(R.id.et_key);
                //服务器地址
                final EditText et_address = view.findViewById(R.id.et_address_getlocation);
                //确认按钮
                Button btnConfirm = view.findViewById(R.id.btn_confirm);
                //展示对话框
                alertDialog.show();
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        //为地图图层0的人名赋值
                        mNamoOfMap0 = et_name.getText().toString().trim();
                        String key = et_key.getText().toString().trim();
                        if (mNamoOfMap0 == null || mNamoOfMap0.length() == 0 || key.isEmpty()) {
                            Toast.makeText(mActivity, "称呼或配对码不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mActivity, "正在获取位置", Toast.LENGTH_SHORT).show();
                            getLocationInfoForMap0(et_address.getText().toString().trim(), key);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        //为地图图层0设置图标点击事件
        mBaiduMapOfMap0.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            //图标上的小气泡标题
            TextView mTvPopTitle;
            //详情小图标
            ImageView ivDetail;
            //路线图标
            ImageView ivRoad;

            //标识：是否显示气泡的
            boolean toggle = true;

            public boolean onMarkerClick(Marker marker) {
                //为地图0上的被监听者图标设置点击事件，弹出气泡
                if (marker.getId() == mMarkerOfMap0.getId()) {
                    if (mPopOfMap0 == null) {
                        //实例化弹出气泡
                        mPopOfMap0 = View.inflate(mActivity, R.layout.pop, null);
                        MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                                .position(marker.getPosition())
                                .yOffset(-90)
                                .build();
                        //实例化气泡里的标题
                        mTvPopTitle = mPopOfMap0.findViewById(R.id.tv_title);
                        //设置标题文本内容
                        mTvPopTitle.setText(marker.getTitle());
                        //实例化气泡里的路线图标
                        ivRoad = mPopOfMap0.findViewById(R.id.iv_navigate_method);
                        //实例化气泡里的详情图标
                        ivDetail = mPopOfMap0.findViewById(R.id.iv_detail);
                        //将气泡加进地图0
                        mMapView0.addView(mPopOfMap0, params);
                        //详情图标监听
                        ivDetail.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                String mes[] = new String[]{"坐标类型：bd09", "纬度：" + mLatOfMap0 + "", "经度：" + mLngOfMap0 + "", "地址：" + mAddressStrOfMap0};
                                mDetailDialogOfMap0 = new AlertDialog.Builder(mActivity)
                                        .setItems(mes, null)
                                        .setTitle("相关信息")
                                        .create();
                                mDetailDialogOfMap0.show();
                            }
                        });
                        //路线图标设置事件：弹出出行方式选择的对话框
                        ivRoad.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //初始化对话框的view
                                View view = View.inflate(mActivity, R.layout.dialog_navigate_mode, null);
                                mSelectTripModeDialogOfMap0 = new AlertDialog.Builder(mActivity)
                                        .setTitle("请选择出行方式")
                                        .setView(view)
                                        .create();
                                mSelectTripModeDialogOfMap0.show();
                                //实例化步行图标
                                ImageView ivWalk = view.findViewById(R.id.iv_walk);
                                //实例化骑行图标
                                ImageView ivBike = view.findViewById(R.id.iv_bike);
                                //实例化驾车图标
                                ImageView ivCar = view.findViewById(R.id.iv_car);
                                //实例化公交图标
                                // ImageView ivBus = view.findViewById(R.id.iv_bus);
                                //步行图标设置监听：弹出搜索结果对话框
                                ivWalk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResultOfMap0 = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResultOfMap0 = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResultOfMap0.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.walkingSearch(new WalkingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(mLatLngForMap0)));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //骑行图标监听
                                ivBike.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResultOfMap0 = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResultOfMap0 = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResultOfMap0.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.bikingSearch(new BikingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(mLatLngForMap0)));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //驾车图标设置监听：弹出搜索结果对话框
                                ivCar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResultOfMap0 = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResultOfMap0 = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResultOfMap0.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.drivingSearch(new DrivingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(mLatLngForMap0)));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
//                                ivBus.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                    }
//                                });
                            }
                        });
                    } else {
                        if (toggle) {
                            toggle = false;
                            mPopOfMap0.setVisibility(View.INVISIBLE);
                        } else {
                            toggle = true;
                            mPopOfMap0.setVisibility(View.VISIBLE);
                        }
                        MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                                .position(marker.getPosition())
                                .yOffset(-90)
                                .build();
                        mMapView0.updateViewLayout(mPopOfMap0, params);
                    }
                    mTvPopTitle.setText(marker.getTitle());
                }
                return true;
            }
        });

        //地图0关闭按钮监听
        mButtonColseOfMap0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("是否取消监听")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isListenerOfMap0 = false;
                                mTextViewOfMap0.setVisibility(View.VISIBLE);
                                mTimerOfMap0.cancel();
                                mButtonColseOfMap0.setVisibility(View.INVISIBLE);
                                mButtionLocateOfMap0.setVisibility(View.INVISIBLE);
                                if (mMarkerOfMap0 != null) {
                                    mMarkerOfMap0.remove();
                                    mMarkerOfMap0 = null;

                                }
                                if (mPopOfMap0 != null) {
                                    mPopOfMap0.setVisibility(View.INVISIBLE);

                                }
                                mBaiduMapOfMap0.clear();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        //地图0定位按钮监听
        mButtionLocateOfMap0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMapOfMap0.clear();
                mMarkerOfMap0=null;
                if (mLatLngForMap0 != null) {
                    mBaiduMapOfMap0.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                            .target(mLatLngForMap0)
                            .zoom(19)
                            .overlook(0)
                            .build()));
                }
                if (mPopOfMap0 != null) {
                    mPopOfMap0.setVisibility(View.INVISIBLE);
                }
            }
        });



        //设置路线规划事件
        mRoutePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            //步行规划
            public void onGetWalkingRouteResult(final WalkingRouteResult walkingRouteResult) {
                if (walkingRouteResult != null && walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    if(mLvRouteResultOfMap0!=null){
                        mLvRouteResultOfMap0.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return walkingRouteResult.getRouteLines().size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = View.inflate(mActivity, R.layout.item_route_result, null);
                                TextView tvItemId = view.findViewById(R.id.tv_item_id);
                                TextView tvName = view.findViewById(R.id.tv_name);
                                TextView tvTime = view.findViewById(R.id.tv_time);
                                TextView tvDis = view.findViewById(R.id.tv_distance);

                                tvItemId.setText("方案" + (1 + position));
                                tvName.setText(walkingRouteResult.getRouteLines().get(position).getTitle());
                                tvTime.setText("时间：大约" + (walkingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                                tvDis.setText("路程：大约" + (walkingRouteResult.getRouteLines().get(position).getDistance() / 1000.0) + "公里");
                                return view;
                            }
                        });
                        //点击搜索结果项时在地图上绘制路线
                        mLvRouteResultOfMap0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //若已经画上了步行路线，需要清除再画
                                if (mWalkingRouteOverlayOfMap0 != null) {
                                    mWalkingRouteOverlayOfMap0.removeFromMap();
                                }
                                mBaiduMapOfMap0.clear();
                                mMarkerOfMap0 = null;
                                mWalkingRouteOverlayOfMap0 = new WalkingRouteOverlay(mBaiduMapOfMap0);
                                mBaiduMapOfMap0.setOnMarkerClickListener(mWalkingRouteOverlayOfMap0);
                                mWalkingRouteOverlayOfMap0.setData(walkingRouteResult.getRouteLines().get(position));
                                mWalkingRouteOverlayOfMap0.addToMap();
                                mWalkingRouteOverlayOfMap0.zoomToSpan();

                                mSelectTripModeDialogOfMap0.dismiss();
                                mDialogRouteResultOfMap0.dismiss();
                                final WalkNavigateHelper walkNavigateHelper = WalkNavigateHelper.getInstance();
                                walkNavigateHelper.initNaviEngine(mActivity, new IWEngineInitListener() {
                                    @Override
                                    public void engineInitSuccess() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化成功");
                                        AlertDialog inNaviDialog = new AlertDialog.Builder(mActivity)
                                                .setTitle("是否开始导航")
                                                .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        WalkRouteNodeInfo startPt = new WalkRouteNodeInfo();
                                                        WalkRouteNodeInfo endPt = new WalkRouteNodeInfo();
                                                        startPt.setLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                                        endPt.setLocation(mLatLngForMap0);
                                                        WalkNaviLaunchParam param = new WalkNaviLaunchParam()
                                                                .startNodeInfo(startPt)
                                                                .endNodeInfo(endPt)
                                                                .extraNaviMode(0);
                                                        WalkNavigateHelper.getInstance().routePlanWithRouteNode(param, new IWRoutePlanListener() {
                                                            @Override
                                                            public void onRoutePlanStart() {

                                                            }

                                                            @Override
                                                            public void onRoutePlanSuccess() {
                                                                LogUtils.e("步行导航执行结果", "成功");
                                                                Intent intent = new Intent(mActivity, WNaviGuideActivity.class);
                                                                mActivity.startActivity(intent);
                                                            }

                                                            @Override
                                                            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                                                                LogUtils.e("步行导航执行结果", "失败");
                                                                Toast.makeText(mActivity, "该路线不建议步行", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                })
                                                .create();
                                        inNaviDialog.show();
                                    }

                                    @Override
                                    public void engineInitFail() {

                                    }
                                });
                            }
                        });
                    }
                    if(mLvRouteResultOfMap1!=null){
                        mLvRouteResultOfMap1.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return walkingRouteResult.getRouteLines().size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = View.inflate(mActivity, R.layout.item_route_result, null);
                                TextView tvItemId = view.findViewById(R.id.tv_item_id);
                                TextView tvName = view.findViewById(R.id.tv_name);
                                TextView tvTime = view.findViewById(R.id.tv_time);
                                TextView tvDis = view.findViewById(R.id.tv_distance);

                                tvItemId.setText("方案" + (1 + position));
                                tvName.setText(walkingRouteResult.getRouteLines().get(position).getTitle());
                                tvTime.setText("时间：大约" + (walkingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                                tvDis.setText("路程：大约" + (walkingRouteResult.getRouteLines().get(position).getDistance() / 1000.0) + "公里");
                                return view;
                            }
                        });
                        //点击搜索结果项时在地图上绘制路线
                        mLvRouteResultOfMap1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //若已经画上了步行路线，需要清除再画
                                if (mWalkingRouteOverlayOfMap1 != null) {
                                    mWalkingRouteOverlayOfMap1.removeFromMap();
                                }
                                mBaiduMapOfMap1.clear();
                                mMarkerOfMap1 = null;
                                mWalkingRouteOverlayOfMap1 = new WalkingRouteOverlay(mBaiduMapOfMap1);
                                mBaiduMapOfMap1.setOnMarkerClickListener(mWalkingRouteOverlayOfMap1);
                                mWalkingRouteOverlayOfMap1.setData(walkingRouteResult.getRouteLines().get(position));
                                mWalkingRouteOverlayOfMap1.addToMap();
                                mWalkingRouteOverlayOfMap1.zoomToSpan();

                                mSelectTripModeDialogOfMap1.dismiss();
                                mDialogRouteResultOfMap1.dismiss();
                                final WalkNavigateHelper walkNavigateHelper = WalkNavigateHelper.getInstance();
                                walkNavigateHelper.initNaviEngine(mActivity, new IWEngineInitListener() {
                                    @Override
                                    public void engineInitSuccess() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化成功");
                                        AlertDialog inNaviDialog = new AlertDialog.Builder(mActivity)
                                                .setTitle("是否开始导航")
                                                .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        WalkRouteNodeInfo startPt = new WalkRouteNodeInfo();
                                                        WalkRouteNodeInfo endPt = new WalkRouteNodeInfo();
                                                        startPt.setLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                                        endPt.setLocation(mLatLngForMap1);
                                                        WalkNaviLaunchParam param = new WalkNaviLaunchParam()
                                                                .startNodeInfo(startPt)
                                                                .endNodeInfo(endPt)
                                                                .extraNaviMode(0);
                                                        WalkNavigateHelper.getInstance().routePlanWithRouteNode(param, new IWRoutePlanListener() {
                                                            @Override
                                                            public void onRoutePlanStart() {

                                                            }

                                                            @Override
                                                            public void onRoutePlanSuccess() {
                                                                LogUtils.e("步行导航执行结果", "成功");
                                                                Intent intent = new Intent(mActivity, WNaviGuideActivity.class);
                                                                mActivity.startActivity(intent);
                                                            }

                                                            @Override
                                                            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                                                                LogUtils.e("步行导航执行结果", "失败");
                                                                Toast.makeText(mActivity, "该路线不建议步行", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                })
                                                .create();
                                        inNaviDialog.show();
                                    }

                                    @Override
                                    public void engineInitFail() {

                                    }
                                });
                            }
                        });
                    }
                    if(mLvRouteResultOfMap2!=null){
                        mLvRouteResultOfMap2.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return walkingRouteResult.getRouteLines().size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = View.inflate(mActivity, R.layout.item_route_result, null);
                                TextView tvItemId = view.findViewById(R.id.tv_item_id);
                                TextView tvName = view.findViewById(R.id.tv_name);
                                TextView tvTime = view.findViewById(R.id.tv_time);
                                TextView tvDis = view.findViewById(R.id.tv_distance);

                                tvItemId.setText("方案" + (1 + position));
                                tvName.setText(walkingRouteResult.getRouteLines().get(position).getTitle());
                                tvTime.setText("时间：大约" + (walkingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                                tvDis.setText("路程：大约" + (walkingRouteResult.getRouteLines().get(position).getDistance() / 1000.0) + "公里");
                                return view;
                            }
                        });
                        //点击搜索结果项时在地图上绘制路线
                        mLvRouteResultOfMap2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //若已经画上了步行路线，需要清除再画
                                if (mWalkingRouteOverlayOfMap2 != null) {
                                    mWalkingRouteOverlayOfMap2.removeFromMap();
                                }
                                mBaiduMapOfMap2.clear();
                                mMarkerOfMap2 = null;
                                mWalkingRouteOverlayOfMap2 = new WalkingRouteOverlay(mBaiduMapOfMap2);
                                mBaiduMapOfMap2.setOnMarkerClickListener(mWalkingRouteOverlayOfMap2);
                                mWalkingRouteOverlayOfMap2.setData(walkingRouteResult.getRouteLines().get(position));
                                mWalkingRouteOverlayOfMap2.addToMap();
                                mWalkingRouteOverlayOfMap2.zoomToSpan();

                                mSelectTripModeDialogOfMap2.dismiss();
                                mDialogRouteResultOfMap2.dismiss();
                                final WalkNavigateHelper walkNavigateHelper = WalkNavigateHelper.getInstance();
                                walkNavigateHelper.initNaviEngine(mActivity, new IWEngineInitListener() {
                                    @Override
                                    public void engineInitSuccess() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化成功");
                                        AlertDialog inNaviDialog = new AlertDialog.Builder(mActivity)
                                                .setTitle("是否开始导航")
                                                .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        WalkRouteNodeInfo startPt = new WalkRouteNodeInfo();
                                                        WalkRouteNodeInfo endPt = new WalkRouteNodeInfo();
                                                        startPt.setLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                                        endPt.setLocation(mLatLngForMap2);
                                                        WalkNaviLaunchParam param = new WalkNaviLaunchParam()
                                                                .startNodeInfo(startPt)
                                                                .endNodeInfo(endPt)
                                                                .extraNaviMode(0);
                                                        WalkNavigateHelper.getInstance().routePlanWithRouteNode(param, new IWRoutePlanListener() {
                                                            @Override
                                                            public void onRoutePlanStart() {

                                                            }

                                                            @Override
                                                            public void onRoutePlanSuccess() {
                                                                LogUtils.e("步行导航执行结果", "成功");
                                                                Intent intent = new Intent(mActivity, WNaviGuideActivity.class);
                                                                mActivity.startActivity(intent);
                                                            }

                                                            @Override
                                                            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                                                                LogUtils.e("步行导航执行结果", "失败");
                                                                Toast.makeText(mActivity, "该路线不建议步行", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                })
                                                .create();
                                        inNaviDialog.show();
                                    }

                                    @Override
                                    public void engineInitFail() {

                                    }
                                });
                            }
                        });
                    }
                    if(mLvRouteResultOfMap3!=null){
                        mLvRouteResultOfMap3.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return walkingRouteResult.getRouteLines().size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = View.inflate(mActivity, R.layout.item_route_result, null);
                                TextView tvItemId = view.findViewById(R.id.tv_item_id);
                                TextView tvName = view.findViewById(R.id.tv_name);
                                TextView tvTime = view.findViewById(R.id.tv_time);
                                TextView tvDis = view.findViewById(R.id.tv_distance);

                                tvItemId.setText("方案" + (1 + position));
                                tvName.setText(walkingRouteResult.getRouteLines().get(position).getTitle());
                                tvTime.setText("时间：大约" + (walkingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                                tvDis.setText("路程：大约" + (walkingRouteResult.getRouteLines().get(position).getDistance() / 1000.0) + "公里");
                                return view;
                            }
                        });
                        //点击搜索结果项时在地图上绘制路线
                        mLvRouteResultOfMap3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //若已经画上了步行路线，需要清除再画
                                if (mWalkingRouteOverlayOfMap3 != null) {
                                    mWalkingRouteOverlayOfMap3.removeFromMap();
                                }
                                mBaiduMapOfMap3.clear();
                                mMarkerOfMap3 = null;
                                mWalkingRouteOverlayOfMap3 = new WalkingRouteOverlay(mBaiduMapOfMap3);
                                mBaiduMapOfMap3.setOnMarkerClickListener(mWalkingRouteOverlayOfMap3);
                                mWalkingRouteOverlayOfMap3.setData(walkingRouteResult.getRouteLines().get(position));
                                mWalkingRouteOverlayOfMap3.addToMap();
                                mWalkingRouteOverlayOfMap3.zoomToSpan();

                                mSelectTripModeDialogOfMap3.dismiss();
                                mDialogRouteResultOfMap3.dismiss();
                                final WalkNavigateHelper walkNavigateHelper = WalkNavigateHelper.getInstance();
                                walkNavigateHelper.initNaviEngine(mActivity, new IWEngineInitListener() {
                                    @Override
                                    public void engineInitSuccess() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化成功");
                                        AlertDialog inNaviDialog = new AlertDialog.Builder(mActivity)
                                                .setTitle("是否开始导航")
                                                .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        WalkRouteNodeInfo startPt = new WalkRouteNodeInfo();
                                                        WalkRouteNodeInfo endPt = new WalkRouteNodeInfo();
                                                        startPt.setLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                                        endPt.setLocation(mLatLngForMap3);
                                                        WalkNaviLaunchParam param = new WalkNaviLaunchParam()
                                                                .startNodeInfo(startPt)
                                                                .endNodeInfo(endPt)
                                                                .extraNaviMode(0);
                                                        WalkNavigateHelper.getInstance().routePlanWithRouteNode(param, new IWRoutePlanListener() {
                                                            @Override
                                                            public void onRoutePlanStart() {

                                                            }

                                                            @Override
                                                            public void onRoutePlanSuccess() {
                                                                LogUtils.e("步行导航执行结果", "成功");
                                                                Intent intent = new Intent(mActivity, WNaviGuideActivity.class);
                                                                mActivity.startActivity(intent);
                                                            }

                                                            @Override
                                                            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                                                                LogUtils.e("步行导航执行结果", "失败");
                                                                Toast.makeText(mActivity, "该路线不建议步行", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                })
                                                .create();
                                        inNaviDialog.show();
                                    }

                                    @Override
                                    public void engineInitFail() {

                                    }
                                });
                            }
                        });
                    }
                } else {
                    Toast.makeText(mActivity, "未搜索到结果", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            //获取驾车路线搜索结果，把结果添加在ListView里，并为每个item设置监听
            @Override
            public void onGetDrivingRouteResult(final DrivingRouteResult drivingRouteResult) {
                if (drivingRouteResult != null && drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    if (mLvRouteResultOfMap0 != null) {
                        mLvRouteResultOfMap0.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return drivingRouteResult.getRouteLines().size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = View.inflate(mActivity, R.layout.item_route_result, null);
                                TextView tvItemId = view.findViewById(R.id.tv_item_id);
                                TextView tvName = view.findViewById(R.id.tv_name);
                                TextView tvTime = view.findViewById(R.id.tv_time);
                                TextView tvDis = view.findViewById(R.id.tv_distance);

                                tvItemId.setText("方案" + (1 + position));
                                tvName.setText(drivingRouteResult.getRouteLines().get(position).getTitle());
                                tvTime.setText("时间：大约" + (drivingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                                tvDis.setText("路程：大约" + (drivingRouteResult.getRouteLines().get(position).getDistance() / 1000.0) + "公里");
                                return view;
                            }
                        });
                        //为地图图层0的路线搜索结果设置监听，点击结果项时在地图上绘制路线
                        mLvRouteResultOfMap0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //若已经画上了驾车路线，需要清除再画
                                if (mDrivingRouteOverlayOfMap0 != null) {
                                    mDrivingRouteOverlayOfMap0.removeFromMap();
                                }
                                mBaiduMapOfMap0.clear();
                                mMarkerOfMap0 = null;
                                mDrivingRouteOverlayOfMap0 = new DrivingRouteOverlay(mBaiduMapOfMap0);
                                mBaiduMapOfMap0.setOnMarkerClickListener(mDrivingRouteOverlayOfMap0);
                                mDrivingRouteOverlayOfMap0.setData(drivingRouteResult.getRouteLines().get(position));
                                mDrivingRouteOverlayOfMap0.addToMap();
                                mDrivingRouteOverlayOfMap0.zoomToSpan();

                                mSelectTripModeDialogOfMap0.dismiss();
                                mDialogRouteResultOfMap0.dismiss();
                                BikeNavigateHelper bikeNavigateHelper = BikeNavigateHelper.getInstance();
                                bikeNavigateHelper.initNaviEngine(mActivity, new IBEngineInitListener() {
                                    public void engineInitSuccess() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化成功");

                                        AlertDialog inNaviDialog = new AlertDialog.Builder(mActivity)
                                                .setTitle("是否开始导航")
                                                .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BikeRouteNodeInfo startPt = new BikeRouteNodeInfo();
                                                        BikeRouteNodeInfo endPt = new BikeRouteNodeInfo();
                                                        startPt.setLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                                        endPt.setLocation(mLatLngForMap0);
                                                        BikeNaviLaunchParam param = new BikeNaviLaunchParam()
                                                                .startNodeInfo(startPt)
                                                                .endNodeInfo(endPt).vehicle(1);
                                                        BikeNavigateHelper.getInstance().routePlanWithRouteNode(param, new IBRoutePlanListener() {
                                                            public void onRoutePlanStart() {
                                                            }

                                                            public void onRoutePlanSuccess() {
                                                                Intent intent = new Intent(mActivity, BNaviGuideActivity.class);
                                                                mActivity.startActivity(intent);
                                                            }

                                                            public void onRoutePlanFail(BikeRoutePlanError bikeRoutePlanError) {
                                                            }
                                                        });
                                                    }
                                                })
                                                .create();
                                        inNaviDialog.show();

                                    }

                                    public void engineInitFail() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化失败");
                                    }
                                });
                            }
                        });
                    }
                    if (mLvRouteResultOfMap1 != null) {
                        mLvRouteResultOfMap1.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return drivingRouteResult.getRouteLines().size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = View.inflate(mActivity, R.layout.item_route_result, null);
                                TextView tvItemId = view.findViewById(R.id.tv_item_id);
                                TextView tvName = view.findViewById(R.id.tv_name);
                                TextView tvTime = view.findViewById(R.id.tv_time);
                                TextView tvDis = view.findViewById(R.id.tv_distance);

                                tvItemId.setText("方案" + (1 + position));
                                tvName.setText(drivingRouteResult.getRouteLines().get(position).getTitle());
                                tvTime.setText("时间：大约" + (drivingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                                tvDis.setText("路程：大约" + (drivingRouteResult.getRouteLines().get(position).getDistance() / 1000.0) + "公里");
                                return view;
                            }
                        });
                        //为地图图层1的路线搜索结果设置监听，点击结果项时在地图上绘制路线
                        mLvRouteResultOfMap1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //若已经画上了驾车路线，需要清除再画
                                if (mDrivingRouteOverlayOfMap1 != null) {
                                    mDrivingRouteOverlayOfMap1.removeFromMap();
                                }
                                mBaiduMapOfMap1.clear();
                                mMarkerOfMap1 = null;
                                mDrivingRouteOverlayOfMap1 = new DrivingRouteOverlay(mBaiduMapOfMap1);
                                mBaiduMapOfMap1.setOnMarkerClickListener(mDrivingRouteOverlayOfMap1);
                                mDrivingRouteOverlayOfMap1.setData(drivingRouteResult.getRouteLines().get(position));
                                mDrivingRouteOverlayOfMap1.addToMap();
                                mDrivingRouteOverlayOfMap1.zoomToSpan();

                                mSelectTripModeDialogOfMap1.dismiss();
                                mDialogRouteResultOfMap1.dismiss();
                                BikeNavigateHelper bikeNavigateHelper = BikeNavigateHelper.getInstance();
                                bikeNavigateHelper.initNaviEngine(mActivity, new IBEngineInitListener() {
                                    public void engineInitSuccess() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化成功");

                                        AlertDialog inNaviDialog = new AlertDialog.Builder(mActivity)
                                                .setTitle("是否开始导航")
                                                .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BikeRouteNodeInfo startPt = new BikeRouteNodeInfo();
                                                        BikeRouteNodeInfo endPt = new BikeRouteNodeInfo();
                                                        startPt.setLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                                        endPt.setLocation(mLatLngForMap1);
                                                        BikeNaviLaunchParam param = new BikeNaviLaunchParam()
                                                                .startNodeInfo(startPt)
                                                                .endNodeInfo(endPt).vehicle(1);
                                                        BikeNavigateHelper.getInstance().routePlanWithRouteNode(param, new IBRoutePlanListener() {
                                                            public void onRoutePlanStart() {
                                                            }

                                                            public void onRoutePlanSuccess() {
                                                                Intent intent = new Intent(mActivity, BNaviGuideActivity.class);
                                                                mActivity.startActivity(intent);
                                                            }

                                                            public void onRoutePlanFail(BikeRoutePlanError bikeRoutePlanError) {
                                                            }
                                                        });
                                                    }
                                                })
                                                .create();
                                        inNaviDialog.show();

                                    }

                                    public void engineInitFail() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化失败");
                                    }
                                });
                            }
                        });
                    }
                    if (mLvRouteResultOfMap2 != null) {
                        mLvRouteResultOfMap2.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return drivingRouteResult.getRouteLines().size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = View.inflate(mActivity, R.layout.item_route_result, null);
                                TextView tvItemId = view.findViewById(R.id.tv_item_id);
                                TextView tvName = view.findViewById(R.id.tv_name);
                                TextView tvTime = view.findViewById(R.id.tv_time);
                                TextView tvDis = view.findViewById(R.id.tv_distance);

                                tvItemId.setText("方案" + (1 + position));
                                tvName.setText(drivingRouteResult.getRouteLines().get(position).getTitle());
                                tvTime.setText("时间：大约" + (drivingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                                tvDis.setText("路程：大约" + (drivingRouteResult.getRouteLines().get(position).getDistance() / 1000.0) + "公里");
                                return view;
                            }
                        });
                        //为地图图层2的路线搜索结果设置监听，点击结果项时在地图上绘制路线
                        mLvRouteResultOfMap2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //若已经画上了驾车路线，需要清除再画
                                if (mDrivingRouteOverlayOfMap2 != null) {
                                    mDrivingRouteOverlayOfMap2.removeFromMap();
                                }
                                mBaiduMapOfMap2.clear();
                                mMarkerOfMap2 = null;
                                mDrivingRouteOverlayOfMap2 = new DrivingRouteOverlay(mBaiduMapOfMap2);
                                mBaiduMapOfMap2.setOnMarkerClickListener(mDrivingRouteOverlayOfMap2);
                                mDrivingRouteOverlayOfMap2.setData(drivingRouteResult.getRouteLines().get(position));
                                mDrivingRouteOverlayOfMap2.addToMap();
                                mDrivingRouteOverlayOfMap2.zoomToSpan();

                                mSelectTripModeDialogOfMap2.dismiss();
                                mDialogRouteResultOfMap2.dismiss();
                                BikeNavigateHelper bikeNavigateHelper = BikeNavigateHelper.getInstance();
                                bikeNavigateHelper.initNaviEngine(mActivity, new IBEngineInitListener() {
                                    public void engineInitSuccess() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化成功");

                                        AlertDialog inNaviDialog = new AlertDialog.Builder(mActivity)
                                                .setTitle("是否开始导航")
                                                .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BikeRouteNodeInfo startPt = new BikeRouteNodeInfo();
                                                        BikeRouteNodeInfo endPt = new BikeRouteNodeInfo();
                                                        startPt.setLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                                        endPt.setLocation(mLatLngForMap2);
                                                        BikeNaviLaunchParam param = new BikeNaviLaunchParam()
                                                                .startNodeInfo(startPt)
                                                                .endNodeInfo(endPt).vehicle(1);
                                                        BikeNavigateHelper.getInstance().routePlanWithRouteNode(param, new IBRoutePlanListener() {
                                                            public void onRoutePlanStart() {
                                                            }

                                                            public void onRoutePlanSuccess() {
                                                                Intent intent = new Intent(mActivity, BNaviGuideActivity.class);
                                                                mActivity.startActivity(intent);
                                                            }

                                                            public void onRoutePlanFail(BikeRoutePlanError bikeRoutePlanError) {
                                                            }
                                                        });
                                                    }
                                                })
                                                .create();
                                        inNaviDialog.show();

                                    }

                                    public void engineInitFail() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化失败");
                                    }
                                });
                            }
                        });
                    }
                    if (mLvRouteResultOfMap3 != null) {
                        mLvRouteResultOfMap3.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return drivingRouteResult.getRouteLines().size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = View.inflate(mActivity, R.layout.item_route_result, null);
                                TextView tvItemId = view.findViewById(R.id.tv_item_id);
                                TextView tvName = view.findViewById(R.id.tv_name);
                                TextView tvTime = view.findViewById(R.id.tv_time);
                                TextView tvDis = view.findViewById(R.id.tv_distance);

                                tvItemId.setText("方案" + (1 + position));
                                tvName.setText(drivingRouteResult.getRouteLines().get(position).getTitle());
                                tvTime.setText("时间：大约" + (drivingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                                tvDis.setText("路程：大约" + (drivingRouteResult.getRouteLines().get(position).getDistance() / 1000.0) + "公里");
                                return view;
                            }
                        });
                        //为地图图层3的路线搜索结果设置监听，点击结果项时在地图上绘制路线
                        mLvRouteResultOfMap3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //若已经画上了驾车路线，需要清除再画
                                if (mDrivingRouteOverlayOfMap3 != null) {
                                    mDrivingRouteOverlayOfMap3.removeFromMap();
                                }
                                mBaiduMapOfMap3.clear();
                                mMarkerOfMap3 = null;
                                mDrivingRouteOverlayOfMap3 = new DrivingRouteOverlay(mBaiduMapOfMap3);
                                mBaiduMapOfMap3.setOnMarkerClickListener(mDrivingRouteOverlayOfMap3);
                                mDrivingRouteOverlayOfMap3.setData(drivingRouteResult.getRouteLines().get(position));
                                mDrivingRouteOverlayOfMap3.addToMap();
                                mDrivingRouteOverlayOfMap3.zoomToSpan();

                                mSelectTripModeDialogOfMap3.dismiss();
                                mDialogRouteResultOfMap3.dismiss();
                                BikeNavigateHelper bikeNavigateHelper = BikeNavigateHelper.getInstance();
                                bikeNavigateHelper.initNaviEngine(mActivity, new IBEngineInitListener() {
                                    public void engineInitSuccess() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化成功");

                                        AlertDialog inNaviDialog = new AlertDialog.Builder(mActivity)
                                                .setTitle("是否开始导航")
                                                .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BikeRouteNodeInfo startPt = new BikeRouteNodeInfo();
                                                        BikeRouteNodeInfo endPt = new BikeRouteNodeInfo();
                                                        startPt.setLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                                        endPt.setLocation(mLatLngForMap3);
                                                        BikeNaviLaunchParam param = new BikeNaviLaunchParam()
                                                                .startNodeInfo(startPt)
                                                                .endNodeInfo(endPt).vehicle(1);
                                                        BikeNavigateHelper.getInstance().routePlanWithRouteNode(param, new IBRoutePlanListener() {
                                                            public void onRoutePlanStart() {
                                                            }

                                                            public void onRoutePlanSuccess() {
                                                                Intent intent = new Intent(mActivity, BNaviGuideActivity.class);
                                                                mActivity.startActivity(intent);
                                                            }

                                                            public void onRoutePlanFail(BikeRoutePlanError bikeRoutePlanError) {
                                                            }
                                                        });
                                                    }
                                                })
                                                .create();
                                        inNaviDialog.show();

                                    }

                                    public void engineInitFail() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化失败");
                                    }
                                });
                            }
                        });
                    }
                } else {
                    Toast.makeText(mActivity, "未搜索到结果", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(final BikingRouteResult bikingRouteResult) {
                if (bikingRouteResult != null && bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    if(mLvRouteResultOfMap0!=null){
                        mLvRouteResultOfMap0.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return bikingRouteResult.getRouteLines().size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = View.inflate(mActivity, R.layout.item_route_result, null);
                                TextView tvItemId = view.findViewById(R.id.tv_item_id);
                                TextView tvName = view.findViewById(R.id.tv_name);
                                TextView tvTime = view.findViewById(R.id.tv_time);
                                TextView tvDis = view.findViewById(R.id.tv_distance);

                                tvItemId.setText("方案" + (1 + position));
                                tvName.setText(bikingRouteResult.getRouteLines().get(position).getTitle());
                                tvTime.setText("时间：大约" + (bikingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                                tvDis.setText("路程：大约" + (bikingRouteResult.getRouteLines().get(position).getDistance() / 1000.0) + "公里");
                                return view;
                            }
                        });
                        //点击搜索结果项时在地图上绘制路线
                        mLvRouteResultOfMap0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //若已经画上了步行路线，需要清除再画
                                if (mBikingRouteOverlayOfMap0 != null) {
                                    mBikingRouteOverlayOfMap0.removeFromMap();
                                }
                                mBaiduMapOfMap0.clear();
                                mMarkerOfMap0 = null;
                                mBikingRouteOverlayOfMap0 = new BikingRouteOverlay(mBaiduMapOfMap0);
                                mBaiduMapOfMap0.setOnMarkerClickListener(mBikingRouteOverlayOfMap0);
                                mBikingRouteOverlayOfMap0.setData(bikingRouteResult.getRouteLines().get(position));
                                mBikingRouteOverlayOfMap0.addToMap();
                                mBikingRouteOverlayOfMap0.zoomToSpan();

                                mSelectTripModeDialogOfMap0.dismiss();
                                mDialogRouteResultOfMap0.dismiss();
                                BikeNavigateHelper bikeNavigateHelper = BikeNavigateHelper.getInstance();
                                bikeNavigateHelper.initNaviEngine(mActivity, new IBEngineInitListener() {
                                    public void engineInitSuccess() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化成功");

                                        AlertDialog inNaviDialog = new AlertDialog.Builder(mActivity)
                                                .setTitle("是否开始导航")
                                                .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BikeRouteNodeInfo startPt = new BikeRouteNodeInfo();
                                                        BikeRouteNodeInfo endPt = new BikeRouteNodeInfo();
                                                        startPt.setLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                                        endPt.setLocation(mLatLngForMap0);
                                                        BikeNaviLaunchParam param = new BikeNaviLaunchParam()
                                                                .startNodeInfo(startPt)
                                                                .endNodeInfo(endPt).vehicle(0);
                                                        BikeNavigateHelper.getInstance().routePlanWithRouteNode(param, new IBRoutePlanListener() {
                                                            public void onRoutePlanStart() {
                                                            }

                                                            public void onRoutePlanSuccess() {
                                                                Intent intent = new Intent(mActivity, BNaviGuideActivity.class);
                                                                mActivity.startActivity(intent);
                                                            }

                                                            public void onRoutePlanFail(BikeRoutePlanError bikeRoutePlanError) {
                                                            }
                                                        });
                                                    }
                                                })
                                                .create();
                                        inNaviDialog.show();

                                    }

                                    public void engineInitFail() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化失败");
                                    }
                                });
                            }
                        });
                    }
                    if(mLvRouteResultOfMap1!=null){
                        mLvRouteResultOfMap1.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return bikingRouteResult.getRouteLines().size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = View.inflate(mActivity, R.layout.item_route_result, null);
                                TextView tvItemId = view.findViewById(R.id.tv_item_id);
                                TextView tvName = view.findViewById(R.id.tv_name);
                                TextView tvTime = view.findViewById(R.id.tv_time);
                                TextView tvDis = view.findViewById(R.id.tv_distance);

                                tvItemId.setText("方案" + (1 + position));
                                tvName.setText(bikingRouteResult.getRouteLines().get(position).getTitle());
                                tvTime.setText("时间：大约" + (bikingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                                tvDis.setText("路程：大约" + (bikingRouteResult.getRouteLines().get(position).getDistance() / 1000.0) + "公里");
                                return view;
                            }
                        });
                        //点击搜索结果项时在地图上绘制路线
                        mLvRouteResultOfMap1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //若已经画上了步行路线，需要清除再画
                                if (mBikingRouteOverlayOfMap1 != null) {
                                    mBikingRouteOverlayOfMap1.removeFromMap();
                                }
                                mBaiduMapOfMap1.clear();
                                mMarkerOfMap1 = null;
                                mBikingRouteOverlayOfMap1 = new BikingRouteOverlay(mBaiduMapOfMap1);
                                mBaiduMapOfMap1.setOnMarkerClickListener(mBikingRouteOverlayOfMap1);
                                mBikingRouteOverlayOfMap1.setData(bikingRouteResult.getRouteLines().get(position));
                                mBikingRouteOverlayOfMap1.addToMap();
                                mBikingRouteOverlayOfMap1.zoomToSpan();

                                mSelectTripModeDialogOfMap1.dismiss();
                                mDialogRouteResultOfMap1.dismiss();
                                BikeNavigateHelper bikeNavigateHelper = BikeNavigateHelper.getInstance();
                                bikeNavigateHelper.initNaviEngine(mActivity, new IBEngineInitListener() {
                                    public void engineInitSuccess() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化成功");

                                        AlertDialog inNaviDialog = new AlertDialog.Builder(mActivity)
                                                .setTitle("是否开始导航")
                                                .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BikeRouteNodeInfo startPt = new BikeRouteNodeInfo();
                                                        BikeRouteNodeInfo endPt = new BikeRouteNodeInfo();
                                                        startPt.setLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                                        endPt.setLocation(mLatLngForMap1);
                                                        BikeNaviLaunchParam param = new BikeNaviLaunchParam()
                                                                .startNodeInfo(startPt)
                                                                .endNodeInfo(endPt).vehicle(0);
                                                        BikeNavigateHelper.getInstance().routePlanWithRouteNode(param, new IBRoutePlanListener() {
                                                            public void onRoutePlanStart() {
                                                            }

                                                            public void onRoutePlanSuccess() {
                                                                Intent intent = new Intent(mActivity, BNaviGuideActivity.class);
                                                                mActivity.startActivity(intent);
                                                            }

                                                            public void onRoutePlanFail(BikeRoutePlanError bikeRoutePlanError) {
                                                            }
                                                        });
                                                    }
                                                })
                                                .create();
                                        inNaviDialog.show();

                                    }

                                    public void engineInitFail() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化失败");
                                    }
                                });
                            }
                        });
                    }
                    if(mLvRouteResultOfMap2!=null){
                        mLvRouteResultOfMap2.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return bikingRouteResult.getRouteLines().size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = View.inflate(mActivity, R.layout.item_route_result, null);
                                TextView tvItemId = view.findViewById(R.id.tv_item_id);
                                TextView tvName = view.findViewById(R.id.tv_name);
                                TextView tvTime = view.findViewById(R.id.tv_time);
                                TextView tvDis = view.findViewById(R.id.tv_distance);

                                tvItemId.setText("方案" + (1 + position));
                                tvName.setText(bikingRouteResult.getRouteLines().get(position).getTitle());
                                tvTime.setText("时间：大约" + (bikingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                                tvDis.setText("路程：大约" + (bikingRouteResult.getRouteLines().get(position).getDistance() / 1000.0) + "公里");
                                return view;
                            }
                        });
                        //点击搜索结果项时在地图上绘制路线
                        mLvRouteResultOfMap2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //若已经画上了步行路线，需要清除再画
                                if (mBikingRouteOverlayOfMap2 != null) {
                                    mBikingRouteOverlayOfMap2.removeFromMap();
                                }
                                mBaiduMapOfMap2.clear();
                                mMarkerOfMap2 = null;
                                mBikingRouteOverlayOfMap2 = new BikingRouteOverlay(mBaiduMapOfMap2);
                                mBaiduMapOfMap2.setOnMarkerClickListener(mBikingRouteOverlayOfMap2);
                                mBikingRouteOverlayOfMap2.setData(bikingRouteResult.getRouteLines().get(position));
                                mBikingRouteOverlayOfMap2.addToMap();
                                mBikingRouteOverlayOfMap2.zoomToSpan();

                                mSelectTripModeDialogOfMap2.dismiss();
                                mDialogRouteResultOfMap2.dismiss();
                                BikeNavigateHelper bikeNavigateHelper = BikeNavigateHelper.getInstance();
                                bikeNavigateHelper.initNaviEngine(mActivity, new IBEngineInitListener() {
                                    public void engineInitSuccess() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化成功");

                                        AlertDialog inNaviDialog = new AlertDialog.Builder(mActivity)
                                                .setTitle("是否开始导航")
                                                .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BikeRouteNodeInfo startPt = new BikeRouteNodeInfo();
                                                        BikeRouteNodeInfo endPt = new BikeRouteNodeInfo();
                                                        startPt.setLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                                        endPt.setLocation(mLatLngForMap2);
                                                        BikeNaviLaunchParam param = new BikeNaviLaunchParam()
                                                                .startNodeInfo(startPt)
                                                                .endNodeInfo(endPt).vehicle(0);
                                                        BikeNavigateHelper.getInstance().routePlanWithRouteNode(param, new IBRoutePlanListener() {
                                                            public void onRoutePlanStart() {
                                                            }

                                                            public void onRoutePlanSuccess() {
                                                                Intent intent = new Intent(mActivity, BNaviGuideActivity.class);
                                                                mActivity.startActivity(intent);
                                                            }

                                                            public void onRoutePlanFail(BikeRoutePlanError bikeRoutePlanError) {
                                                            }
                                                        });
                                                    }
                                                })
                                                .create();
                                        inNaviDialog.show();

                                    }

                                    public void engineInitFail() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化失败");
                                    }
                                });
                            }
                        });
                    }
                    if(mLvRouteResultOfMap3!=null){
                        mLvRouteResultOfMap3.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return bikingRouteResult.getRouteLines().size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = View.inflate(mActivity, R.layout.item_route_result, null);
                                TextView tvItemId = view.findViewById(R.id.tv_item_id);
                                TextView tvName = view.findViewById(R.id.tv_name);
                                TextView tvTime = view.findViewById(R.id.tv_time);
                                TextView tvDis = view.findViewById(R.id.tv_distance);

                                tvItemId.setText("方案" + (1 + position));
                                tvName.setText(bikingRouteResult.getRouteLines().get(position).getTitle());
                                tvTime.setText("时间：大约" + (bikingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                                tvDis.setText("路程：大约" + (bikingRouteResult.getRouteLines().get(position).getDistance() / 1000.0) + "公里");
                                return view;
                            }
                        });
                        //点击搜索结果项时在地图上绘制路线
                        mLvRouteResultOfMap3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //若已经画上了步行路线，需要清除再画
                                if (mBikingRouteOverlayOfMap3 != null) {
                                    mBikingRouteOverlayOfMap3.removeFromMap();
                                }
                                mBaiduMapOfMap3.clear();
                                mMarkerOfMap3 = null;
                                mBikingRouteOverlayOfMap3 = new BikingRouteOverlay(mBaiduMapOfMap3);
                                mBaiduMapOfMap3.setOnMarkerClickListener(mBikingRouteOverlayOfMap3);
                                mBikingRouteOverlayOfMap3.setData(bikingRouteResult.getRouteLines().get(position));
                                mBikingRouteOverlayOfMap3.addToMap();
                                mBikingRouteOverlayOfMap3.zoomToSpan();

                                mSelectTripModeDialogOfMap3.dismiss();
                                mDialogRouteResultOfMap3.dismiss();
                                BikeNavigateHelper bikeNavigateHelper = BikeNavigateHelper.getInstance();
                                bikeNavigateHelper.initNaviEngine(mActivity, new IBEngineInitListener() {
                                    public void engineInitSuccess() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化成功");

                                        AlertDialog inNaviDialog = new AlertDialog.Builder(mActivity)
                                                .setTitle("是否开始导航")
                                                .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BikeRouteNodeInfo startPt = new BikeRouteNodeInfo();
                                                        BikeRouteNodeInfo endPt = new BikeRouteNodeInfo();
                                                        startPt.setLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                                        endPt.setLocation(mLatLngForMap3);
                                                        BikeNaviLaunchParam param = new BikeNaviLaunchParam()
                                                                .startNodeInfo(startPt)
                                                                .endNodeInfo(endPt).vehicle(0);
                                                        BikeNavigateHelper.getInstance().routePlanWithRouteNode(param, new IBRoutePlanListener() {
                                                            public void onRoutePlanStart() {
                                                            }

                                                            public void onRoutePlanSuccess() {
                                                                Intent intent = new Intent(mActivity, BNaviGuideActivity.class);
                                                                mActivity.startActivity(intent);
                                                            }

                                                            public void onRoutePlanFail(BikeRoutePlanError bikeRoutePlanError) {
                                                            }
                                                        });
                                                    }
                                                })
                                                .create();
                                        inNaviDialog.show();

                                    }

                                    public void engineInitFail() {
                                        LogUtils.e("导航引擎初始化结果", "引擎初始化失败");
                                    }
                                });
                            }
                        });
                    }
                } else {
                    Toast.makeText(mActivity, "未搜索到结果", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 为地图1初始化监听
     */
    public void initListenerForMap1(){
        //为地图图层1上的TextView设置监听，未处于正在监听弹出请输入信息的对话框，输入信息后获取位置
        mTextViewOfMap1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //无网络状态不执行任何操作，直接return
                if (!NetUtils.networkConnected(mActivity)) {
                    Toast.makeText(mActivity, "无网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                //若未处于正在监听时所执行的操作：弹出请输入信息的对话框
                View view = View.inflate(mActivity, R.layout.dialog_get_info, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("请输入信息")
                        .setView(view)
                        .create();
                //设置姓名的EditView
                final EditText et_name = view.findViewById(R.id.et_name);
                //设置配对码的EditView
                final EditText et_key = view.findViewById(R.id.et_key);
                //服务器地址
                final EditText et_address = view.findViewById(R.id.et_address_getlocation);
                //确认按钮
                Button btnConfirm = view.findViewById(R.id.btn_confirm);
                //展示对话框
                alertDialog.show();
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        //为地图图层1的人名赋值
                        mNamoOfMap1 = et_name.getText().toString().trim();
                        String key = et_key.getText().toString().trim();
                        if (mNamoOfMap1 == null || mNamoOfMap1.length() == 0 || key.isEmpty()) {
                            Toast.makeText(mActivity, "称呼或配对码不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mActivity, "正在获取位置", Toast.LENGTH_SHORT).show();
                            getLocationInfoForMap1(et_address.getText().toString().trim(), key);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        //为地图图层1设置图标点击事件
        mBaiduMapOfMap1.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            //图标上的小气泡标题
            TextView mTvPopTitle;
            //详情小图标
            ImageView ivDetail;
            //路线图标
            ImageView ivRoad;

            //标识：是否显示气泡的
            boolean toggle = true;

            public boolean onMarkerClick(Marker marker) {
                //为地图1上的被监听者图标设置点击事件，弹出气泡
                if (marker.getId() == mMarkerOfMap1.getId()) {
                    if (mPopOfMap1 == null) {
                        //实例化弹出气泡
                        mPopOfMap1 = View.inflate(mActivity, R.layout.pop, null);
                        MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                                .position(marker.getPosition())
                                .yOffset(-90)
                                .build();
                        //实例化气泡里的标题
                        mTvPopTitle = mPopOfMap1.findViewById(R.id.tv_title);
                        //设置标题文本内容
                        mTvPopTitle.setText(marker.getTitle());
                        //实例化气泡里的路线图标
                        ivRoad = mPopOfMap1.findViewById(R.id.iv_navigate_method);
                        //实例化气泡里的详情图标
                        ivDetail = mPopOfMap1.findViewById(R.id.iv_detail);
                        //将气泡加进地图1
                        mMapView1.addView(mPopOfMap1, params);
                        //详情图标监听
                        ivDetail.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                String mes[] = new String[]{"坐标类型：bd09", "纬度：" + mLatOfMap1 + "", "经度：" + mLngOfMap1 + "", "地址：" + mAddressStrOfMap1};
                                mDetailDialogOfMap1 = new AlertDialog.Builder(mActivity)
                                        .setItems(mes, null)
                                        .setTitle("相关信息")
                                        .create();
                                mDetailDialogOfMap1.show();
                            }
                        });
                        //路线图标设置事件：弹出出行方式选择的对话框
                        ivRoad.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //初始化对话框的view
                                View view = View.inflate(mActivity, R.layout.dialog_navigate_mode, null);
                                mSelectTripModeDialogOfMap1 = new AlertDialog.Builder(mActivity)
                                        .setTitle("请选择出行方式")
                                        .setView(view)
                                        .create();
                                mSelectTripModeDialogOfMap1.show();
                                //实例化步行图标
                                ImageView ivWalk = view.findViewById(R.id.iv_walk);
                                //实例化骑行图标
                                ImageView ivBike = view.findViewById(R.id.iv_bike);
                                //实例化驾车图标
                                ImageView ivCar = view.findViewById(R.id.iv_car);
                                //实例化公交图标
                                // ImageView ivBus = view.findViewById(R.id.iv_bus);
                                //步行图标设置监听：弹出搜索结果对话框
                                ivWalk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResultOfMap1 = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResultOfMap1 = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResultOfMap1.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.walkingSearch(new WalkingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(mLatLngForMap1)));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //骑行图标监听
                                ivBike.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResultOfMap1 = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResultOfMap1 = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResultOfMap1.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.bikingSearch(new BikingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(mLatLngForMap1)));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //驾车图标设置监听：弹出搜索结果对话框
                                ivCar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResultOfMap1 = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResultOfMap1 = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResultOfMap1.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.drivingSearch(new DrivingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(mLatLngForMap1)));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
//                                ivBus.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                    }
//                                });
                            }
                        });
                    } else {
                        if (toggle) {
                            toggle = false;
                            mPopOfMap1.setVisibility(View.INVISIBLE);
                        } else {
                            toggle = true;
                            mPopOfMap1.setVisibility(View.VISIBLE);
                        }
                        MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                                .position(marker.getPosition())
                                .yOffset(-90)
                                .build();
                        mMapView1.updateViewLayout(mPopOfMap1, params);
                    }
                    mTvPopTitle.setText(marker.getTitle());
                }
                return true;
            }
        });

        //地图1关闭按钮监听
        mButtonColseOfMap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("是否取消监听")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isListenerOfMap1 = false;
                                mTextViewOfMap1.setVisibility(View.VISIBLE);
                                mTimerOfMap1.cancel();
                                mButtonColseOfMap1.setVisibility(View.INVISIBLE);
                                mButtionLocateOfMap1.setVisibility(View.INVISIBLE);
                                if (mMarkerOfMap1 != null) {
                                    mMarkerOfMap1.remove();
                                    mMarkerOfMap1 = null;

                                }
                                if (mPopOfMap1 != null) {
                                    mPopOfMap1.setVisibility(View.INVISIBLE);

                                }
                                mBaiduMapOfMap1.clear();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        //地图1定位按钮监听
        mButtionLocateOfMap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMapOfMap1.clear();
                mMarkerOfMap1=null;
                if (mLatLngForMap1 != null) {
                    mBaiduMapOfMap1.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                            .target(mLatLngForMap1)
                            .zoom(19)
                            .overlook(1)
                            .build()));
                }
                if (mPopOfMap1 != null) {
                    mPopOfMap1.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    /**
     * 为地图2初始化监听
     */
    public void initListenerForMap2(){
        //为地图图层2上的TextView设置监听，未处于正在监听弹出请输入信息的对话框，输入信息后获取位置
        mTextViewOfMap2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //无网络状态不执行任何操作，直接return
                if (!NetUtils.networkConnected(mActivity)) {
                    Toast.makeText(mActivity, "无网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                //若未处于正在监听时所执行的操作：弹出请输入信息的对话框
                View view = View.inflate(mActivity, R.layout.dialog_get_info, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("请输入信息")
                        .setView(view)
                        .create();
                //设置姓名的EditView
                final EditText et_name = view.findViewById(R.id.et_name);
                //设置配对码的EditView
                final EditText et_key = view.findViewById(R.id.et_key);
                //服务器地址
                final EditText et_address = view.findViewById(R.id.et_address_getlocation);
                //确认按钮
                Button btnConfirm = view.findViewById(R.id.btn_confirm);
                //展示对话框
                alertDialog.show();
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        //为地图图层2的人名赋值
                        mNamoOfMap2 = et_name.getText().toString().trim();
                        String key = et_key.getText().toString().trim();
                        if (mNamoOfMap2 == null || mNamoOfMap2.length() == 0 || key.isEmpty()) {
                            Toast.makeText(mActivity, "称呼或配对码不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mActivity, "正在获取位置", Toast.LENGTH_SHORT).show();
                            getLocationInfoForMap2(et_address.getText().toString().trim(), key);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        //为地图图层2设置图标点击事件
        mBaiduMapOfMap2.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            //图标上的小气泡标题
            TextView mTvPopTitle;
            //详情小图标
            ImageView ivDetail;
            //路线图标
            ImageView ivRoad;

            //标识：是否显示气泡的
            boolean toggle = true;

            public boolean onMarkerClick(Marker marker) {
                //为地图2上的被监听者图标设置点击事件，弹出气泡
                if (marker.getId() == mMarkerOfMap2.getId()) {
                    if (mPopOfMap2 == null) {
                        //实例化弹出气泡
                        mPopOfMap2 = View.inflate(mActivity, R.layout.pop, null);
                        MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                                .position(marker.getPosition())
                                .yOffset(-90)
                                .build();
                        //实例化气泡里的标题
                        mTvPopTitle = mPopOfMap2.findViewById(R.id.tv_title);
                        //设置标题文本内容
                        mTvPopTitle.setText(marker.getTitle());
                        //实例化气泡里的路线图标
                        ivRoad = mPopOfMap2.findViewById(R.id.iv_navigate_method);
                        //实例化气泡里的详情图标
                        ivDetail = mPopOfMap2.findViewById(R.id.iv_detail);
                        //将气泡加进地图2
                        mMapView2.addView(mPopOfMap2, params);
                        //详情图标监听
                        ivDetail.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                String mes[] = new String[]{"坐标类型：bd09", "纬度：" + mLatOfMap2 + "", "经度：" + mLngOfMap2 + "", "地址：" + mAddressStrOfMap2};
                                mDetailDialogOfMap2 = new AlertDialog.Builder(mActivity)
                                        .setItems(mes, null)
                                        .setTitle("相关信息")
                                        .create();
                                mDetailDialogOfMap2.show();
                            }
                        });
                        //路线图标设置事件：弹出出行方式选择的对话框
                        ivRoad.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //初始化对话框的view
                                View view = View.inflate(mActivity, R.layout.dialog_navigate_mode, null);
                                mSelectTripModeDialogOfMap2 = new AlertDialog.Builder(mActivity)
                                        .setTitle("请选择出行方式")
                                        .setView(view)
                                        .create();
                                mSelectTripModeDialogOfMap2.show();
                                //实例化步行图标
                                ImageView ivWalk = view.findViewById(R.id.iv_walk);
                                //实例化骑行图标
                                ImageView ivBike = view.findViewById(R.id.iv_bike);
                                //实例化驾车图标
                                ImageView ivCar = view.findViewById(R.id.iv_car);
                                //实例化公交图标
                                // ImageView ivBus = view.findViewById(R.id.iv_bus);
                                //步行图标设置监听：弹出搜索结果对话框
                                ivWalk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResultOfMap2 = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResultOfMap2 = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResultOfMap2.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.walkingSearch(new WalkingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(mLatLngForMap2)));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //骑行图标监听
                                ivBike.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResultOfMap2 = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResultOfMap2 = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResultOfMap2.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.bikingSearch(new BikingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(mLatLngForMap2)));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //驾车图标设置监听：弹出搜索结果对话框
                                ivCar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResultOfMap2 = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResultOfMap2 = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResultOfMap2.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.drivingSearch(new DrivingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(mLatLngForMap2)));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
//                                ivBus.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                    }
//                                });
                            }
                        });
                    } else {
                        if (toggle) {
                            toggle = false;
                            mPopOfMap2.setVisibility(View.INVISIBLE);
                        } else {
                            toggle = true;
                            mPopOfMap2.setVisibility(View.VISIBLE);
                        }
                        MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                                .position(marker.getPosition())
                                .yOffset(-90)
                                .build();
                        mMapView2.updateViewLayout(mPopOfMap2, params);
                    }
                    mTvPopTitle.setText(marker.getTitle());
                }
                return true;
            }
        });

        //地图2关闭按钮监听
        mButtonColseOfMap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("是否取消监听")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isListenerOfMap2 = false;
                                mTextViewOfMap2.setVisibility(View.VISIBLE);
                                mTimerOfMap2.cancel();
                                mButtonColseOfMap2.setVisibility(View.INVISIBLE);
                                mButtionLocateOfMap2.setVisibility(View.INVISIBLE);
                                if (mMarkerOfMap2 != null) {
                                    mMarkerOfMap2.remove();
                                    mMarkerOfMap2 = null;

                                }
                                if (mPopOfMap2 != null) {
                                    mPopOfMap2.setVisibility(View.INVISIBLE);

                                }
                                mBaiduMapOfMap2.clear();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        //地图2定位按钮监听
        mButtionLocateOfMap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMapOfMap2.clear();
                mMarkerOfMap2=null;
                if (mLatLngForMap2 != null) {
                    mBaiduMapOfMap2.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                            .target(mLatLngForMap2)
                            .zoom(19)
                            .overlook(0)
                            .build()));
                }
                if (mPopOfMap2 != null) {
                    mPopOfMap2.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    /**
     * 为地图3初始化监听
     */
    public void initListenerForMap3(){
        //为地图图层3上的TextView设置监听，未处于正在监听弹出请输入信息的对话框，输入信息后获取位置
        mTextViewOfMap3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //无网络状态不执行任何操作，直接return
                if (!NetUtils.networkConnected(mActivity)) {
                    Toast.makeText(mActivity, "无网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                //若未处于正在监听时所执行的操作：弹出请输入信息的对话框
                View view = View.inflate(mActivity, R.layout.dialog_get_info, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("请输入信息")
                        .setView(view)
                        .create();
                //设置姓名的EditView
                final EditText et_name = view.findViewById(R.id.et_name);
                //设置配对码的EditView
                final EditText et_key = view.findViewById(R.id.et_key);
                //服务器地址
                final EditText et_address = view.findViewById(R.id.et_address_getlocation);
                //确认按钮
                Button btnConfirm = view.findViewById(R.id.btn_confirm);
                //展示对话框
                alertDialog.show();
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        //为地图图层3的人名赋值
                        mNamoOfMap3 = et_name.getText().toString().trim();
                        String key = et_key.getText().toString().trim();
                        if (mNamoOfMap3 == null || mNamoOfMap3.length() == 0 || key.isEmpty()) {
                            Toast.makeText(mActivity, "称呼或配对码不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mActivity, "正在获取位置", Toast.LENGTH_SHORT).show();
                            getLocationInfoForMap3(et_address.getText().toString().trim(), key);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        //为地图图层3设置图标点击事件
        mBaiduMapOfMap3.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            //图标上的小气泡标题
            TextView mTvPopTitle;
            //详情小图标
            ImageView ivDetail;
            //路线图标
            ImageView ivRoad;

            //标识：是否显示气泡的
            boolean toggle = true;

            public boolean onMarkerClick(Marker marker) {
                //为地图3上的被监听者图标设置点击事件，弹出气泡
                if (marker.getId() == mMarkerOfMap3.getId()) {
                    if (mPopOfMap3 == null) {
                        //实例化弹出气泡
                        mPopOfMap3 = View.inflate(mActivity, R.layout.pop, null);
                        MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                                .position(marker.getPosition())
                                .yOffset(-90)
                                .build();
                        //实例化气泡里的标题
                        mTvPopTitle = mPopOfMap3.findViewById(R.id.tv_title);
                        //设置标题文本内容
                        mTvPopTitle.setText(marker.getTitle());
                        //实例化气泡里的路线图标
                        ivRoad = mPopOfMap3.findViewById(R.id.iv_navigate_method);
                        //实例化气泡里的详情图标
                        ivDetail = mPopOfMap3.findViewById(R.id.iv_detail);
                        //将气泡加进地图3
                        mMapView3.addView(mPopOfMap3, params);
                        //详情图标监听
                        ivDetail.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                String mes[] = new String[]{"坐标类型：bd09", "纬度：" + mLatOfMap3 + "", "经度：" + mLngOfMap3 + "", "地址：" + mAddressStrOfMap3};
                                mDetailDialogOfMap3 = new AlertDialog.Builder(mActivity)
                                        .setItems(mes, null)
                                        .setTitle("相关信息")
                                        .create();
                                mDetailDialogOfMap3.show();
                            }
                        });
                        //路线图标设置事件：弹出出行方式选择的对话框
                        ivRoad.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //初始化对话框的view
                                View view = View.inflate(mActivity, R.layout.dialog_navigate_mode, null);
                                mSelectTripModeDialogOfMap3 = new AlertDialog.Builder(mActivity)
                                        .setTitle("请选择出行方式")
                                        .setView(view)
                                        .create();
                                mSelectTripModeDialogOfMap3.show();
                                //实例化步行图标
                                ImageView ivWalk = view.findViewById(R.id.iv_walk);
                                //实例化骑行图标
                                ImageView ivBike = view.findViewById(R.id.iv_bike);
                                //实例化驾车图标
                                ImageView ivCar = view.findViewById(R.id.iv_car);
                                //实例化公交图标
                                // ImageView ivBus = view.findViewById(R.id.iv_bus);
                                //步行图标设置监听：弹出搜索结果对话框
                                ivWalk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResultOfMap3 = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResultOfMap3 = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResultOfMap3.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.walkingSearch(new WalkingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(mLatLngForMap3)));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //骑行图标监听
                                ivBike.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResultOfMap3 = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResultOfMap3 = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResultOfMap3.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.bikingSearch(new BikingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(mLatLngForMap3)));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //驾车图标设置监听：弹出搜索结果对话框
                                ivCar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResultOfMap3 = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResultOfMap3 = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResultOfMap3.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.drivingSearch(new DrivingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(mLatLngForMap3)));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
//                                ivBus.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                    }
//                                });
                            }
                        });
                    } else {
                        if (toggle) {
                            toggle = false;
                            mPopOfMap3.setVisibility(View.INVISIBLE);
                        } else {
                            toggle = true;
                            mPopOfMap3.setVisibility(View.VISIBLE);
                        }
                        MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                                .position(marker.getPosition())
                                .yOffset(-90)
                                .build();
                        mMapView3.updateViewLayout(mPopOfMap3, params);
                    }
                    mTvPopTitle.setText(marker.getTitle());
                }
                return true;
            }
        });

        //地图3关闭按钮监听
        mButtonColseOfMap3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("是否取消监听")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isListenerOfMap3 = false;
                                mTextViewOfMap3.setVisibility(View.VISIBLE);
                                mTimerOfMap3.cancel();
                                mButtonColseOfMap3.setVisibility(View.INVISIBLE);
                                mButtionLocateOfMap3.setVisibility(View.INVISIBLE);
                                if (mMarkerOfMap3 != null) {
                                    mMarkerOfMap3.remove();
                                    mMarkerOfMap3 = null;

                                }
                                if (mPopOfMap3 != null) {
                                    mPopOfMap3.setVisibility(View.INVISIBLE);

                                }
                                mBaiduMapOfMap3.clear();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        //地图3定位按钮监听
        mButtionLocateOfMap3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMapOfMap3.clear();
                mMarkerOfMap3=null;
                if (mLatLngForMap3 != null) {
                    mBaiduMapOfMap3.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                            .target(mLatLngForMap3)
                            .zoom(19)
                            .overlook(0)
                            .build()));
                }
                if (mPopOfMap3 != null) {
                    mPopOfMap3.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    /**
     * 上传位置数据到服务器
     */
    void uploadLocation(final String path, final String key) {
        if (!isUploading) {
            mTimerForUploading = new Timer();
            TimerTask tt = new TimerTask() {
                boolean isFirstIn = true;

                public void run() {
                    try {
                        String sendParam = "{\n" +
                                "\tKey:\"" + key + "\",\n" +
                                "\tLatitude:" + LocationPager.bdLocation.getLatitude() + ",\n" +
                                "\tLongitude:" + LocationPager.bdLocation.getLongitude() + ",\n" +
                                "\tAddress:\"" + LocationPager.bdLocation.getAddrStr() + "\"\n" +
                                "}";

                        URL url = new URL(path);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(1000);
                        conn.setReadTimeout(1000);
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");    //设置为Post方法
                        conn.connect();

                        //将数据发送给服务器
                        OutputStream output = conn.getOutputStream();
                        output.write(sendParam.getBytes());
                        output.flush();
                        output.close();
                        if (conn.getResponseCode() == 200) {
                            isUploading = true;
                            Log.e("接受服务器发送过来的信息", "" + conn.getResponseCode());
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            System.out.println("READ: " + in.readLine());
                            in.close();
                        } else {
                            isUploading = false;
                            if (isFirstIn) {
                                Message message = new Message();
                                message.obj = "未连接到服务器，上传失败";
                                handler.sendMessage(message);
                            }
                        }
                    } catch (Exception e) {
                        isUploading = false;
                        if (isFirstIn) {
                            Message message = new Message();
                            message.obj = "未连接到服务器，上传失败";
                            handler.sendMessage(message);
                        }
                        e.printStackTrace();
                    }
                    isFirstIn = false;
                }
            };
            mTimerForUploading.schedule(tt, 1000, 1000);
        }
    }

    /**
     * 地图图层0从依据配对码从服务器获取位置信息
     */
    private void getLocationInfoForMap0(final String address, final String key) {
        if (!isListenerOfMap0) {
            mButtonColseOfMap0.setVisibility(View.VISIBLE);
            mButtionLocateOfMap0.setVisibility(View.VISIBLE);
            isListenerOfMap0 = true;
            //隐藏覆盖在地图上的TextView
            mTextViewOfMap0.setVisibility(View.GONE);
            mTimerOfMap0 = new Timer();
            TimerTask tt = new TimerTask() {
                boolean isFirstIn = true;

                public void run() {
                    try {
                        String sendParam = key;
                        URL url = new URL(address);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(2000);
                        conn.setReadTimeout(1000);
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");    //设置为Post方法
                        conn.connect();

                        //将数据发送给服务器
                        OutputStream output = conn.getOutputStream();
                        output.write(sendParam.getBytes());
                        output.flush();
                        output.close();
                        if (conn.getResponseCode() == 200) {
                            String jsonStr = StreamToStringUtils.StreamToString(conn.getInputStream());
//                            LogUtils.e(TAG,jsonStr);
                            JSONObject jsonObject = new JSONObject(jsonStr);
                            mAddressStrOfMap0 = jsonObject.getString("Address");

                            mLatOfMap0 = jsonObject.getDouble("Latitude");
                            mLngOfMap0 = jsonObject.getDouble("Longitude");
                            mLatLngForMap0 = new LatLng(mLatOfMap0, mLngOfMap0);

                            if (isFirstIn) {
                                mBaiduMapOfMap0.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                                        .zoom(19).target(mLatLngForMap0).build()));
                            }
                            //依据位置数据添加覆盖物
                            if (mMarkerOfMap0 == null) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin);
                                markerOptions.position(mLatLngForMap0)
                                        .title(mNamoOfMap0)
                                        .icon(icon)            // 图标
                                        .draggable(true);    // 设置图标可以拖动
                                //实例化覆盖物对象
                                mMarkerOfMap0 = (Marker) mBaiduMapOfMap0.addOverlay(markerOptions);
                            }
                            //若已经有了标志覆盖物，改变位置即可
                            else {
                                mMarkerOfMap0.setPosition(mLatLngForMap0);
                            }
                        } else {
                            if (isFirstIn) {
                                Message message = new Message();
                                message.obj = "未连接到服务器";
                                handler.sendMessage(message);
                            }
                        }
                    } catch (Exception e) {
                        if (isFirstIn) {
                            Message message = new Message();
                            message.obj = "未连接到服务器";
                            handler.sendMessage(message);
                        }
                        e.printStackTrace();
                    }
                    isFirstIn = false;
                }
            };
            mTimerOfMap0.schedule(tt, 1000, 1000);
        }
    }

    /**
     * 地图图层1从依据配对码从服务器获取位置信息
     */
    private void getLocationInfoForMap1(final String address, final String key) {
        if (!isListenerOfMap1) {
            mButtonColseOfMap1.setVisibility(View.VISIBLE);
            mButtionLocateOfMap1.setVisibility(View.VISIBLE);
            isListenerOfMap1 = true;
            //隐藏覆盖在地图上的TextView
            mTextViewOfMap1.setVisibility(View.GONE);
            mTimerOfMap1 = new Timer();
            TimerTask tt = new TimerTask() {
                boolean isFirstIn = true;

                public void run() {
                    try {
                        String sendParam = key;
                        URL url = new URL(address);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(2000);
                        conn.setReadTimeout(1000);
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");    //设置为Post方法
                        conn.connect();

                        //将数据发送给服务器
                        OutputStream output = conn.getOutputStream();
                        output.write(sendParam.getBytes());
                        output.flush();
                        output.close();
                        if (conn.getResponseCode() == 200) {
                            String jsonStr = StreamToStringUtils.StreamToString(conn.getInputStream());
//                            LogUtils.e(TAG,jsonStr);
                            JSONObject jsonObject = new JSONObject(jsonStr);
                            mAddressStrOfMap1 = jsonObject.getString("Address");

                            mLatOfMap1 = jsonObject.getDouble("Latitude");
                            mLngOfMap1 = jsonObject.getDouble("Longitude");
                            mLatLngForMap1 = new LatLng(mLatOfMap1, mLngOfMap1);

                            if (isFirstIn) {
                                mBaiduMapOfMap1.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                                        .zoom(19).target(mLatLngForMap1).build()));
                            }
                            //依据位置数据添加覆盖物
                            if (mMarkerOfMap1 == null) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin);
                                markerOptions.position(mLatLngForMap1)
                                        .title(mNamoOfMap1)
                                        .icon(icon)            // 图标
                                        .draggable(true);    // 设置图标可以拖动
                                //实例化覆盖物对象
                                mMarkerOfMap1 = (Marker) mBaiduMapOfMap1.addOverlay(markerOptions);
                            }
                            //若已经有了标志覆盖物，改变位置即可
                            else {
                                mMarkerOfMap1.setPosition(mLatLngForMap1);
                            }
                        } else {
                            if (isFirstIn) {
                                Message message = new Message();
                                message.obj = "未连接到服务器";
                                handler.sendMessage(message);
                            }
                        }
                    } catch (Exception e) {
                        if (isFirstIn) {
                            Message message = new Message();
                            message.obj = "未连接到服务器";
                            handler.sendMessage(message);
                        }
                        e.printStackTrace();
                    }
                    isFirstIn = false;
                }
            };
            mTimerOfMap1.schedule(tt, 1000, 1000);
        }
    }

    /**
     * 地图图层2从依据配对码从服务器获取位置信息
     */
    private void getLocationInfoForMap2(final String address, final String key) {
        if (!isListenerOfMap2) {
            mButtonColseOfMap2.setVisibility(View.VISIBLE);
            mButtionLocateOfMap2.setVisibility(View.VISIBLE);
            isListenerOfMap2 = true;
            //隐藏覆盖在地图上的TextView
            mTextViewOfMap2.setVisibility(View.GONE);
            mTimerOfMap2 = new Timer();
            TimerTask tt = new TimerTask() {
                boolean isFirstIn = true;

                public void run() {
                    try {
                        String sendParam = key;
                        URL url = new URL(address);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(1000);
                        conn.setReadTimeout(1000);
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");    //设置为Post方法
                        conn.connect();

                        //将数据发送给服务器
                        OutputStream output = conn.getOutputStream();
                        output.write(sendParam.getBytes());
                        output.flush();
                        output.close();
                        if (conn.getResponseCode() == 200) {
                            String jsonStr = StreamToStringUtils.StreamToString(conn.getInputStream());
//                            LogUtils.e(TAG,jsonStr);
                            JSONObject jsonObject = new JSONObject(jsonStr);
                            mAddressStrOfMap2 = jsonObject.getString("Address");

                            mLatOfMap2 = jsonObject.getDouble("Latitude");
                            mLngOfMap2 = jsonObject.getDouble("Longitude");
                            mLatLngForMap2 = new LatLng(mLatOfMap2, mLngOfMap2);

                            if (isFirstIn) {
                                mBaiduMapOfMap2.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                                        .zoom(19).target(mLatLngForMap2).build()));
                            }
                            //依据位置数据添加覆盖物
                            if (mMarkerOfMap2 == null) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin);
                                markerOptions.position(mLatLngForMap2)
                                        .title(mNamoOfMap2)
                                        .icon(icon)            // 图标
                                        .draggable(true);    // 设置图标可以拖动
                                //实例化覆盖物对象
                                mMarkerOfMap2 = (Marker) mBaiduMapOfMap2.addOverlay(markerOptions);
                            }
                            //若已经有了标志覆盖物，改变位置即可
                            else {
                                mMarkerOfMap2.setPosition(mLatLngForMap2);
                            }
                        } else {
                            if (isFirstIn) {
                                Message message = new Message();
                                message.obj = "未连接到服务器";
                                handler.sendMessage(message);
                            }
                        }
                    } catch (Exception e) {
                        if (isFirstIn) {
                            Message message = new Message();
                            message.obj = "未连接到服务器";
                            handler.sendMessage(message);
                        }
                        e.printStackTrace();
                    }
                    isFirstIn = false;
                }
            };
            mTimerOfMap2.schedule(tt, 1000, 1000);
        }
    }

    /**
     * 地图图层3从依据配对码从服务器获取位置信息
     */
    private void getLocationInfoForMap3(final String address, final String key) {
        if (!isListenerOfMap3) {
            mButtonColseOfMap3.setVisibility(View.VISIBLE);
            mButtionLocateOfMap3.setVisibility(View.VISIBLE);
            isListenerOfMap3 = true;
            //隐藏覆盖在地图上的TextView
            mTextViewOfMap3.setVisibility(View.GONE);
            mTimerOfMap3 = new Timer();
            TimerTask tt = new TimerTask() {
                boolean isFirstIn = true;

                public void run() {
                    try {
                        String sendParam = key;
                        URL url = new URL(address);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(1000);
                        conn.setReadTimeout(1000);
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");    //设置为Post方法
                        conn.connect();

                        //将数据发送给服务器
                        OutputStream output = conn.getOutputStream();
                        output.write(sendParam.getBytes());
                        output.flush();
                        output.close();
                        if (conn.getResponseCode() == 200) {
                            String jsonStr = StreamToStringUtils.StreamToString(conn.getInputStream());
//                            LogUtils.e(TAG,jsonStr);
                            JSONObject jsonObject = new JSONObject(jsonStr);
                            mAddressStrOfMap3 = jsonObject.getString("Address");

                            mLatOfMap3 = jsonObject.getDouble("Latitude");
                            mLngOfMap3 = jsonObject.getDouble("Longitude");
                            mLatLngForMap3 = new LatLng(mLatOfMap3, mLngOfMap3);

                            if (isFirstIn) {
                                mBaiduMapOfMap3.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                                        .zoom(19).target(mLatLngForMap3).build()));
                            }
                            //依据位置数据添加覆盖物
                            if (mMarkerOfMap3 == null) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin);
                                markerOptions.position(mLatLngForMap3)
                                        .title(mNamoOfMap3)
                                        .icon(icon)            // 图标
                                        .draggable(true);    // 设置图标可以拖动
                                //实例化覆盖物对象
                                mMarkerOfMap3 = (Marker) mBaiduMapOfMap3.addOverlay(markerOptions);
                            }
                            //若已经有了标志覆盖物，改变位置即可
                            else {
                                mMarkerOfMap3.setPosition(mLatLngForMap3);
                            }
                        } else {
                            if (isFirstIn) {
                                Message message = new Message();
                                message.obj = "未连接到服务器";
                                handler.sendMessage(message);
                            }
                        }
                    } catch (Exception e) {
                        if (isFirstIn) {
                            Message message = new Message();
                            message.obj = "未连接到服务器";
                            handler.sendMessage(message);
                        }
                        e.printStackTrace();
                    }
                    isFirstIn = false;
                }
            };
            mTimerOfMap3.schedule(tt, 1000, 1000);
        }
    }
}

