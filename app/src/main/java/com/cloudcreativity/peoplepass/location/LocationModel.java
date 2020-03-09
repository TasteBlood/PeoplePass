package com.cloudcreativity.peoplepass.location;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseApp;
import com.cloudcreativity.peoplepass.databinding.ActivityLocationBinding;
import com.cloudcreativity.peoplepass.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationModel {


    public ObservableField<Boolean> isShow = new ObservableField<>();
    public ObservableField<String> placeText = new ObservableField<>();

    private LocationActivity context;
    private LocationClient locationClient;
    private GeoCoder geoCoder;
    private BaiduMap map;
    private ActivityLocationBinding binding;
    public BaseAdapter poiAdapter;
    private List<Map<String,String>> poiList = new ArrayList<>();
    private Marker marker;


    LocationModel(final LocationActivity context, ActivityLocationBinding binding) {
        this.context = context;
        this.binding = binding;
        this.isShow.set(false);

        map = this.binding.mvLocation.getMap();
        map.setIndoorEnable(true);

        geoCoder = GeoCoder.newInstance();

        //拖动选择位置
        map.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if(marker==LocationModel.this.marker){
                    //说明拖动的是当前的点
                    poiList.clear();
                    LogUtils.e("xuxiwu","dragged  latitude="+marker.getPosition().latitude+",longitude="+marker.getPosition().longitude);
                    reverseGeocode(marker.getPosition());
                }
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });

        //点击选择位置
        map.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                poiList.clear();
                if(marker!=null)
                    marker.setPosition(latLng);
                reverseGeocode(latLng);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        //地理编码与反地理编码
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                poiList.clear();
                if(geoCodeResult!=null&&geoCodeResult.error==SearchResult.ERRORNO.NO_ERROR){
                    LogUtils.e("xuxiwu",geoCodeResult.getLocation().latitude+"-"+geoCodeResult.getLocation().longitude);
                    reverseGeocode(geoCodeResult.getLocation());
                    if(marker==null){
                        //重新生成marker
                        MarkerOptions options = new MarkerOptions()
                                .zIndex(9)
                                .draggable(true)
                                .position(geoCodeResult.getLocation())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location))
                                .animateType(MarkerOptions.MarkerAnimateType.grow);
                        marker = (Marker) map.addOverlay(options);
                    }else{
                        marker.setPosition(geoCodeResult.getLocation());
                    }
                    map.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(geoCodeResult.getLocation(),18f));
                }
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                poiList.clear();
                if(reverseGeoCodeResult!=null&&reverseGeoCodeResult.error==SearchResult.ERRORNO.NO_ERROR){
                    for(PoiInfo info : reverseGeoCodeResult.getPoiList()){
                        LogUtils.e("xuxiwu",info.name+"\n"+info.address);
                        Map<String,String> data = new HashMap<>();
                        data.put("name",info.getName());
                        data.put("address",info.getAddress());
                        data.put("province",info.getProvince());
                        data.put("city",info.getCity());
                        data.put("area",info.getArea());
                        data.put("street",info.getName());
                        data.put("latitude",String.valueOf(info.getLocation().latitude));
                        data.put("longitude",String.valueOf(info.getLocation().longitude));
                        poiList.add(data);
                    }
                    poiAdapter.notifyDataSetChanged();
                }
            }
        });



        poiAdapter = new SimpleAdapter(context,
                poiList,
                R.layout.item_layout_location,
                new String[]{"name","address"},
                new int[]{R.id.tv_name,R.id.tv_address});

        startLocation();

        binding.lvLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //将当前选中的地址返回
                Map<String, String> map = poiList.get(position);
                Intent data = new Intent();
                data.putExtra("province",map.get("province"));
                data.putExtra("city",map.get("city"));
                data.putExtra("area",map.get("area"));
                data.putExtra("name",map.get("name"));
                data.putExtra("street",map.get("street"));
                data.putExtra("address",map.get("address"));
                data.putExtra("latitude",Double.parseDouble(map.get("latitude")));
                data.putExtra("longitude",Double.parseDouble(map.get("longitude")));

                context.setResult(Activity.RESULT_OK,data);
                context.finish();
            }
        });
    }

    public void onCloseClick(){
        context.finish();
    }

    public void onClearClick(){
        placeText.set(null);
    }

    private void startLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedLocationPoiList(true);
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        locationClient = new LocationClient(BaseApp.app,option);
        locationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if(bdLocation!=null){
                    //标记当前的位置
                    MarkerOptions options = new MarkerOptions()
                            .zIndex(9)
                            .draggable(true)
                            .position(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location))
                            .animateType(MarkerOptions.MarkerAnimateType.grow);
                    marker = (Marker) map.addOverlay(options);
                    map.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()),18f));

                    //处理poi数据
                    reverseGeocode(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()));
                }
            }
        });
        locationClient.start();
    }

    private void startGeoCoder(String location){
        //先地理编码，然后进行poi搜索
        GeoCodeOption option = new GeoCodeOption();
        option.city("甘肃兰州");
        option.address(location);


        geoCoder.geocode(option);
    }

//    private void startPoiSearch(LatLng latLng){
//        PoiNearbySearchOption option = new PoiNearbySearchOption();
//        option.radius(1500);
//        option.keyword("乡镇街道路");
//        option.location(latLng);
//        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
//            @Override
//            public void onGetPoiResult(PoiResult poiResult) {
//                if(poiResult!=null&&poiResult.error==SearchResult.ERRORNO.NO_ERROR){
//                    for(PoiInfo info : poiResult.getAllPoi()){
//                        LogUtils.e("xuxiwu",info.name+"\n"+info.address);
//                        Map<String,String> data = new HashMap<>();
//                        data.put("name",info.getName());
//                        data.put("address",info.getAddress());
//                        data.put("province",info.getProvince());
//                        data.put("city",info.getCity());
//                        data.put("area",info.getArea());
//                        data.put("street",info.getName());
//                        data.put("latitude",String.valueOf(info.getLocation().latitude));
//                        data.put("longitude",String.valueOf(info.getLocation().longitude));
//                        poiList.add(data);
//                    }
//
//                    poiAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
//                LogUtils.e("xuxiwu","get poi detail result");
//            }
//
//            @Override
//            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
//                LogUtils.e("xuxiwu","get poi detail search result");
//            }
//
//            @Override
//            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
//                LogUtils.e("xuxiwu","get poi indoor result");
//            }
//        });
//
//        poiSearch.searchNearby(option);
//    }

    /**
     * 反地理编码就可以进行poi获取
     */
    private void reverseGeocode(LatLng latLng){
        ReverseGeoCodeOption option = new ReverseGeoCodeOption();
        option.location(latLng);
        option.radius(1000);

        geoCoder.reverseGeoCode(option);
    }

    public TextWatcher MyTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!TextUtils.isEmpty(s)){
                isShow.set(true);
                startGeoCoder(s.toString());
            }else{
                isShow.set(false);
            }
        }
    } ;

    void destroy(){
        if(locationClient!=null){
            locationClient.stop();
            locationClient = null;
        }
    }
}
