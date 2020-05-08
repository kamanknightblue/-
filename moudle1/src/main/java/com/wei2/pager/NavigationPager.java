package com.wei2.pager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.baidu.mapapi.map.MapPoi;
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
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
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
import com.wei2.utils.LogUtils;
import com.wei2.utils.NetUtils;
import com.wei2.utils.overlayutil.BikingRouteOverlay;
import com.wei2.utils.overlayutil.DrivingRouteOverlay;
import com.wei2.utils.overlayutil.TransitRouteOverlay;
import com.wei2.utils.overlayutil.WalkingRouteOverlay;
import com.wei2.view.MySearchView;
import com.wei2.view.OnSearchListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NavigationPager {
    public Activity mActivity;
    public TextView tvTitle;
    public FrameLayout flContent;
    public final View mRootView;
    private View mapContent;
    /**
     * 我的自定义搜索栏
     */
    private MySearchView mSearchView;
    /**
     * 地图图层
     */
    private MapView mMapView;
    /**
     * 地图控制器
     */
    private BaiduMap mBaiduMap;
    /**
     * 地图将会改变的目标状态
     */
    private MapStatusUpdate mMapStatusUpdate;
    /**
     * 我的定位点数据
     */
    private MyLocationData mMyLocationData;
    /**
     * 定位数据
     */
    private BDLocation bdLocation = LocationPager.bdLocation;
    /**
     * POI显示列表
     */
    private ListView listView;
    /**
     * 定位按钮点击数是否奇数的标识
     */
    boolean isOdd = false;
    /**
     * 定位按钮
     */
    private Button mBtnLocate;
    /**
     * Poi列表
     */
    private List<PoiInfo> poiInfoList;
    /**
     * 当前被选中的POI
     */
    PoiInfo mPoiInfo;
    /**
     * 路线规划搜索工具
     */
    private RoutePlanSearch mRoutePlanSearch;
    /**
     * 标志覆盖物上的气泡
     */
    private View mPop;
    /**
     * 弹出气泡的详情图标
     */
    private ImageView mIvDetailOfmPop;
    /**
     * 弹出气泡的标题
     */
    private TextView mTvPopTitle;
    /**
     * 弹出气泡的路线图标
     */
    private ImageView mIvRoadOfmPop;
    /**
     * 路线规划结果列表
     */
    private ListView mLvRouteResult;
    /**
     * 覆盖物集合
     */
    List<Marker> mMarkerList;
    /**
     * 当前被点击的覆盖物
     */
    Marker curMarker;
    /**
     * 驾车出行方式的覆盖物
     */
    DrivingRouteOverlay mDrivingRouteOverlay;
    /**
     * 步行出行方式覆盖物
     */
    WalkingRouteOverlay mWalkingRouteOverlay;
    /**
     * 骑行出行方式覆盖物
     */
    BikingRouteOverlay mBikingRouteOverlay;
    /**
     * 公交出行方式覆盖物
     */
    TransitRouteOverlay mTransitRouteOverlay;
    /**
     * 路线搜索结果对话框
     */
    private AlertDialog mDialogRouteResult;
    /**
     * 选择出行方式的对话框
     */
    private AlertDialog mDialogTripMode;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.obj.toString().equals("导航启动失败，该路线不支持导航")) {
                Toast.makeText(mActivity, "导航启动失败，该路线不支持导航", Toast.LENGTH_SHORT).show();
            }
        }

    };
    /**
     * 位置点的相关信息
     */
    private AlertDialog mDetailDialog;
    private Button mBtnTraffic;


    public NavigationPager(Activity activity) {
        mActivity = activity;
        mRootView = initView();
        initData();
        initListener();
    }

    /**
     * 初始化组件，包括MapView、MySearchView
     *
     * @return 返回根View
     */
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager, null);

        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("导航服务");
        flContent = view.findViewById(R.id.fl_content);

        mapContent = View.inflate(mActivity, R.layout.layout_map, null);
        //POI内容列表
        listView = mapContent.findViewById(R.id.lv_search_content);
        //定位按钮
        mBtnLocate = mapContent.findViewById(R.id.btn_locate);
        //获取地图图层
        mMapView = mapContent.findViewById(R.id.bmapView0);
        //获取搜索栏
        mSearchView = mapContent.findViewById(R.id.sv_default);

        mSearchView.setVisibility(View.VISIBLE);

        //交通图层开关
        mBtnTraffic = mapContent.findViewById(R.id.btn_traffic_layer);
        mBtnTraffic.setVisibility(View.VISIBLE);

        flContent.addView(mapContent);
        return view;
    }

    /**
     * 初始化数据，包括当前经纬度位置，地图状态
     */
    public void initData() {
        //实例化地图控制器
        mBaiduMap = mMapView.getMap();
        //初始化路线搜索工具
        mRoutePlanSearch = RoutePlanSearch.newInstance();
        //关闭比例尺
        mMapView.showScaleControl(false);
        //初始化指南针
        mBaiduMap.setCompassEnable(true);
        android.graphics.Point point = new android.graphics.Point(100, 300);
        mBaiduMap.setCompassPosition(point);
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //初始化覆盖物集合
        mMarkerList = new ArrayList<>();

        //定位点配置
        MyLocationConfiguration myLocationConfiguration =
                new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                        true, BitmapDescriptorFactory.fromResource(R.mipmap.navigation_blue));
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);

        //初始化地图状态
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                .target(LocationPager.DEFAULTLatLng_BD09).zoom(18).build()));
        //设置定时器，1s后以1s为周期更新数据
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            //为第一次计时设置标识
            boolean isFirstTime = true;

            public void run() {
                //实时获取从定位页面传来的定位数据
                bdLocation = LocationPager.bdLocation;
                //接收从定位页面的定位点数据
                mMyLocationData = LocationPager.mLocData;
                mBaiduMap.setMyLocationData(mMyLocationData);
                if (bdLocation != null && mMyLocationData != null && isFirstTime) {
                    //更新地图状态
                    mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                            .target(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()))
                            .zoom(18).build());
                    mBaiduMap.setMapStatus(mMapStatusUpdate);
                    isFirstTime = false;
                }
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    /**
     * 初始化监听，包括搜索事件
     */
    public void initListener() {
        //搜索栏搜索事件，主要获取到搜索的字符串然后进行POI检索
        mSearchView.setOnSearchListener(new OnSearchListener() {
            public void search(String content) {
                //无网络状态不执行任何操作，直接return
                if (!NetUtils.networkConnected(mActivity)) {
                    Toast.makeText(mActivity, "无网络连接，搜索失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mPop!=null){
                    mPop.setVisibility(View.INVISIBLE);
                }
                if (bdLocation == null) {
                    Toast.makeText(mActivity, "无位置数据，搜索失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                //默认在当前城市内搜索POI，城市从bdLocation中获取，所有需判断下bdLocation
                if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                    Toast.makeText(mActivity, "搜索中...", Toast.LENGTH_SHORT).show();
                    PoiSearch poiSearch = PoiSearch.newInstance();
                    poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                        public void onGetPoiResult(PoiResult poiResult) {
                            if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                                Toast.makeText(mActivity, "没有搜索到结果", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            mBaiduMap.clear();
                            poiInfoList = poiResult.getAllPoi();
                            //加入覆盖物前先清空
                            mMarkerList.clear();
                            //遍历poi集合，在地图上添poi，将poi对应marker加入覆盖物集合mMarkerList
                            for (PoiInfo poiInfo : poiInfoList) {

                                //添加poi覆盖物
                                MarkerOptions options = new MarkerOptions();
                                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin);
                                options.position(poiInfo.getLocation())
                                        .icon(icon)            // 图标
                                        .draggable(true)    // 设置图标可以拖动
                                        .title(poiInfo.getName());
                                mMarkerList.add((Marker) mBaiduMap.addOverlay(options));
                            }
                            //在listview里展示搜索结果
                            listView.setAdapter(new BaseAdapter() {
                                @Override
                                public int getCount() {
                                    return poiInfoList.size();
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
                                    View view;
                                    if (convertView == null) {
                                        view = View.inflate(mActivity.getApplicationContext(), R.layout.item_poisearch_result, null);
                                    } else {
                                        view = convertView;
                                    }
                                    TextView tvName = view.findViewById(R.id.tv_poi_item_name);
                                    TextView tvAddress = view.findViewById(R.id.tv_poi_item_address);
                                    tvName.setText(poiInfoList.get(position).getName());
                                    tvAddress.setText(poiInfoList.get(position).getAddress());
                                    return view;
                                }

                            });
                            listView.setVisibility(View.VISIBLE);
                            ;

                        }

                        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                        }

                        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
                        }

                        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                        }
                    });
                    poiSearch.searchInCity(new PoiCitySearchOption().city(bdLocation.getCity()).keyword(content));

                } else {
                    Toast.makeText(mActivity, "没有当前位置，搜索失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //搜索框点击事件，当点击时展示已有的ListView
        mSearchView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);
            }
        });

        //POI列表点击事件,点击poi项目跳转到对应坐标未知，但此前要删除地图上所有覆盖物，从新添加覆盖物
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBaiduMap.clear();
                //加入覆盖物前先清空
                mMarkerList.clear();

                for (PoiInfo poiInfo : poiInfoList) {

                    //添加poi覆盖物
                    MarkerOptions options = new MarkerOptions();
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin);
                    options.position(poiInfo.getLocation())
                            .icon(icon)            // 图标
                            .draggable(true)    // 设置图标可以拖动
                            .title(poiInfo.getName());
                    mMarkerList.add((Marker) mBaiduMap.addOverlay(options));
                }


                mPoiInfo = poiInfoList.get(position);
                mBaiduMap.setMyLocationConfiguration(
                        new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                                true, BitmapDescriptorFactory.fromResource(R.mipmap.navigation_blue)));
                mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                        .target(poiInfoList.get(position).getLocation()).zoom(18).build());
                mBaiduMap.animateMapStatus(mMapStatusUpdate, 2000);
            }
        });

        //地图单击事件的监听
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mPop != null) {
                    mPop.setVisibility(View.INVISIBLE);
                }
                listView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });

        //定位按钮点击事件
        mBtnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPop != null) {
                    mPop.setVisibility(View.INVISIBLE);
                }
                if (bdLocation == null || bdLocation.getLocType() == 62 || bdLocation.getLocType() == 63) {
                    Toast.makeText(mActivity, "未获取到位置", Toast.LENGTH_SHORT).show();
                } else {
                    mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().
                            target(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())).
                            zoom(19).
                            build());
                    mBaiduMap.animateMapStatus(mMapStatusUpdate, 2500);
                    mBaiduMap.setMyLocationData(mMyLocationData);
                    if (isOdd) {
                        MyLocationConfiguration.LocationMode mode = MyLocationConfiguration.LocationMode.NORMAL;
                        boolean enableDirection = true;
                        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.mipmap.navigation_blue);
                        MyLocationConfiguration mc = new MyLocationConfiguration(mode, enableDirection, customMarker);
                        mBaiduMap.setMyLocationConfiguration(mc);
                        isOdd = false;
                        mBtnLocate.setText("定位");
                    } else {
                        MyLocationConfiguration.LocationMode mode = MyLocationConfiguration.LocationMode.COMPASS;
                        boolean enableDirection = true;
                        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.mipmap.navigation_blue);
                        MyLocationConfiguration mc = new MyLocationConfiguration(mode, enableDirection, customMarker);
                        mBaiduMap.setMyLocationConfiguration(mc);
                        isOdd = true;
                        mBtnLocate.setText("罗盘");
                    }
                }

            }
        });

        //标志覆盖物点击事件,弹出气泡，并给气泡的图标设置点击监听，弹出四种出行方式图标，再给出行方式图标设置监听
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            boolean popToggle = true;

            @Override
            public boolean onMarkerClick(Marker marker) {
                //只有在覆盖物集合里才显示气泡
                if (mMarkerList.contains(marker)) {
                    curMarker = marker;
                    for (int i = 0; i < mMarkerList.size(); i++) {
                        if (mMarkerList.get(i) == curMarker) {
                            mPoiInfo = poiInfoList.get(i);
                        }
                    }
                    if (mPop == null) {
                        mPop = View.inflate(mActivity, R.layout.pop, null);
                        MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                                .position(marker.getPosition())
                                .yOffset(-90)
                                .build();
                        //实例化详细图标
                        mIvDetailOfmPop = mPop.findViewById(R.id.iv_detail);
                        //实例化路线图标
                        mIvRoadOfmPop = mPop.findViewById(R.id.iv_navigate_method);
                        //气泡的标题组件
                        mTvPopTitle = mPop.findViewById(R.id.tv_title);
                        //将气泡加入地图中
                        mMapView.addView(mPop, params);

                        mIvDetailOfmPop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDetailDialog = new AlertDialog.Builder(mActivity)
                                        .setItems(new String[]{
                                                "名称：" + mPoiInfo.getName(),
                                                "地址：" + mPoiInfo.getAddress(),
                                                "电话：" + mPoiInfo.getPhoneNum(),
                                                "坐标(bd09)：\n" + mPoiInfo.getLocation()}, null)
                                        .setTitle("相关信息")
                                        .create();

                                mDetailDialog.show();

                            }
                        });
                        //为气泡里的路线图标添加监听
                        mIvRoadOfmPop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //初始化对话框的view
                                View view = View.inflate(mActivity, R.layout.dialog_navigate_mode, null);
                                mDialogTripMode = new AlertDialog.Builder(mActivity)
                                        .setTitle("请选择出行方式")
                                        .setView(view)
                                        .create();
                                mDialogTripMode.show();
                                //实例化步行图标
                                ImageView ivWalk = view.findViewById(R.id.iv_walk);
                                //实例化骑行图标
                                ImageView ivBike = view.findViewById(R.id.iv_bike);
                                //实例化驾车图标
                                ImageView ivCar = view.findViewById(R.id.iv_car);
                                //实例化公交图标
//                                ImageView ivBus = view.findViewById(R.id.iv_bus);

                                //步行图标设置监听：弹出搜索结果对话框
                                ivWalk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(mActivity, "搜索中...", Toast.LENGTH_SHORT).show();
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResult = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResult = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResult.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.walkingSearch(new WalkingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(curMarker.getPosition())));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //骑行按钮监听
                                ivBike.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(mActivity, "搜索中...", Toast.LENGTH_SHORT).show();
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResult = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResult = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResult.show();
                                        //有当前位置才能启动路线搜索,所有需对bdLocation进行判断
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.bikingSearch(new BikingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(curMarker.getPosition())));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //驾车图标设置监听：弹出搜索结果对话框
                                ivCar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(mActivity, "搜索中...", Toast.LENGTH_SHORT).show();
                                        View viewRouteResult = View.inflate(mActivity, R.layout.dialog_route_result, null);
                                        mLvRouteResult = viewRouteResult.findViewById(R.id.lv_route_result);
                                        mDialogRouteResult = new AlertDialog.Builder(mActivity)
                                                .setTitle("搜索结果")
                                                .setView(viewRouteResult)
                                                .create();
                                        mDialogRouteResult.show();
                                        //有当前位置才能启动路线搜索
                                        if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                                            mRoutePlanSearch.drivingSearch(new DrivingRoutePlanOption()
                                                    .from(PlanNode.withLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())))
                                                    .to(PlanNode.withLocation(curMarker.getPosition())));
                                        } else {
                                            Toast.makeText(mActivity, "未获取到当前位置，无法规划路线", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //公交按钮监听
//                                ivBus.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                    }
//                                });
                            }
                        });
                    } else {
                        mPop.setVisibility(View.VISIBLE);
                        MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                                .position(marker.getPosition())
                                .yOffset(-90)
                                .build();
                        mMapView.updateViewLayout(mPop, params);
                    }
                    mTvPopTitle.setText(marker.getTitle());
                }
                return true;
            }
        });

        //路线规划监听，将路线搜索结果展示在ListView,并给ListView结果选项添加监听
        mRoutePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            //驾车方式回调函数
            public void onGetDrivingRouteResult(final DrivingRouteResult drivingRouteResult) {
                if (drivingRouteResult != null && drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    //为搜索结果设置适配器，将搜索的结果添加进列表
                    mLvRouteResult.setAdapter(new BaseAdapter() {
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
                            tvTime.setText("时间：" + "大约" + (drivingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                            tvDis.setText("路程：" + "大约" + (drivingRouteResult.getRouteLines().get(position).getDistance() / 1000) + "公里");
                            return view;
                        }
                    });
                    //搜索结果列表项添加事件监听，当点击其中某项时将具体路线在地图上画出来
                    //画出路线后，退出路线结果展示对话框，退出路线方式选择对话框，清除依据poi添加的覆盖物
                    mLvRouteResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            if (mDrivingRouteOverlay != null) {
                                mDrivingRouteOverlay.removeFromMap();
                            }
                            mDrivingRouteOverlay = new DrivingRouteOverlay(mBaiduMap);
                            mBaiduMap.setOnMarkerClickListener(mDrivingRouteOverlay);
                            mDrivingRouteOverlay.setData(drivingRouteResult.getRouteLines().get(position));
                            mDrivingRouteOverlay.addToMap();
                            mDrivingRouteOverlay.zoomToSpan();

                            mDialogRouteResult.dismiss();
                            mDialogTripMode.dismiss();
                            for (Marker marker : mMarkerList) {
                                marker.remove();
                            }
                            mPop.setVisibility(View.INVISIBLE);
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
                                                    endPt.setLocation(drivingRouteResult.getRouteLines().get(position).getTerminal().getLocation());
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

                } else {
                    Toast.makeText(mActivity, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            //步行方式回调
            public void onGetWalkingRouteResult(final WalkingRouteResult walkingRouteResult) {
                if (walkingRouteResult != null && walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    //为搜索结果设置适配器，将搜索的结果添加进列表
                    mLvRouteResult.setAdapter(new BaseAdapter() {
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
                            tvTime.setText("时间：" + "大约" + (walkingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                            tvDis.setText("路程：" + "大约" + (walkingRouteResult.getRouteLines().get(position).getDistance() / 1000) + "公里");
                            return view;
                        }
                    });
                    //搜索结果列表项添加事件监听，当点击其中某项时将具体路线在地图上画出来
                    //画出路线后，退出路线结果展示对话框，退出路线方式选择对话框，清除依据poi添加的覆盖物
                    mLvRouteResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            //如过已经添加过一次覆盖物，需要清除掉
                            if (mWalkingRouteOverlay != null) {
                                mWalkingRouteOverlay.removeFromMap();
                            }
                            mWalkingRouteOverlay = new WalkingRouteOverlay(mBaiduMap);
                            mBaiduMap.setOnMarkerClickListener(mWalkingRouteOverlay);
                            mWalkingRouteOverlay.setData(walkingRouteResult.getRouteLines().get(position));
                            mWalkingRouteOverlay.addToMap();
                            mWalkingRouteOverlay.zoomToSpan();

                            mDialogRouteResult.dismiss();
                            mDialogTripMode.dismiss();
                            for (Marker marker : mMarkerList) {
                                marker.remove();
                            }
                            mPop.setVisibility(View.INVISIBLE);

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
                                                    endPt.setLocation(walkingRouteResult.getRouteLines().get(position).getTerminal().getLocation());
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
                                                            Message message = new Message();
                                                            message.obj = "导航启动失败，该路线不支持导航";
                                                            handler.sendMessage(message);

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

                } else {
                    Toast.makeText(mActivity, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            //公交方式回调
            public void onGetTransitRouteResult(final TransitRouteResult transitRouteResult) {
                if (transitRouteResult != null && transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    //为搜索结果设置适配器，将搜索的结果添加进列表
                    mLvRouteResult.setAdapter(new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return transitRouteResult.getRouteLines().size();
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
                            tvName.setText(transitRouteResult.getRouteLines().get(position).getTitle());
                            tvTime.setText("时间：" + "大约" + (transitRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                            tvDis.setText("路程：" + "大约" + (transitRouteResult.getRouteLines().get(position).getDistance() / 1000) + "公里");
                            return view;
                        }
                    });
                    //搜索结果列表项添加事件监听，当点击其中某项时将具体路线在地图上画出来
                    //画出路线后，退出路线结果展示对话框，退出路线方式选择对话框，清除依据poi添加的覆盖物
                    mLvRouteResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //如过已经添加过一次覆盖物，需要清除掉
                            if (mTransitRouteOverlay != null) {
                                mTransitRouteOverlay.removeFromMap();
                            }
                            mTransitRouteOverlay = new TransitRouteOverlay(mBaiduMap);
                            mBaiduMap.setOnMarkerClickListener(mWalkingRouteOverlay);
                            mTransitRouteOverlay.setData(transitRouteResult.getRouteLines().get(position));
                            mTransitRouteOverlay.addToMap();
                            mTransitRouteOverlay.zoomToSpan();

                            mDialogRouteResult.dismiss();
                            mDialogTripMode.dismiss();
                            for (Marker marker : mMarkerList) {
                                marker.remove();
                            }
                            mPop.setVisibility(View.INVISIBLE);
                        }
                    });

                } else {
                    Toast.makeText(mActivity, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
            }

            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
            }

            //骑行方式回调方法
            public void onGetBikingRouteResult(final BikingRouteResult bikingRouteResult) {
                if (bikingRouteResult != null && bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    //为搜索结果设置适配器，将搜索的结果添加进列表
                    mLvRouteResult.setAdapter(new BaseAdapter() {
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
                            tvTime.setText("时间：" + "大约" + (bikingRouteResult.getRouteLines().get(position).getDuration() / 60) + "分钟");
                            tvDis.setText("路程：" + "大约" + (bikingRouteResult.getRouteLines().get(position).getDistance() / 1000) + "公里");
                            return view;
                        }
                    });
                    //搜索结果列表项添加事件监听，当点击其中某项时将具体路线在地图上画出来
                    //画出路线后，退出路线结果展示对话框，退出路线方式选择对话框，清除依据poi添加的覆盖物
                    mLvRouteResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            if (mBikingRouteOverlay != null) {
                                mBikingRouteOverlay.removeFromMap();
                            }
                            mBikingRouteOverlay = new BikingRouteOverlay(mBaiduMap);
                            mBaiduMap.setOnMarkerClickListener(mBikingRouteOverlay);
                            mBikingRouteOverlay.setData(bikingRouteResult.getRouteLines().get(position));
                            mBikingRouteOverlay.addToMap();
                            mBikingRouteOverlay.zoomToSpan();

                            mDialogRouteResult.dismiss();
                            mDialogTripMode.dismiss();
                            for (Marker marker : mMarkerList) {
                                marker.remove();
                            }
                            mPop.setVisibility(View.INVISIBLE);
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
                                                    endPt.setLocation(bikingRouteResult.getRouteLines().get(position).getTerminal().getLocation());
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

                } else {
                    Toast.makeText(mActivity, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        //交通图层按钮
        mBtnTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(false);
                    Toast.makeText(mActivity,"关闭路况图",Toast.LENGTH_SHORT).show();
                }else {
                    mBaiduMap.setTrafficEnabled(true);
                    Toast.makeText(mActivity,"开启路况图",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

