package com.pineapple.mobilecraft.tumcca.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.MallCityBean;
import com.pineapple.mobilecraft.tumcca.data.MallProvinceBean;
import com.pineapple.mobilecraft.tumcca.view.PinnedHeaderExpandableListView;
import android.widget.AbsListView.LayoutParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiankun on 2015/7/9.
 */
public class UserCity extends DialogFragment implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener,
        PinnedHeaderExpandableListView.OnHeaderUpdateListener{

    private Handler mHandler;

    private PinnedHeaderExpandableListView expandableListView;
    private ArrayList<MallProvinceBean> provinceList;
    private ArrayList<List<MallCityBean>> cityList;
    private MyexpandableListAdapter adapter;
    private Gson gson;
    private String jsonCity = "[{\"id\":1,\"name\":\"北京市\",\"parentId\":0,\"city\":[{\"id\":35,\"name\":\"北京市\",\"parentId\":1}]},{\"id\":2,\"name\":\"天津市\",\"parentId\":0,\"city\":[{\"id\":36,\"name\":\"天津市\",\"parentId\":2}]},{\"id\":3,\"name\":\"河北省\",\"parentId\":0,\"city\":[{\"id\":37,\"name\":\"石家庄市\",\"parentId\":3},{\"id\":38,\"name\":\"唐山市\",\"parentId\":3},{\"id\":39,\"name\":\"秦皇岛市\",\"parentId\":3},{\"id\":40,\"name\":\"邯郸市\",\"parentId\":3},{\"id\":41,\"name\":\"邢台市\",\"parentId\":3},{\"id\":42,\"name\":\"保定市\",\"parentId\":3},{\"id\":43,\"name\":\"张家口市\",\"parentId\":3},{\"id\":44,\"name\":\"承德市\",\"parentId\":3},{\"id\":45,\"name\":\"沧州市\",\"parentId\":3},{\"id\":46,\"name\":\"廊坊市\",\"parentId\":3},{\"id\":47,\"name\":\"衡水市\",\"parentId\":3}]},{\"id\":4,\"name\":\"山西省\",\"parentId\":0,\"city\":[{\"id\":48,\"name\":\"太原市\",\"parentId\":4},{\"id\":49,\"name\":\"大同市\",\"parentId\":4},{\"id\":50,\"name\":\"阳泉市\",\"parentId\":4},{\"id\":51,\"name\":\"长治市\",\"parentId\":4},{\"id\":52,\"name\":\"晋城市\",\"parentId\":4},{\"id\":53,\"name\":\"朔州市\",\"parentId\":4},{\"id\":54,\"name\":\"晋中市\",\"parentId\":4},{\"id\":55,\"name\":\"运城市\",\"parentId\":4},{\"id\":56,\"name\":\"忻州市\",\"parentId\":4},{\"id\":57,\"name\":\"临汾市\",\"parentId\":4},{\"id\":58,\"name\":\"吕梁市\",\"parentId\":4}]},{\"id\":5,\"name\":\"内蒙古自治区\",\"parentId\":0,\"city\":[{\"id\":59,\"name\":\"呼和浩特市\",\"parentId\":5},{\"id\":60,\"name\":\"包头市\",\"parentId\":5},{\"id\":61,\"name\":\"乌海市\",\"parentId\":5},{\"id\":62,\"name\":\"赤峰市\",\"parentId\":5},{\"id\":63,\"name\":\"通辽市\",\"parentId\":5},{\"id\":64,\"name\":\"鄂尔多斯市\",\"parentId\":5},{\"id\":65,\"name\":\"呼伦贝尔市\",\"parentId\":5},{\"id\":66,\"name\":\"巴彦淖尔市\",\"parentId\":5},{\"id\":67,\"name\":\"乌兰察布市\",\"parentId\":5},{\"id\":68,\"name\":\"兴安盟\",\"parentId\":5},{\"id\":69,\"name\":\"锡林郭勒盟\",\"parentId\":5},{\"id\":70,\"name\":\"阿拉善盟\",\"parentId\":5}]},{\"id\":6,\"name\":\"辽宁省\",\"parentId\":0,\"city\":[{\"id\":71,\"name\":\"沈阳市\",\"parentId\":6},{\"id\":72,\"name\":\"大连市\",\"parentId\":6},{\"id\":73,\"name\":\"鞍山市\",\"parentId\":6},{\"id\":74,\"name\":\"抚顺市\",\"parentId\":6},{\"id\":75,\"name\":\"本溪市\",\"parentId\":6},{\"id\":76,\"name\":\"丹东市\",\"parentId\":6},{\"id\":77,\"name\":\"锦州市\",\"parentId\":6},{\"id\":78,\"name\":\"营口市\",\"parentId\":6},{\"id\":79,\"name\":\"阜新市\",\"parentId\":6},{\"id\":80,\"name\":\"辽阳市\",\"parentId\":6},{\"id\":81,\"name\":\"盘锦市\",\"parentId\":6},{\"id\":82,\"name\":\"铁岭市\",\"parentId\":6},{\"id\":83,\"name\":\"朝阳市\",\"parentId\":6},{\"id\":84,\"name\":\"葫芦岛市\",\"parentId\":6}]},{\"id\":7,\"name\":\"吉林省\",\"parentId\":0,\"city\":[{\"id\":85,\"name\":\"长春市\",\"parentId\":7},{\"id\":86,\"name\":\"吉林市\",\"parentId\":7},{\"id\":87,\"name\":\"四平市\",\"parentId\":7},{\"id\":88,\"name\":\"辽源市\",\"parentId\":7},{\"id\":89,\"name\":\"通化市\",\"parentId\":7},{\"id\":90,\"name\":\"白山市\",\"parentId\":7},{\"id\":91,\"name\":\"松原市\",\"parentId\":7},{\"id\":92,\"name\":\"白城市\",\"parentId\":7},{\"id\":93,\"name\":\"延边朝鲜族自治州\",\"parentId\":7}]},{\"id\":8,\"name\":\"黑龙江省\",\"parentId\":0,\"city\":[{\"id\":94,\"name\":\"哈尔滨市\",\"parentId\":8},{\"id\":95,\"name\":\"齐齐哈尔市\",\"parentId\":8},{\"id\":96,\"name\":\"鸡西市\",\"parentId\":8},{\"id\":97,\"name\":\"鹤岗市\",\"parentId\":8},{\"id\":98,\"name\":\"双鸭山市\",\"parentId\":8},{\"id\":99,\"name\":\"大庆市\",\"parentId\":8},{\"id\":100,\"name\":\"伊春市\",\"parentId\":8},{\"id\":101,\"name\":\"佳木斯市\",\"parentId\":8},{\"id\":102,\"name\":\"七台河市\",\"parentId\":8},{\"id\":103,\"name\":\"牡丹江市\",\"parentId\":8},{\"id\":104,\"name\":\"黑河市\",\"parentId\":8},{\"id\":105,\"name\":\"绥化市\",\"parentId\":8},{\"id\":106,\"name\":\"大兴安岭地区\",\"parentId\":8}]},{\"id\":9,\"name\":\"上海市\",\"parentId\":0,\"city\":[{\"id\":107,\"name\":\"上海市\",\"parentId\":9}]},{\"id\":10,\"name\":\"江苏省\",\"parentId\":0,\"city\":[{\"id\":108,\"name\":\"南京市\",\"parentId\":10},{\"id\":109,\"name\":\"无锡市\",\"parentId\":10},{\"id\":110,\"name\":\"徐州市\",\"parentId\":10},{\"id\":111,\"name\":\"常州市\",\"parentId\":10},{\"id\":112,\"name\":\"苏州市\",\"parentId\":10},{\"id\":113,\"name\":\"南通市\",\"parentId\":10},{\"id\":114,\"name\":\"连云港市\",\"parentId\":10},{\"id\":115,\"name\":\"淮安市\",\"parentId\":10},{\"id\":116,\"name\":\"盐城市\",\"parentId\":10},{\"id\":117,\"name\":\"扬州市\",\"parentId\":10},{\"id\":118,\"name\":\"镇江市\",\"parentId\":10},{\"id\":119,\"name\":\"泰州市\",\"parentId\":10},{\"id\":120,\"name\":\"宿迁市\",\"parentId\":10}]},{\"id\":11,\"name\":\"浙江省\",\"parentId\":0,\"city\":[{\"id\":121,\"name\":\"杭州市\",\"parentId\":11},{\"id\":122,\"name\":\"宁波市\",\"parentId\":11},{\"id\":123,\"name\":\"温州市\",\"parentId\":11},{\"id\":124,\"name\":\"嘉兴市\",\"parentId\":11},{\"id\":125,\"name\":\"湖州市\",\"parentId\":11},{\"id\":126,\"name\":\"绍兴市\",\"parentId\":11},{\"id\":127,\"name\":\"金华市\",\"parentId\":11},{\"id\":128,\"name\":\"衢州市\",\"parentId\":11},{\"id\":129,\"name\":\"舟山市\",\"parentId\":11},{\"id\":130,\"name\":\"台州市\",\"parentId\":11},{\"id\":131,\"name\":\"丽水市\",\"parentId\":11}]},{\"id\":12,\"name\":\"安徽省\",\"parentId\":0,\"city\":[{\"id\":132,\"name\":\"合肥市\",\"parentId\":12},{\"id\":133,\"name\":\"芜湖市\",\"parentId\":12},{\"id\":134,\"name\":\"蚌埠市\",\"parentId\":12},{\"id\":135,\"name\":\"淮南市\",\"parentId\":12},{\"id\":136,\"name\":\"马鞍山市\",\"parentId\":12},{\"id\":137,\"name\":\"淮北市\",\"parentId\":12},{\"id\":138,\"name\":\"铜陵市\",\"parentId\":12},{\"id\":139,\"name\":\"安庆市\",\"parentId\":12},{\"id\":140,\"name\":\"黄山市\",\"parentId\":12},{\"id\":141,\"name\":\"滁州市\",\"parentId\":12},{\"id\":142,\"name\":\"阜阳市\",\"parentId\":12},{\"id\":143,\"name\":\"宿州市\",\"parentId\":12},{\"id\":144,\"name\":\"巢湖市\",\"parentId\":12},{\"id\":145,\"name\":\"六安市\",\"parentId\":12},{\"id\":146,\"name\":\"亳州市\",\"parentId\":12},{\"id\":147,\"name\":\"池州市\",\"parentId\":12},{\"id\":148,\"name\":\"宣城市\",\"parentId\":12}]},{\"id\":13,\"name\":\"福建省\",\"parentId\":0,\"city\":[{\"id\":149,\"name\":\"福州市\",\"parentId\":13},{\"id\":150,\"name\":\"厦门市\",\"parentId\":13},{\"id\":151,\"name\":\"莆田市\",\"parentId\":13},{\"id\":152,\"name\":\"三明市\",\"parentId\":13},{\"id\":153,\"name\":\"泉州市\",\"parentId\":13},{\"id\":154,\"name\":\"漳州市\",\"parentId\":13},{\"id\":155,\"name\":\"南平市\",\"parentId\":13},{\"id\":156,\"name\":\"龙岩市\",\"parentId\":13},{\"id\":157,\"name\":\"宁德市\",\"parentId\":13}]},{\"id\":14,\"name\":\"江西省\",\"parentId\":0,\"city\":[{\"id\":158,\"name\":\"南昌市\",\"parentId\":14},{\"id\":159,\"name\":\"景德镇市\",\"parentId\":14},{\"id\":160,\"name\":\"萍乡市\",\"parentId\":14},{\"id\":161,\"name\":\"九江市\",\"parentId\":14},{\"id\":162,\"name\":\"新余市\",\"parentId\":14},{\"id\":163,\"name\":\"鹰潭市\",\"parentId\":14},{\"id\":164,\"name\":\"赣州市\",\"parentId\":14},{\"id\":165,\"name\":\"吉安市\",\"parentId\":14},{\"id\":166,\"name\":\"宜春市\",\"parentId\":14},{\"id\":167,\"name\":\"抚州市\",\"parentId\":14},{\"id\":168,\"name\":\"上饶市\",\"parentId\":14}]},{\"id\":15,\"name\":\"山东省\",\"parentId\":0,\"city\":[{\"id\":169,\"name\":\"济南市\",\"parentId\":15},{\"id\":170,\"name\":\"青岛市\",\"parentId\":15},{\"id\":171,\"name\":\"淄博市\",\"parentId\":15},{\"id\":172,\"name\":\"枣庄市\",\"parentId\":15},{\"id\":173,\"name\":\"东营市\",\"parentId\":15},{\"id\":174,\"name\":\"烟台市\",\"parentId\":15},{\"id\":175,\"name\":\"潍坊市\",\"parentId\":15},{\"id\":176,\"name\":\"济宁市\",\"parentId\":15},{\"id\":177,\"name\":\"泰安市\",\"parentId\":15},{\"id\":178,\"name\":\"威海市\",\"parentId\":15},{\"id\":179,\"name\":\"日照市\",\"parentId\":15},{\"id\":180,\"name\":\"莱芜市\",\"parentId\":15},{\"id\":181,\"name\":\"临沂市\",\"parentId\":15},{\"id\":182,\"name\":\"德州市\",\"parentId\":15},{\"id\":183,\"name\":\"聊城市\",\"parentId\":15},{\"id\":184,\"name\":\"滨州市\",\"parentId\":15},{\"id\":185,\"name\":\"荷泽市\",\"parentId\":15}]},{\"id\":16,\"name\":\"河南省\",\"parentId\":0,\"city\":[{\"id\":186,\"name\":\"郑州市\",\"parentId\":16},{\"id\":187,\"name\":\"开封市\",\"parentId\":16},{\"id\":188,\"name\":\"洛阳市\",\"parentId\":16},{\"id\":189,\"name\":\"平顶山市\",\"parentId\":16},{\"id\":190,\"name\":\"安阳市\",\"parentId\":16},{\"id\":191,\"name\":\"鹤壁市\",\"parentId\":16},{\"id\":192,\"name\":\"新乡市\",\"parentId\":16},{\"id\":193,\"name\":\"焦作市\",\"parentId\":16},{\"id\":194,\"name\":\"濮阳市\",\"parentId\":16},{\"id\":195,\"name\":\"许昌市\",\"parentId\":16},{\"id\":196,\"name\":\"漯河市\",\"parentId\":16},{\"id\":197,\"name\":\"三门峡市\",\"parentId\":16},{\"id\":198,\"name\":\"南阳市\",\"parentId\":16},{\"id\":199,\"name\":\"商丘市\",\"parentId\":16},{\"id\":200,\"name\":\"信阳市\",\"parentId\":16},{\"id\":201,\"name\":\"周口市\",\"parentId\":16},{\"id\":202,\"name\":\"驻马店市\",\"parentId\":16}]},{\"id\":17,\"name\":\"湖北省\",\"parentId\":0,\"city\":[{\"id\":203,\"name\":\"武汉市\",\"parentId\":17},{\"id\":204,\"name\":\"黄石市\",\"parentId\":17},{\"id\":205,\"name\":\"十堰市\",\"parentId\":17},{\"id\":206,\"name\":\"宜昌市\",\"parentId\":17},{\"id\":207,\"name\":\"襄樊市\",\"parentId\":17},{\"id\":208,\"name\":\"鄂州市\",\"parentId\":17},{\"id\":209,\"name\":\"荆门市\",\"parentId\":17},{\"id\":210,\"name\":\"孝感市\",\"parentId\":17},{\"id\":211,\"name\":\"荆州市\",\"parentId\":17},{\"id\":212,\"name\":\"黄冈市\",\"parentId\":17},{\"id\":213,\"name\":\"咸宁市\",\"parentId\":17},{\"id\":214,\"name\":\"随州市\",\"parentId\":17},{\"id\":215,\"name\":\"恩施土家族苗族自治州\",\"parentId\":17},{\"id\":216,\"name\":\"神农架\",\"parentId\":17}]},{\"id\":18,\"name\":\"湖南省\",\"parentId\":0,\"city\":[{\"id\":217,\"name\":\"长沙市\",\"parentId\":18},{\"id\":218,\"name\":\"株洲市\",\"parentId\":18},{\"id\":219,\"name\":\"湘潭市\",\"parentId\":18},{\"id\":220,\"name\":\"衡阳市\",\"parentId\":18},{\"id\":221,\"name\":\"邵阳市\",\"parentId\":18},{\"id\":222,\"name\":\"岳阳市\",\"parentId\":18},{\"id\":223,\"name\":\"常德市\",\"parentId\":18},{\"id\":224,\"name\":\"张家界市\",\"parentId\":18},{\"id\":225,\"name\":\"益阳市\",\"parentId\":18},{\"id\":226,\"name\":\"郴州市\",\"parentId\":18},{\"id\":227,\"name\":\"永州市\",\"parentId\":18},{\"id\":228,\"name\":\"怀化市\",\"parentId\":18},{\"id\":229,\"name\":\"娄底市\",\"parentId\":18},{\"id\":230,\"name\":\"湘西土家族苗族自治州\",\"parentId\":18}]},{\"id\":19,\"name\":\"广东省\",\"parentId\":0,\"city\":[{\"id\":231,\"name\":\"广州市\",\"parentId\":19},{\"id\":232,\"name\":\"韶关市\",\"parentId\":19},{\"id\":233,\"name\":\"深圳市\",\"parentId\":19},{\"id\":234,\"name\":\"珠海市\",\"parentId\":19},{\"id\":235,\"name\":\"汕头市\",\"parentId\":19},{\"id\":236,\"name\":\"佛山市\",\"parentId\":19},{\"id\":237,\"name\":\"江门市\",\"parentId\":19},{\"id\":238,\"name\":\"湛江市\",\"parentId\":19},{\"id\":239,\"name\":\"茂名市\",\"parentId\":19},{\"id\":240,\"name\":\"肇庆市\",\"parentId\":19},{\"id\":241,\"name\":\"惠州市\",\"parentId\":19},{\"id\":242,\"name\":\"梅州市\",\"parentId\":19},{\"id\":243,\"name\":\"汕尾市\",\"parentId\":19},{\"id\":244,\"name\":\"河源市\",\"parentId\":19},{\"id\":245,\"name\":\"阳江市\",\"parentId\":19},{\"id\":246,\"name\":\"清远市\",\"parentId\":19},{\"id\":247,\"name\":\"东莞市\",\"parentId\":19},{\"id\":248,\"name\":\"中山市\",\"parentId\":19},{\"id\":249,\"name\":\"潮州市\",\"parentId\":19},{\"id\":250,\"name\":\"揭阳市\",\"parentId\":19},{\"id\":251,\"name\":\"云浮市\",\"parentId\":19}]},{\"id\":20,\"name\":\"广西壮族自治区\",\"parentId\":0,\"city\":[{\"id\":252,\"name\":\"南宁市\",\"parentId\":20},{\"id\":253,\"name\":\"柳州市\",\"parentId\":20},{\"id\":254,\"name\":\"桂林市\",\"parentId\":20},{\"id\":255,\"name\":\"梧州市\",\"parentId\":20},{\"id\":256,\"name\":\"北海市\",\"parentId\":20},{\"id\":257,\"name\":\"防城港市\",\"parentId\":20},{\"id\":258,\"name\":\"钦州市\",\"parentId\":20},{\"id\":259,\"name\":\"贵港市\",\"parentId\":20},{\"id\":260,\"name\":\"玉林市\",\"parentId\":20},{\"id\":261,\"name\":\"百色市\",\"parentId\":20},{\"id\":262,\"name\":\"贺州市\",\"parentId\":20},{\"id\":263,\"name\":\"河池市\",\"parentId\":20},{\"id\":264,\"name\":\"来宾市\",\"parentId\":20},{\"id\":265,\"name\":\"崇左市\",\"parentId\":20}]},{\"id\":21,\"name\":\"海南省\",\"parentId\":0,\"city\":[{\"id\":266,\"name\":\"海口市\",\"parentId\":21},{\"id\":267,\"name\":\"三亚市\",\"parentId\":21}]},{\"id\":22,\"name\":\"重庆市\",\"parentId\":0,\"city\":[{\"id\":268,\"name\":\"重庆市\",\"parentId\":22}]},{\"id\":23,\"name\":\"四川省\",\"parentId\":0,\"city\":[{\"id\":269,\"name\":\"成都市\",\"parentId\":23},{\"id\":270,\"name\":\"自贡市\",\"parentId\":23},{\"id\":271,\"name\":\"攀枝花市\",\"parentId\":23},{\"id\":272,\"name\":\"泸州市\",\"parentId\":23},{\"id\":273,\"name\":\"德阳市\",\"parentId\":23},{\"id\":274,\"name\":\"绵阳市\",\"parentId\":23},{\"id\":275,\"name\":\"广元市\",\"parentId\":23},{\"id\":276,\"name\":\"遂宁市\",\"parentId\":23},{\"id\":277,\"name\":\"内江市\",\"parentId\":23},{\"id\":278,\"name\":\"乐山市\",\"parentId\":23},{\"id\":279,\"name\":\"南充市\",\"parentId\":23},{\"id\":280,\"name\":\"眉山市\",\"parentId\":23},{\"id\":281,\"name\":\"宜宾市\",\"parentId\":23},{\"id\":282,\"name\":\"广安市\",\"parentId\":23},{\"id\":283,\"name\":\"达州市\",\"parentId\":23},{\"id\":284,\"name\":\"雅安市\",\"parentId\":23},{\"id\":285,\"name\":\"巴中市\",\"parentId\":23},{\"id\":286,\"name\":\"??阳市\",\"parentId\":23},{\"id\":287,\"name\":\"阿坝藏族羌族自治州\",\"parentId\":23},{\"id\":288,\"name\":\"甘孜藏族自治州\",\"parentId\":23},{\"id\":289,\"name\":\"凉山彝族自治州\",\"parentId\":23}]},{\"id\":27,\"name\":\"陕西省\",\"parentId\":0,\"city\":[{\"id\":322,\"name\":\"西安市\",\"parentId\":27},{\"id\":323,\"name\":\"铜川市\",\"parentId\":27},{\"id\":324,\"name\":\"宝鸡市\",\"parentId\":27},{\"id\":325,\"name\":\"咸阳市\",\"parentId\":27},{\"id\":326,\"name\":\"渭南市\",\"parentId\":27},{\"id\":327,\"name\":\"延安市\",\"parentId\":27},{\"id\":328,\"name\":\"汉中市\",\"parentId\":27},{\"id\":329,\"name\":\"榆林市\",\"parentId\":27},{\"id\":330,\"name\":\"安康市\",\"parentId\":27},{\"id\":331,\"name\":\"商洛市\",\"parentId\":27}]}]";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null)
        {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        View root = inflater.inflate(R.layout.user_city, container, false);
        expandableListView = (PinnedHeaderExpandableListView) root.findViewById(R.id.expandablelist);
        expandableListView.setOnChildClickListener(this);
        provinceList = (ArrayList<MallProvinceBean>) gson.fromJson(jsonCity, (new TypeToken<ArrayList<MallProvinceBean>>(){}).getType());
        cityList = new ArrayList<List<MallCityBean>>();
        int size = provinceList.size();
        for(int i = 0; i<size; i++)
        {
            MallProvinceBean bean = provinceList.get(i);
            cityList.add(bean.getCity());
        }
        adapter = new MyexpandableListAdapter(getActivity());
        expandableListView.setAdapter(adapter);
        expandableListView.setOnHeaderUpdateListener(this);
        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHandler = ((UserInfoActivity)activity).mHandler;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {

            int fullWidth = getDialog().getWindow().getAttributes().width;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                fullWidth = size.x;
            } else {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                fullWidth = display.getWidth();
            }

            final int padding = getResources().getDimensionPixelOffset(R.dimen.dialogfragment_marginleft);

            int w = fullWidth-100;
            int h = getDialog().getWindow().getAttributes().height;

            getDialog().getWindow().setLayout(w, h);
        }
    }

    @Override
    public View getPinnedHeader() {
        View headerView = (ViewGroup) getActivity().getLayoutInflater().inflate(
                R.layout.mall_province_item, null);
        headerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        return headerView;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
        MallProvinceBean firstVisibleGroup = (MallProvinceBean) adapter
                .getGroup(firstVisibleGroupPos);
        TextView textView = (TextView) headerView.findViewById(R.id.province);
        textView.setText(firstVisibleGroup.getName());
    }

    @Override
    public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2,
                                long arg3) {
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        MallProvinceBean provinceBean = provinceList.get(groupPosition);
        MallCityBean bean = cityList.get(groupPosition).get(childPosition);
        String cityname = bean.getName();
        String provincename = provinceBean.getName();
        Message msg = mHandler.obtainMessage(UserInfoActivity.MSG_CHANGE_REGION);
        Bundle bundle = new Bundle();
        bundle.putString("province", provincename);
        bundle.putString("city", cityname);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        this.dismiss();
        return false;
    }

    class MyexpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private LayoutInflater inflater;

        public MyexpandableListAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        // 返回父列表个数
        @Override
        public int getGroupCount() {
            return provinceList.size();
        }

        // 返回子列表个数
        @Override
        public int getChildrenCount(int groupPosition) {
            return cityList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {

            return provinceList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return cityList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {

            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            ProvinceHolder provinceHolder = null;
            if (convertView == null) {
                provinceHolder = new ProvinceHolder();
                convertView = inflater.inflate(R.layout.mall_province_item,
                        null);
                provinceHolder.province = (TextView) convertView
                        .findViewById(R.id.province);
                provinceHolder.imgProvince = (ImageView) convertView
                        .findViewById(R.id.imgProvince);
                convertView.setTag(provinceHolder);
            } else {
                provinceHolder = (ProvinceHolder) convertView.getTag();
            }

            provinceHolder.province
                    .setText(((MallProvinceBean) getGroup(groupPosition))
                            .getName());
            // if (isExpanded)// ture is Expanded or false is not isExpanded
            // groupHolder.ivPic.setImageResource(R.drawable.expanded);
            // else
            // groupHolder.ivPic.setImageResource(R.drawable.collapse);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            CityHolder cityHolder = null;
            if (convertView == null) {
                cityHolder = new CityHolder();
                convertView = inflater.inflate(R.layout.mall_city_item, null);

                cityHolder.city = (TextView) convertView
                        .findViewById(R.id.city);
                convertView.setTag(cityHolder);
            } else {
                cityHolder = (CityHolder) convertView.getTag();
            }

            cityHolder.city.setText(((MallCityBean) getChild(groupPosition,
                    childPosition)).getName());
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    class ProvinceHolder {
        private TextView province;
        private ImageView imgProvince;
    }

    class CityHolder {
        private TextView city;
    }
}