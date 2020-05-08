package com.wei2.pager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.wei2.bikenavi.R;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.wei2.utils.LocationUtils;
import com.wei2.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张盛威
 * 定位页面
 */
public class LocationPager {
    public Activity mActivity;
    public TextView tvTitle;
    public FrameLayout flContent;
    public final View mRootView;
    private View mapContent;
    private MapView mMapView;
    /**
     * 设置默认显示位置，gps84坐标
     */
    private static final LatLng DEFAULTLatLng = new LatLng(28.324, 113.078);
    /**
     * 设置默认显示位置，bd09坐标
     */
    public static final LatLng DEFAULTLatLng_BD09 =
            new LatLng(LocationUtils.gps84_To_bd09(DEFAULTLatLng.latitude, DEFAULTLatLng.longitude)[0],
                    LocationUtils.gps84_To_bd09(DEFAULTLatLng.latitude, DEFAULTLatLng.longitude)[1]);
    /**
     * 当前纬度
     */
    public static double curLatitude;
    /**
     * 当前经度
     */
    public static double curLongitude;
    /**
     * 地图控制器
     */
    private BaiduMap mBaiduMap;
    /**
     * 定位按钮，点击即可定位
     */
    private Button mbtn_locate;
    /**
     * 当前地址的字符串描述
     */
    private String curAddressStr;
    /**
     * 默认的地址字符串形式描述
     */
    private final static String DEFAULTADDRESSSTR = "未知位置";
    /**
     * 当前方向传感器的方向
     */
    public static float mCurrentZ;
    /**
     * 我的当前位置信息
     */
    public static MyLocationData mLocData = new MyLocationData.Builder().latitude(DEFAULTLatLng_BD09.latitude)
            .longitude(DEFAULTLatLng_BD09.longitude).build();
    /**
     * 是否第一次开启程序
     */
    private boolean isFirstIn = true;
    /**
     * 定位点的相关配置
     */
    private MyLocationConfiguration mMyLocationConfiguration;
    /**
     * 定位按钮点击数是否奇数的标识
     */
    boolean isOdd = false;
    /**
     * 地图将会更新到的状态
     */
    private MapStatusUpdate mMapStatusUpdate;
    /**
     * 定位数据
     */
    public static BDLocation bdLocation;
    /**
     * 定位客户端
     */
    private LocationClient mLocationClient;

    public LocationPager(Activity activity) {
        mActivity = activity;
        mRootView = initView();
        requestPermission();
        initData();
        initListener();
    }


    /**
     * 初始化组件
     *
     * @return 返回根组件
     */
    public View initView() {
        //获取布局文件，并转为view
        View view = View.inflate(mActivity, R.layout.pager, null);
        //获取顶部的标题栏
        tvTitle = view.findViewById(R.id.tv_title);
        //获取标题栏之下的容器部分
        flContent = view.findViewById(R.id.fl_content);
        //取得有地图容器的布局文件
        mapContent = View.inflate(mActivity, R.layout.layout_map, null);
        //定位按钮
        mbtn_locate = mapContent.findViewById(R.id.btn_locate);
        //获取地图MapView
        mMapView = mapContent.findViewById(R.id.bmapView0);
        //取得地图控制器
        mBaiduMap = mMapView.getMap();

        flContent.addView(mapContent);
        return view;
    }

    /**
     * 初始化数据
     */
    public void initData() {
        //顶部标题栏
        tvTitle.setText("位置信息");
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //开启指南针
        mBaiduMap.setCompassEnable(true);
        //把比例尺和缩放按钮隐藏
        mMapView.showScaleControl(false);
        mMapView.showZoomControls(false);
        //配置我的定位点
        mMyLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                true, BitmapDescriptorFactory.fromResource(R.mipmap.navigation_blue));
        mBaiduMap.setMyLocationConfiguration(mMyLocationConfiguration);
        mBaiduMap.setMyLocationData(mLocData);
        //初始化地图状态
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                .target(DEFAULTLatLng_BD09).zoom(17).build()));

        //初始化定位客户端
        mLocationClient = new LocationClient(mActivity);

        final LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);

        mLocationClient.setLocOption(option);

        mLocationClient.start();

    }


    /**
     * 初始化一些监听
     */
    void initListener() {
        //定位按钮设置点击监听，用来改变地图状态
        mbtn_locate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mMapStatusUpdate = null;
                if (bdLocation!=null&&(bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161)) {
                    Toast.makeText(mActivity, curAddressStr, Toast.LENGTH_SHORT).show();
                    //设置地图状态
                    mMapStatusUpdate = MapStatusUpdateFactory.newLatLng(new LatLng(curLatitude, curLongitude));
                    mBaiduMap.animateMapStatus(mMapStatusUpdate, 2500);
                    mMapStatusUpdate = MapStatusUpdateFactory.zoomTo(20);
                    mBaiduMap.animateMapStatus(mMapStatusUpdate, 1000);
                    mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().overlook(-50).build());
                    mBaiduMap.animateMapStatus(mMapStatusUpdate, 1000);
                }
                else {
                    Toast.makeText(mActivity, "无法获取位置,请开启位置服务", Toast.LENGTH_SHORT).show();
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(DEFAULTLatLng_BD09), 2500);
                    return ;
                }
                if (isOdd) {
                    isOdd = false;
                    mMyLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                            true, BitmapDescriptorFactory.fromResource(R.mipmap.navigation_blue));
                    mBaiduMap.setMyLocationConfiguration(mMyLocationConfiguration);
                    //改变定位按钮文本
                    mbtn_locate.setText("定位");
                }
                else {
                    isOdd = true;
                    mMyLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS,
                            true, BitmapDescriptorFactory.fromResource(R.mipmap.navigation_blue));
                    mBaiduMap.setMyLocationConfiguration(mMyLocationConfiguration);
                    mbtn_locate.setText("罗盘");
                }
            }
        });
        //定位客户端监听定位数据
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            int count = 1;

            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null || mMapView == null) {
                    return;
                }
                bdLocation = location;
                mLocData = null;
                switch (location.getLocType()) {
                    //卫星定位和网络定位时执行的操作
                    case BDLocation.TypeGpsLocation:
                    case BDLocation.TypeNetWorkLocation:
                        curLatitude = location.getLatitude();
                        curLongitude = location.getLongitude();
                        curAddressStr = location.getAddrStr();
                        mLocData = new MyLocationData.Builder()
                                .accuracy(location.getRadius())
                                .direction(getCurrentZ())
                                .latitude(curLatitude)
                                .longitude(curLongitude).build();

                        if (isFirstIn) {
                            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().
                                    target(new LatLng(curLatitude, curLongitude)).zoom(19).build()));
                            isFirstIn = false;
                        }
                        break;

                    case BDLocation.TypeCriteriaException:
                    case BDLocation.TypeOffLineLocation:
                        Log.e("定位结果", "TypeOffLineLocation");
                    case BDLocation.TypeNone:
                        Log.e("定位结果", "TypeNone");
                    case BDLocation.TypeServerCheckKeyError:
                        Log.e("定位结果", "TypeServerCheckKeyError");
                        //网络定位异常时执行的操作
                    case BDLocation.TypeNetWorkException:
                        Log.e("定位结果", "TypeNetWorkException");
                        if (isFirstIn) {
                            Toast.makeText(mActivity, "无法获取位置,请开启位置服务", Toast.LENGTH_SHORT).show();
                            isFirstIn = false;
                        }
                        curLatitude = DEFAULTLatLng_BD09.latitude;
                        curLongitude = DEFAULTLatLng_BD09.longitude;
                        curAddressStr = DEFAULTADDRESSSTR;
                        mLocData = new MyLocationData.Builder()
                                .accuracy(50)
                                .direction(getCurrentZ())
                                .latitude(curLatitude)
                                .longitude(curLongitude).build();
                        break;
                }
                LogUtils.e("定位次数", "" + count + "\n");
                count++;
                mBaiduMap.setMyLocationData(mLocData);
            }
        });

    }


    /**
     * 根据方向传感器获取方向,为成员变量mCurrentZ赋值
     *
     * @return 当前方向
     */
    float getCurrentZ() {

        SensorManager sensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(new SensorEventListener() {

            public void onSensorChanged(SensorEvent event) {
                mCurrentZ = event.values[0];
//                LogUtils.e("方向", "" + mCurrentZ);
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, orientationSensor, SensorManager.SENSOR_DELAY_UI);
        return mCurrentZ;
    }

    /**
     * 动态获取一些权限
     */
    private void requestPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(mActivity, permissions, 1);
        }
    }

}
